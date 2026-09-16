package com.mwalimubank.mbimsapi.features.reconciliation;

import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.features.common.PageSpecs;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;
import com.mwalimubank.mbimsapi.features.recon.reconciliation_item.ReconciliationItemEntity;
import com.mwalimubank.mbimsapi.features.recon.reconciliation_item.ReconciliationItemRepository;
import com.mwalimubank.mbimsapi.features.reconciliation.dto.ReconciliationResponseDTO;
import com.mwalimubank.mbimsapi.features.reconciliation.entity.ReconciliationEntity;
import com.mwalimubank.mbimsapi.features.reconciliation.repository.ReconciliationRepository;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
import com.mwalimubank.mbimsapi.features.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReconciliationService {
    private final ReconciliationRepository repository;
    private final UserRepository userRepository;
    private final ReconciliationHelper helper;
    private final ReconciliationItemRepository itemRepository;
    private final PagedQueryService pagedQueryService;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "name", "fileAName", "fileBName",
            "matchCount", "missingInACount", "missingInBCount",
            "status", "createdAt", "updatedAt"
    );

    private static final Map<String, String> SORT_ALIASES = Map.of(
            // frontend column id → entity field if needed
            // "fileA", "fileAName"
    );

    public PagedResponse<ReconciliationResponseDTO> findAll(
            PaginationRequest pagination,
            String search
    ) {
        Specification<ReconciliationEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search,
                        "name",
                        "fileAName",
                        "fileBName",
                        "status"
                )
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                ReconciliationEntity.class,
                ReconciliationEntity::getId,
                ReconciliationResponseDTO::fromEntity,
                ReconciliationResponseDTO::setApprovalStatus,
                SORT_FIELDS,
                SORT_ALIASES
        );
    }


    public ApprovalAwareDTO<ReconciliationResponseDTO> findOne(Long id) {
        ReconciliationEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Reconciliation not found"));

        ReconciliationResponseDTO dto = ReconciliationResponseDTO.fromEntity(entity);

        return approvalStatusUtil.attachApprovalInfo(
                dto,
                entity.getId(),
                ReconciliationEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    // keep your existing compareExcel / compareAndSave here...


    public Map<String, Object> compareExcel(MultipartFile[] files) {

        if (files == null || files.length < 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Two Excel files are required"
            );
        }

        List<FileDataset> datasets = Arrays.stream(files)
                .map(this::parseFile)
                .toList();

        FileDataset fileA = datasets.get(0);
        FileDataset fileB = datasets.get(1);

        Set<String> matches = fileA.values.stream()
                .filter(fileB.values::contains)
                .collect(Collectors.toSet());

        Map<String, List<String>> missing = new HashMap<>();

        missing.put(fileA.fileName,
                fileA.values.stream()
                        .filter(v -> !fileB.values.contains(v))
                        .toList());

        missing.put(fileB.fileName,
                fileB.values.stream()
                        .filter(v -> !fileA.values.contains(v))
                        .toList());

        return Map.of(
                "matches", matches,
                "missing", missing
        );
    }


    @Transactional
    public ReconciliationResponseDTO submit(Map<String, Object> payload) {

        String name = (String) payload.get("name");
        String fileAName = (String) payload.get("fileAName");
        String fileBName = (String) payload.get("fileBName");

        @SuppressWarnings("unchecked")
        List<String> matches = (List<String>) payload.get("matches");

        @SuppressWarnings("unchecked")
        Map<String, List<String>> missing = (Map<String, List<String>>) payload.get("missing");

        List<String> missingA = missing.getOrDefault(fileAName, List.of());
        List<String> missingB = missing.getOrDefault(fileBName, List.of());

        ReconciliationEntity entity = new ReconciliationEntity();
        entity.setCode(helper.nextReconCode());
        entity.setName(name != null && !name.isBlank()
                ? name
                : "Reconciliation " + LocalDateTime.now());
        entity.setFileAName(fileAName);
        entity.setFileBName(fileBName);
        entity.setMatchCount(matches.size());
        entity.setMissingInACount(missingA.size());
        entity.setMissingInBCount(missingB.size());
        entity.setStatus("COMPLETED");
        entity.setCreatedBy(currentUserService.getCurrentUser());

        entity = repository.save(entity);

        List<ReconciliationItemEntity> items = new ArrayList<>();
        for (String v : matches) {
            items.add(item(entity, v, "MATCH", null));
        }
        for (String v : missingA) {
            items.add(item(entity, v, "MISSING_IN_B", fileAName));
        }
        for (String v : missingB) {
            items.add(item(entity, v, "MISSING_IN_A", fileBName));
        }
        itemRepository.saveAll(items);

        return ReconciliationResponseDTO.fromEntity(entity, new HashSet<>(matches), missing);
    }

    private ReconciliationItemEntity item(
            ReconciliationEntity parent, String value, String type, String sourceFile
    ) {
        ReconciliationItemEntity i = new ReconciliationItemEntity();
        i.setReconciliation(parent);
        i.setValue(value);
        i.setItemType(type);
        i.setSourceFile(sourceFile);
        return i;
    }

    private FileDataset parseFile(MultipartFile file) {

        try (InputStream is = file.getInputStream()) {

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            List<List<String>> rows = new ArrayList<>();

            // 👉 Read Excel like NestJS (array of arrays)
            for (Row row : sheet) {
                List<String> cols = new ArrayList<>();

                int maxCol = row.getLastCellNum();

                for (int i = 0; i < maxCol; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cols.add(normalize(cellValue(cell)));
                }

                rows.add(cols);
            }

            // 👉 Find header row
            List<String> header = rows.stream()
                    .filter(r -> r.contains("RRN") || r.contains("Reference Number"))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Header not found in " + file.getOriginalFilename()
                    ));

            int columnIndex = header.contains("RRN")
                    ? header.indexOf("RRN")
                    : header.indexOf("Reference Number");

            int start = rows.indexOf(header) + 1;

            // 👉 Extract + normalize + filter (CORE LOGIC)
            Set<String> values = rows.subList(start, rows.size())
                    .stream()
                    .map(r -> columnIndex < r.size() ? normalize(r.get(columnIndex)) : null)
                    .filter(this::isValidValue)
                    .collect(Collectors.toSet());

            return new FileDataset(file.getOriginalFilename(), values);

        } catch (Exception e) {
            throw new RuntimeException("Failed to read file: " + file.getOriginalFilename(), e);
        }
    }

    // 🔹 Strict filter (this is the real fix)
    private boolean isValidValue(String val) {
        if (val == null) return false;

        val = val.trim();

        return !val.isEmpty()
                && !val.equalsIgnoreCase("null")
                && !val.equalsIgnoreCase("undefined")
                && !val.equalsIgnoreCase("posted")
                && val.matches("\\d+"); // only digits
    }

    private String normalize(Object val) {
        return val == null ? "" : val.toString().trim();
    }

    private Object cellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double n = cell.getNumericCellValue();
                yield (n == Math.floor(n))
                        ? String.valueOf((long) n)
                        : String.valueOf(n);
            }
            case BOOLEAN -> cell.getBooleanCellValue();
            default -> null;
        };
    }

    private record FileDataset(String fileName, Set<String> values) {}

    public Map<String, Object> getItems(Long reconciliationId) {
        ReconciliationEntity recon = repository.findById(reconciliationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reconciliation not found"));

        List<ReconciliationItemEntity> items = itemRepository.findByReconciliationId(reconciliationId);

        List<String> matches = items.stream()
                .filter(i -> "MATCH".equals(i.getItemType()))
                .map(ReconciliationItemEntity::getValue)
                .toList();

        List<String> missingInA = items.stream()
                .filter(i -> "MISSING_IN_A".equals(i.getItemType()))
                .map(ReconciliationItemEntity::getValue)
                .toList();

        List<String> missingInB = items.stream()
                .filter(i -> "MISSING_IN_B".equals(i.getItemType()))
                .map(ReconciliationItemEntity::getValue)
                .toList();

        return Map.of(
                "id", recon.getId(),
                "name", recon.getName(),
                "fileAName", recon.getFileAName(),
                "fileBName", recon.getFileBName(),
                "matchCount", matches.size(),
                "missingInACount", missingInA.size(),
                "missingInBCount", missingInB.size(),
                "matches", matches,
                "missingInA", missingInA,   // values that exist in B but not in A
                "missingInB", missingInB    // values that exist in A but not in B
        );
    }
}