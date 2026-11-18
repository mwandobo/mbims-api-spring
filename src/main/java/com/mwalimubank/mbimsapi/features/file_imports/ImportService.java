package com.mwalimubank.mbimsapi.features.file_imports;

import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentRepository;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentService;
import com.mwalimubank.mbimsapi.features.administration.department.dto.CreateDepartmentDTO;
import com.mwalimubank.mbimsapi.features.administration.position.PositionRepository;
import com.mwalimubank.mbimsapi.features.administration.position.PositionService;
import com.mwalimubank.mbimsapi.features.administration.position.dto.CreatePositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Single source of truth for all imports.
 * To add a new import: add one register(...) call in the constructor.
 */
@Slf4j
@Service
public class ImportService {

    private static final int MAX_ROWS = 5000;

    // ---------- Definition model ----------

    /** One column: key (used in the row map), header (as in the file), required flag, optional per-value check. */
    public record Column(String key, String header, boolean required, Function<String, String> validator) {}

    /**
     * One import:
     * - duplicateKey: optional, identifies a row so duplicates inside the same file are caught.
     * - rowValidator: optional, for checks that need the database. Returns an error message or null.
     * - saver: persists one validated row. Runs inside a single transaction.
     */
    public record ImportDefinition(String title,
                                   List<Column> columns,
                                   Function<Map<String, String>, String> duplicateKey,
                                   Function<Map<String, String>, String> rowValidator,
                                   Consumer<Map<String, String>> saver) {}

    public record RowError(int row, String column, String message) {}

    public record ImportResult(boolean dryRun, int totalRows, int imported, List<RowError> errors) {}

    private record ParsedRow(int rowNumber, Map<String, String> values) {}

    private final Map<String, ImportDefinition> registry = new LinkedHashMap<>();
    private final TransactionTemplate tx;

    // ---------- Register all imports here ----------

    public ImportService(PlatformTransactionManager txManager,
                         DepartmentService departmentService,
                         PositionService positionService,
                         DepartmentRepository departmentRepository,
                         PositionRepository positionRepository) {
        this.tx = new TransactionTemplate(txManager);

        // ----- Departments: Name, Description -----
        register("departments", "Departments",
                List.of(
                        required("name", "Name", maxLength(100)),
                        optional("description", "Description", maxLength(255))
                ),
                row -> norm(row.get("name")),
                row -> departmentRepository.findByName(row.get("name")).isPresent()
                        ? "Department '" + row.get("name") + "' already exists" : null,
                row -> {
                    CreateDepartmentDTO dto = new CreateDepartmentDTO();
                    dto.setName(row.get("name"));
                    dto.setDescription(blankToNull(row.get("description")));
                    departmentService.create(dto);
                });

        // ----- Positions: Name, Description, Department (by name) -----
        register("positions", "Positions",
                List.of(
                        required("name", "Name", maxLength(100)),
                        optional("description", "Description", maxLength(255)),
                        required("department", "Department", null)
                ),
                row -> norm(row.get("department")) + "|" + norm(row.get("name")),
                row -> {
                    var dept = departmentRepository.findByName(row.get("department"));
                    if (dept.isEmpty()) {
                        return "Department '" + row.get("department") + "' not found";
                    }
                    return positionRepository
                            .findByNameAndDepartmentId(row.get("name"), dept.get().getId()).isPresent()
                            ? "Position '" + row.get("name") + "' already exists in " + row.get("department")
                            : null;
                },
                row -> {
                    DepartmentEntity dept = departmentRepository.findByName(row.get("department"))
                            .orElseThrow(() -> new IllegalStateException(
                                    "Department '" + row.get("department") + "' not found"));
                    CreatePositionDTO dto = new CreatePositionDTO();
                    dto.setName(row.get("name"));
                    dto.setDescription(blankToNull(row.get("description")));
                    dto.setDepartmentId(dept.getId());
                    positionService.create(dto);
                });
    }

    // ---------- Column helpers ----------

    public static Column required(String key, String header, Function<String, String> validator) {
        return new Column(key, header, true, validator);
    }

    public static Column optional(String key, String header, Function<String, String> validator) {
        return new Column(key, header, false, validator);
    }

    public static Function<String, String> maxLength(int max) {
        return v -> v.length() > max ? "Must be at most " + max + " characters" : null;
    }

    public static Function<String, String> email() {
        return v -> v.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") ? null : "Invalid email address";
    }

    private static String norm(String v) {
        return v == null ? "" : v.trim().toLowerCase();
    }

    private static String blankToNull(String v) {
        return v == null || v.isBlank() ? null : v;
    }

    // ---------- Public API ----------

    public ImportResult importFile(String key, MultipartFile file, boolean dryRun) {
        ImportDefinition def = definition(key);
        List<ParsedRow> rows = parse(file, def);
        return process(def, rows, dryRun);
    }

    public byte[] template(String key, boolean xlsx) {
        ImportDefinition def = definition(key);
        List<String> headers = def.columns().stream().map(Column::header).toList();

        if (!xlsx) {
            String line = String.join(",", headers) + "\r\n";
            return ("\uFEFF" + line).getBytes(StandardCharsets.UTF_8);
        }
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet(def.title().replaceAll("[\\\\/*?:\\[\\]]", ""));
            Font bold = wb.createFont();
            bold.setBold(true);
            CellStyle style = wb.createCellStyle();
            style.setFont(bold);
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell c = header.createCell(i);
                c.setCellValue(headers.get(i));
                c.setCellStyle(style);
                sheet.setColumnWidth(i, Math.max(headers.get(i).length() + 4, 14) * 256);
            }
            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate template", e);
        }
    }

    public String titleOf(String key) {
        return definition(key).title();
    }

    // ---------- Internals ----------

    private void register(String key, String title, List<Column> columns,
                          Function<Map<String, String>, String> duplicateKey,
                          Function<Map<String, String>, String> rowValidator,
                          Consumer<Map<String, String>> saver) {
        registry.put(key, new ImportDefinition(title, columns, duplicateKey, rowValidator, saver));
    }

    private ImportDefinition definition(String key) {
        ImportDefinition def = registry.get(key);
        if (def == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown import: " + key);
        }
        return def;
    }

    // ----- Parsing -----

    private List<ParsedRow> parse(MultipartFile file, ImportDefinition def) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file uploaded");
        }
        String name = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase();

        List<List<String>> raw;
        try {
            if (name.endsWith(".csv")) raw = readCsv(file);
            else if (name.endsWith(".xlsx")) raw = readXlsx(file);
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Unsupported file type. Upload a .csv or .xlsx file");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read the file. Is it corrupted?", e);
        }

        if (raw.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file is empty");
        }

        // Map file headers (case-insensitive) to column positions
        List<String> fileHeaders = raw.get(0);
        Map<String, Integer> position = new HashMap<>();
        for (int i = 0; i < fileHeaders.size(); i++) {
            position.putIfAbsent(fileHeaders.get(i).trim().toLowerCase(), i);
        }

        List<String> missing = def.columns().stream()
                .filter(c -> !position.containsKey(c.header().toLowerCase()))
                .map(Column::header).toList();
        if (!missing.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Missing column(s): " + String.join(", ", missing) + ". Download the template for the correct headers");
        }

        List<ParsedRow> rows = new ArrayList<>();
        for (int i = 1; i < raw.size(); i++) {
            List<String> cells = raw.get(i);
            if (cells.stream().allMatch(s -> s == null || s.isBlank())) continue;   // skip blank rows, keep numbering

            Map<String, String> values = new HashMap<>();
            for (Column c : def.columns()) {
                int idx = position.get(c.header().toLowerCase());
                String v = idx < cells.size() && cells.get(idx) != null ? cells.get(idx).trim() : "";
                values.put(c.key(), v);
            }
            rows.add(new ParsedRow(i + 1, values));   // i + 1 = the row number the user sees in Excel
        }

        if (rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file has a header but no data rows");
        }
        if (rows.size() > MAX_ROWS) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Too many rows (" + rows.size() + "). Split the file into parts of " + MAX_ROWS + " rows or fewer");
        }
        return rows;
    }

    private List<List<String>> readCsv(MultipartFile file) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            br.mark(1);
            if (br.read() != 0xFEFF) br.reset();          // skip the UTF-8 BOM if present

            // Excel in some regions saves CSV with ';' instead of ','. Detect it from the header line.
            br.mark(16384);
            String first = br.readLine();
            br.reset();
            if (first == null) return List.of();
            long semis = first.chars().filter(ch -> ch == ';').count();
            long commas = first.chars().filter(ch -> ch == ',').count();
            char delimiter = semis > commas ? ';' : ',';

            CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter(delimiter).setTrim(true).build();
            List<List<String>> out = new ArrayList<>();
            try (CSVParser parser = format.parse(br)) {
                for (CSVRecord rec : parser) {
                    List<String> cells = new ArrayList<>();
                    rec.forEach(cells::add);
                    out.add(cells);
                }
            }
            return out;
        }
    }

    private List<List<String>> readXlsx(MultipartFile file) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

            List<List<String>> out = new ArrayList<>();
            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                List<String> cells = new ArrayList<>();
                if (row != null) {
                    for (int c = 0; c < row.getLastCellNum(); c++) {
                        Cell cell = row.getCell(c);
                        cells.add(cell == null ? "" : fmt.formatCellValue(cell, evaluator));
                    }
                }
                out.add(cells);
            }
            return out;
        }
    }

    // ----- Validate, then save -----

    private ImportResult process(ImportDefinition def, List<ParsedRow> rows, boolean dryRun) {
        List<RowError> errors = new ArrayList<>();
        Map<String, Integer> seen = new HashMap<>();

        for (ParsedRow row : rows) {
            boolean columnsOk = true;
            for (Column c : def.columns()) {
                String v = row.values().get(c.key());
                if (v.isEmpty()) {
                    if (c.required()) {
                        errors.add(new RowError(row.rowNumber(), c.header(), "Required"));
                        columnsOk = false;
                    }
                } else if (c.validator() != null) {
                    String msg = c.validator().apply(v);
                    if (msg != null) {
                        errors.add(new RowError(row.rowNumber(), c.header(), msg));
                        columnsOk = false;
                    }
                }
            }

            // Duplicate inside the same file
            if (columnsOk && def.duplicateKey() != null) {
                Integer first = seen.putIfAbsent(def.duplicateKey().apply(row.values()), row.rowNumber());
                if (first != null) {
                    errors.add(new RowError(row.rowNumber(), null, "Duplicate of row " + first + " in this file"));
                    columnsOk = false;
                }
            }

            // Checks that need the database
            if (columnsOk && def.rowValidator() != null) {
                String msg = def.rowValidator().apply(row.values());
                if (msg != null) errors.add(new RowError(row.rowNumber(), null, msg));
            }
        }

        if (!errors.isEmpty() || dryRun) {
            return new ImportResult(dryRun, rows.size(), 0, errors);
        }

        // Nothing is saved unless every row saves: one transaction, rolled back on any failure
        int[] current = {0};
        try {
            tx.executeWithoutResult(status -> {
                for (ParsedRow row : rows) {
                    current[0] = row.rowNumber();
                    def.saver().accept(row.values());
                }
            });
        } catch (RuntimeException e) {
            log.error("Import '{}' failed at row {}", def.title(), current[0], e);
            String reason = (e instanceof IllegalStateException || e instanceof IllegalArgumentException)
                    && e.getMessage() != null ? e.getMessage() : "Could not save this row";
            errors.add(new RowError(current[0], null, reason + ". No rows were imported"));
            return new ImportResult(false, rows.size(), 0, errors);
        }

        log.info("Import '{}': {} rows imported", def.title(), rows.size());
        return new ImportResult(false, rows.size(), rows.size(), List.of());
    }
}