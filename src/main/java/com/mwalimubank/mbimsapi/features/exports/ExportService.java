package com.mwalimubank.mbimsapi.features.exports;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.mwalimubank.mbimsapi.features.administration.department.dto.DepartmentResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.EmployeeResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.position.dto.PositionResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.unit.dto.UnitResponseDTO;
import com.mwalimubank.mbimsapi.features.asset_management.assetcategory.dto.AssetCategoryResponseDTO;
import com.mwalimubank.mbimsapi.features.fetch_data.FetchDataService;
import com.mwalimubank.mbimsapi.features.role.dto.RoleResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mwalimubank.mbimsapi.features.exports.ExportFormatEnum.*;

/**
 * Single source of truth for all exports.
 * To add a new export: add one register(...) call in the constructor.
 */
@Slf4j
@Service
public class ExportService {

    private static final int MAX_ROWS = 5000;
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ---------- Definition model ----------

    /** One exportable column: id (used for sorting), header text, and how to read the value. */
    public record Column<T>(String id, String header, Function<T, Object> value) {}

    /** One export: title, where the rows come from, and which columns to print. */
    public record ExportDefinition<T>(String title, Supplier<List<T>> source, List<Column<T>> columns) {}

    private final Map<String, ExportDefinition<?>> registry = new LinkedHashMap<>();

    // ---------- Register all exports here ----------

    public ExportService(FetchDataService fetch) {

        register("departments", "Departments", fetch::fetchDepartments, List.of(
                col("name", "Name", DepartmentResponseDTO::getName),
                col("approvalStatus", "Status", DepartmentResponseDTO::getApprovalStatus)
        ));

        register("roles", "Roles", fetch::fetchRoles, List.of(
                col("name", "Name", RoleResponseDTO::getName),
                col("approvalStatus", "Status", RoleResponseDTO::getApprovalStatus)
        ));

        register("units", "Units", fetch::fetchUnits, List.of(
                col("name", "Name", UnitResponseDTO::getName),
                col("approvalStatus", "Status", UnitResponseDTO::getApprovalStatus)
        ));

        register("positions", "Positions", fetch::fetchPositions, List.of(
                col("name", "Name", PositionResponseDTO::getName),
                col("approvalStatus", "Status", PositionResponseDTO::getApprovalStatus)
        ));

        register("asset-categories", "Asset Categories", fetch::fetchAssetCategories, List.of(
                col("name", "Name", AssetCategoryResponseDTO::getName),
                col("approvalStatus", "Status", AssetCategoryResponseDTO::getApprovalStatus)
        ));

        register("employees", "Employees", fetch::fetchEmployees, List.of(
                col("firstName", "First Name", EmployeeResponseDTO::getFirstName),
                col("lastName", "Last Name", EmployeeResponseDTO::getLastName),
                col("email", "Email", EmployeeResponseDTO::getEmail),
                col("approvalStatus", "Status", EmployeeResponseDTO::getApprovalStatus)
        ));
    }

    public byte[] export(String key, ExportFormatEnum format, String filterKey, String sortBy, String sortDirection) {
        return build(definition(key), format, filterKey, sortBy, sortDirection);
    }

    private <T> byte[] build(ExportDefinition<T> def, ExportFormatEnum format,
                             String filterKey, String sortBy, String sortDirection) {
        List<T> rows = prepareRows(def, filterKey, sortBy, sortDirection);
        return switch (format) {
            case PDF -> renderPdf(def, rows);          // your existing PDF method, unchanged
            case CSV -> renderCsv(def, rows);
            case XLSX -> renderXlsx(def, rows);
        };
    }

    /** Search, sort and guard: the same for every format. */
    private <T> List<T> prepareRows(ExportDefinition<T> def, String filterKey, String sortBy, String sortDirection) {
        List<T> rows = new ArrayList<>(def.source().get());

        if (filterKey != null && !filterKey.isBlank()) {
            String needle = filterKey.trim().toLowerCase();
            rows.removeIf(r -> def.columns().stream()
                    .noneMatch(c -> text(c.value().apply(r)).toLowerCase().contains(needle)));
        }

        if (sortBy != null && !sortBy.isBlank()) {
            def.columns().stream().filter(c -> c.id().equals(sortBy)).findFirst().ifPresent(c -> {
                Comparator<T> cmp = Comparator.comparing(
                        r -> text(c.value().apply(r)), String.CASE_INSENSITIVE_ORDER);
                rows.sort("desc".equalsIgnoreCase(sortDirection) ? cmp.reversed() : cmp);
            });
        }

        if (rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No data to export");
        }
        if (rows.size() > MAX_ROWS) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Too many rows (" + rows.size() + "). Narrow your search to " + MAX_ROWS + " or fewer.");
        }
        return rows;
    }

    // ---------- Internals ----------

    private <T> void register(String key, String title, Supplier<List<T>> source, List<Column<T>> columns) {
        registry.put(key, new ExportDefinition<>(title, source, columns));
    }

    private static <T> Column<T> col(String id, String header, Function<T, Object> value) {
        return new Column<>(id, header, value);
    }

    private ExportDefinition<?> definition(String key) {
        ExportDefinition<?> def = registry.get(key);
        if (def == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown export: " + key);
        }
        return def;
    }

    private <T> byte[] renderPdf(ExportDefinition<T> def, List<T> rows) {
        List<Column<T>> cols = def.columns();
        Rectangle page = cols.size() > 6 ? PageSize.A4.rotate() : PageSize.A4;

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(page, 36, 36, 40, 40);

        try {
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new PageNumberEvent());
            doc.open();

            doc.add(new Paragraph(def.title(), titleFont));
            Paragraph meta = new Paragraph(
                    "Generated " + LocalDateTime.now().format(STAMP) + "  |  " + rows.size() + " records", metaFont);
            meta.setSpacingAfter(10);
            doc.add(meta);

            float[] widths = new float[cols.size() + 1];
            widths[0] = 0.5f;                                   // S/N column is narrow
            Arrays.fill(widths, 1, widths.length, 1f);

            PdfPTable table = new PdfPTable(widths);
            table.setWidthPercentage(100);
            table.setHeaderRows(1);                             // repeat header on every page

            addHeader(table, "S/N", headFont);
            cols.forEach(c -> addHeader(table, c.header(), headFont));

            int i = 1;
            for (T row : rows) {
                boolean zebra = i % 2 == 0;
                addCell(table, String.valueOf(i++), bodyFont, zebra);
                for (Column<T> c : cols) {
                    addCell(table, text(c.value().apply(row)), bodyFont, zebra);
                }
            }

            doc.add(table);
            doc.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate PDF", e);
        }
    }

    private <T> byte[] renderCsv(ExportDefinition<T> def, List<T> rows) {
        List<Column<T>> cols = def.columns();

        StringBuilder sb = new StringBuilder("\uFEFF");   // BOM so Excel reads UTF-8 correctly

        sb.append("S/N");
        cols.forEach(c -> sb.append(',').append(csv(c.header())));
        sb.append("\r\n");

        int i = 1;
        for (T row : rows) {
            sb.append(i++);
            for (Column<T> c : cols) {
                sb.append(',').append(csv(c.value().apply(row)));
            }
            sb.append("\r\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String csv(Object value) {
        String s = text(value);
        // Prevent formula injection when opened in Excel (strings only, so real numbers are untouched)
        if (value instanceof String && !s.isEmpty() && "=+-@\t\r".indexOf(s.charAt(0)) >= 0) {
            s = "'" + s;
        }
        return (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r"))
                ? "\"" + s.replace("\"", "\"\"") + "\""
                : s;
    }

    private <T> byte[] renderXlsx(ExportDefinition<T> def, List<T> rows) {
        List<Column<T>> cols = def.columns();
        long started = System.currentTimeMillis();

        // SXSSF streams rows to disk, so memory stays low
        try (SXSSFWorkbook wb = new SXSSFWorkbook(100);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String sheetName = def.title().replaceAll("[\\\\/*?:\\[\\]]", "").trim();
            if (sheetName.isEmpty()) sheetName = "Data";
            Sheet sheet = wb.createSheet(sheetName);

            org.apache.poi.ss.usermodel.Font bold = wb.createFont();
            bold.setBold(true);
            CellStyle head = wb.createCellStyle();
            head.setFont(bold);
            head.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            head.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int[] maxLen = new int[cols.size() + 1];

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("S/N");
            header.getCell(0).setCellStyle(head);
            maxLen[0] = 4;
            for (int c = 0; c < cols.size(); c++) {
                var cell = header.createCell(c + 1);
                cell.setCellValue(cols.get(c).header());
                cell.setCellStyle(head);
                maxLen[c + 1] = cols.get(c).header().length();
            }
            sheet.createFreezePane(0, 1);

            int r = 1;
            for (T item : rows) {
                Row row = sheet.createRow(r);
                row.createCell(0).setCellValue(r);
                for (int c = 0; c < cols.size(); c++) {
                    Object v = cols.get(c).value().apply(item);
                    var cell = row.createCell(c + 1);
                    if (v instanceof Number n) cell.setCellValue(n.doubleValue());   // real numeric cell
                    else cell.setCellValue(text(v));
                    if (r <= 200) maxLen[c + 1] = Math.max(maxLen[c + 1], text(v).length());
                }
                r++;
            }

            // Manual widths: avoids autoSizeColumn, which needs AWT fonts on the server
            for (int c = 0; c < maxLen.length; c++) {
                sheet.setColumnWidth(c, Math.min(Math.max(maxLen[c] + 2, 10), 50) * 256);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("XLSX export '{}': failed while writing workbook", def.title(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate Excel file", e);
        } catch (RuntimeException e) {
            log.error("XLSX export '{}': unexpected error", def.title(), e);
            throw e;
        }
    }

    private static void addHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(55, 65, 81));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addCell(PdfPTable table, String text, Font font, boolean zebra) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        cell.setBorderColor(new Color(209, 213, 219));
        if (zebra) cell.setBackgroundColor(new Color(243, 244, 246));
        table.addCell(cell);
    }

    private static String text(Object value) {
        if (value == null) return "";
        if (value instanceof Boolean b) return b ? "Yes" : "No";
        if (value instanceof LocalDateTime dt) return dt.format(STAMP);
        return value.toString();
    }

    /** Draws "Page N" at the bottom right of every page. */
    private static class PageNumberEvent extends PdfPageEventHelper {
        private final Font font = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(
                    writer.getDirectContent(), Element.ALIGN_RIGHT,
                    new Phrase("Page " + writer.getPageNumber(), font),
                    document.right(), document.bottom() - 15, 0);
        }
    }
}