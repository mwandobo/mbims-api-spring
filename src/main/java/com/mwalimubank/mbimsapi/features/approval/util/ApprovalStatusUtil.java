package com.mwalimubank.mbimsapi.features.approval.util;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalActionEntity;
import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalLevelEntity;
import com.mwalimubank.mbimsapi.features.approval.entity.SysApprovalEntity;
import com.mwalimubank.mbimsapi.features.approval.entity.UserApprovalEntity;
import com.mwalimubank.mbimsapi.features.approval.enums.ApprovalActionEnum;
import com.mwalimubank.mbimsapi.features.approval.repository.ApprovalActionRepository;
import com.mwalimubank.mbimsapi.features.approval.repository.ApprovalLevelRepository;
import com.mwalimubank.mbimsapi.features.approval.repository.SysApprovalRepository;
import com.mwalimubank.mbimsapi.features.approval.repository.UserApprovalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalStatusUtil {

    private final SysApprovalRepository sysApprovalRepository;
    private final UserApprovalRepository userApprovalRepository;
    private final ApprovalLevelRepository approvalLevelRepository;
    private final ApprovalActionRepository approvalActionRepository;
    private final CurrentUserService currentUserService;


    /**
     * Check if approval mode is enabled
     */

    public boolean hasApprovalMode(String entityName) {
        log.debug("Checking approval mode for entity: {}", entityName);

        Optional<SysApprovalEntity> sys = sysApprovalRepository.findByEntityName(entityName);
        if (sys.isEmpty()) {
            log.debug("No SysApproval found for entity: {}", entityName);
            return false;
        }

        log.debug("Found SysApproval with id: {}", sys.get().getId());

        Optional<UserApprovalEntity> userApproval =
                userApprovalRepository.findBySysApprovalId(sys.get().getId());

        if (userApproval.isEmpty()) {
            log.debug("No UserApproval found for sysApprovalId: {}", sys.get().getId());
            return false;
        }

        log.debug("Found UserApproval with id: {}", userApproval.get().getId());

        List<ApprovalLevelEntity> levels =
                approvalLevelRepository.findByUserApprovalId(userApproval.get().getId());

        if (levels.isEmpty()) {
            log.debug("No ApprovalLevels found for userApprovalId: {}", userApproval.get().getId());
            return false;
        }

        log.debug("Approval mode ACTIVE for entity: {} with {} levels",
                entityName, levels.size());

        return true;
    }

    /**
     * Determine approval status
     */
    public String getApprovalStatus(String entityName, Long entityId) {

        UserApprovalEntity userApproval = getUserApproval(entityName);
        if (userApproval == null) return "PENDING";

        List<ApprovalLevelEntity> levels =
                approvalLevelRepository.findByUserApprovalId(userApproval.getId());

        if (levels.isEmpty()) return "PENDING";

        List<Long> levelIds = levels.stream()
                .map(ApprovalLevelEntity::getId)
                .collect(Collectors.toList());

        List<ApprovalActionEntity> actions =
                approvalActionRepository.findByEntityIdAndApprovalLevelIdIn(entityId, levelIds);

        if (actions.isEmpty()) return "PENDING";

        // Check rejection
        if (actions.stream().anyMatch(a -> a.getAction() == ApprovalActionEnum.REJECTED)) {
            return "REJECTED";
        }

        for (ApprovalLevelEntity level : levels) {

            List<ApprovalActionEntity> levelActions = actions.stream()
                    .filter(a -> a.getApprovalLevel().getId().equals(level.getId()))
                    .toList();

            log.debug("Level {} → {} actions: {}",
                    level.getId(),
                    levelActions.size(),
                    levelActions.stream()
                            .map(a -> a.getAction().name())
                            .collect(Collectors.joining(", "))
            );

            if (levelActions.isEmpty()) return "PENDING";

            boolean approved = levelActions.stream()
                    .anyMatch(a -> a.getAction() == ApprovalActionEnum.APPROVED);

            if (!approved) return "PENDING";
        }

        return "APPROVED";
    }

    /**
     * Bulk status
     */

    public Map<Long, String> getBulkApprovalStatuses(String entityName, List<Long> entityIds) {

        log.debug("==== BULK APPROVAL STATUS START ====");
        log.debug("Entity: {}", entityName);
        log.debug("Entity IDs: {}", entityIds);

        Map<Long, String> statuses = new HashMap<>();

        UserApprovalEntity userApproval = getUserApproval(entityName);
        if (userApproval == null) {
            log.debug("No UserApproval found → defaulting all to PENDING");
            entityIds.forEach(id -> statuses.put(id, "PENDING"));
            return statuses;
        }

        log.debug("UserApproval ID: {}", userApproval.getId());

        List<ApprovalLevelEntity> levels =
                approvalLevelRepository.findByUserApprovalId(userApproval.getId());

        log.debug("Approval Levels count: {}", levels.size());
        log.debug("Approval Levels: {}", levels.stream()
                .map(ApprovalLevelEntity::getId)
                .toList());

        if (levels.isEmpty()) {
            log.debug("No levels found → defaulting all to PENDING");
            entityIds.forEach(id -> statuses.put(id, "PENDING"));
            return statuses;
        }

        List<ApprovalActionEntity> actions =
                approvalActionRepository.findByEntityNameAndEntityIdIn(entityName, entityIds);

        log.debug("Total actions fetched: {}", actions.size());

        actions.forEach(a -> log.debug(
                "Action → entityId: {}, levelId: {}, action: {}",
                a.getEntityId(),
                a.getApprovalLevel().getId(),
                a.getAction()
        ));

        // Group by entityId
        Map<Long, List<ApprovalActionEntity>> actionsByEntity =
                actions.stream().collect(Collectors.groupingBy(ApprovalActionEntity::getEntityId));

        for (Long entityId : entityIds) {

            log.debug("---- Evaluating entityId: {} ----", entityId);

            List<ApprovalActionEntity> entityActions =
                    actionsByEntity.getOrDefault(entityId, new ArrayList<>());

            log.debug("Total actions for entity {}: {}", entityId, entityActions.size());

            if (entityActions.isEmpty()) {
                log.debug("No actions found → PENDING");
                statuses.put(entityId, "PENDING");
                continue;
            }

            boolean rejected = false;
            boolean pending = false;

            for (ApprovalLevelEntity level : levels) {

                List<ApprovalActionEntity> levelActions = entityActions.stream()
                        .filter(a -> a.getApprovalLevel().getId().equals(level.getId()))
                        .toList();

                log.debug("Level {} → {} actions", level.getId(), levelActions.size());

                levelActions.forEach(a -> log.debug(
                        "   ↳ action: {}",
                        a.getAction()
                ));

                // 🚨 REJECTION CHECK
                if (levelActions.stream()
                        .anyMatch(a -> a.getAction() == ApprovalActionEnum.REJECTED)) {

                    log.debug("Entity {} REJECTED at level {}", entityId, level.getId());
                    rejected = true;
                    break;
                }

                // 🚨 APPROVAL CHECK
                boolean approved = levelActions.stream()
                        .anyMatch(a -> a.getAction() == ApprovalActionEnum.APPROVED);

                log.debug("Level {} approved? {}", level.getId(), approved);

                if (!approved) {
                    log.debug("Entity {} is still PENDING at level {}", entityId, level.getId());
                    pending = true;
                }
            }

            String finalStatus;
            if (rejected) finalStatus = "REJECTED";
            else if (pending) finalStatus = "PENDING";
            else finalStatus = "APPROVED";

            log.debug("Final status for entity {} → {}", entityId, finalStatus);

            statuses.put(entityId, finalStatus);
        }

        log.debug("==== BULK APPROVAL STATUS END ====");
        return statuses;
    }

    /**
     * Helpers
     */
    private UserApprovalEntity getUserApproval(String entityName) {
        Optional<SysApprovalEntity> sys = sysApprovalRepository.findByEntityName(entityName);
        if (sys.isEmpty()) return null;

        return userApprovalRepository
                .findBySysApprovalId(sys.get().getId())
                .orElse(null);
    }


    public <D, E extends BaseEntity> ApprovalAwareDTO<D> attachApprovalInfo(
            D responseDto,
            E entity
    ) {
        Long entityId = entity.getId();
        String entityName = entity.getClass().getSimpleName();

        log.debug("==== ATTACH APPROVAL INFO START ====");

        boolean hasApprovalMode = hasApprovalMode(entityName);
        String approvalStatus = getApprovalStatus(entityName, entityId);

        log.debug("hasApprovalMode: {}, approvalStatus: {}", hasApprovalMode, approvalStatus);

        if (!hasApprovalMode || "REJECTED".equals(approvalStatus)) {
            log.debug("Skipping approval logic (mode off or rejected)");
            return buildBasic(responseDto, hasApprovalMode, approvalStatus);
        }

        UserApprovalEntity userApproval = getUserApproval(entityName);

        if (userApproval == null) {
            log.debug("No UserApproval found → fallback");
            return buildBasic(responseDto, hasApprovalMode, approvalStatus);
        }

        log.debug("UserApproval ID: {}", userApproval.getId());

        List<ApprovalLevelEntity> levels = getLevelsByUserApproval(userApproval.getId());

        log.debug("Levels count: {}", levels.size());
        log.debug("Levels: {}", levels.stream()
                .map(l -> "ID=" + l.getId() + ", role=" + l.getRole().getId())
                .toList());

        List<Long> levelIds = levels.stream()
                .map(ApprovalLevelEntity::getId)
                .toList();

        List<ApprovalActionEntity> actions = getActions(entityId, levelIds);

        log.debug("Total actions fetched: {}", actions.size());
        actions.forEach(a -> log.debug(
                "Action → levelId: {}, action: {}",
                a.getApprovalLevel().getId(),
                a.getAction()
        ));

        boolean isMyLevelApproved = false;
        boolean shouldApprove = false;

        Long userRoleId =  currentUserService.getCurrentUserRoleId();

        ApprovalLevelEntity myLevel = levels.stream()
                .filter(level -> level.getRole().getId().equals(userRoleId))
                .findFirst()
                .orElse(null);

        if (myLevel == null) {
            log.debug("No matching level found for userRoleId: {}", userRoleId);
        } else {
            log.debug("My level found → ID: {}, createdAt: {}",
                    myLevel.getId(), myLevel.getCreatedAt());

            List<ApprovalActionEntity> myActions = actions.stream()
                    .filter(a -> a.getApprovalLevel().getId().equals(myLevel.getId()))
                    .toList();

            log.debug("My level actions count: {}", myActions.size());

            isMyLevelApproved = myActions.stream()
                    .anyMatch(a -> a.getAction() == ApprovalActionEnum.APPROVED);

            log.debug("isMyLevelApproved: {}", isMyLevelApproved);
        }

        if ("PENDING".equals(approvalStatus) && myLevel != null && !isMyLevelApproved) {

            List<ApprovalLevelEntity> previousLevels = levels.stream()
                    .filter(lvl ->
                            lvl.getCreatedAt() != null &&
                                    myLevel.getCreatedAt() != null &&
                                    lvl.getCreatedAt().isBefore(myLevel.getCreatedAt())
                    )
                    .toList();

            log.debug("Previous levels: {}", previousLevels.stream()
                    .map(ApprovalLevelEntity::getId)
                    .toList());

            boolean allPrevApproved = previousLevels.stream().allMatch(lvl -> {
                boolean approved = actions.stream().anyMatch(a ->
                        a.getApprovalLevel().getId().equals(lvl.getId()) &&
                                a.getAction() == ApprovalActionEnum.APPROVED
                );

                log.debug("Level {} approved? {}", lvl.getId(), approved);
                return approved;
            });

            shouldApprove = allPrevApproved;

            log.debug("shouldApprove: {}", shouldApprove);
        }

        log.debug("==== ATTACH APPROVAL INFO END ====");

        return new ApprovalAwareDTO<>(
                responseDto,
                hasApprovalMode,
                approvalStatus,
                isMyLevelApproved,
                shouldApprove,
                myLevel != null ? myLevel.getId() : null
        );
    }


    private <T> ApprovalAwareDTO<T> buildDefault(T entity) {
        return new ApprovalAwareDTO<>(entity, false, "N/A", false, false, null);
    }

    private <T> ApprovalAwareDTO<T> buildBasic(T entity, boolean hasApprovalMode, String status) {
        return new ApprovalAwareDTO<>(entity, hasApprovalMode, status, false, false, null);
    }






    public List<ApprovalLevelEntity> getLevelsByUserApproval(Long userApprovalId) {
        return approvalLevelRepository.findByUserApprovalId(userApprovalId);
    }

    public List<ApprovalActionEntity> getActions(Long entityId, List<Long> levelIds) {
        return approvalActionRepository
                .findByEntityIdAndApprovalLevelIdIn(entityId, levelIds);
    }
}
