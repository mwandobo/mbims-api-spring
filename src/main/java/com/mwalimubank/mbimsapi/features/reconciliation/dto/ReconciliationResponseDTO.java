package com.mwalimubank.mbimsapi.features.reconciliation.dto;

import com.mwalimubank.mbimsapi.features.reconciliation.entity.ReconciliationEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class ReconciliationResponseDTO {

    private Long id;
    private String code;
    private String name;
    private String fileAName;
    private String fileBName;
    private Integer matchCount;
    private Integer missingInACount;
    private Integer missingInBCount;
    private String staffName;
    private Long staffId;
    private String status;
    private String createdAt;
    private String updatedAt;
    private String approvalStatus;

    /** Present on compare / detail; often omitted on list */
    private Set<String> matches;
    private Map<String, List<String>> missing;

    public static ReconciliationResponseDTO fromEntity(ReconciliationEntity entity) {
        ReconciliationResponseDTO dto = new ReconciliationResponseDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setFileAName(entity.getFileAName());
        dto.setFileBName(entity.getFileBName());
        dto.setMatchCount(entity.getMatchCount());
        dto.setMissingInACount(entity.getMissingInACount());
        dto.setMissingInBCount(entity.getMissingInBCount());
        dto.setStatus(entity.getStatus());
        // use your DateFormatterUtil if createdAt is Instant
        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt().toString());
        }
        dto.setStaffName(
                entity.getCreatedBy() != null ? entity.getCreatedBy().getName() : null
        );
        dto.setStaffId(
                entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null
        );
        if (entity.getUpdatedAt() != null) {
            dto.setUpdatedAt(entity.getUpdatedAt().toString());
        }
        return dto;
    }

    public static ReconciliationResponseDTO fromEntity(
            ReconciliationEntity entity,
            Set<String> matches,
            Map<String, List<String>> missing
    ) {
        ReconciliationResponseDTO dto = fromEntity(entity);
        dto.setMatches(matches);
        dto.setMissing(missing);
        return dto;
    }
}