package com.mwalimubank.mbimsapi.features.approval.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalActionRequestDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalActionResponseDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalActionEntity;
import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalLevelEntity;
import com.mwalimubank.mbimsapi.features.approval.enums.ApprovalActionEnum;
import com.mwalimubank.mbimsapi.features.approval.repository.ApprovalActionRepository;
import com.mwalimubank.mbimsapi.features.approval.repository.ApprovalLevelRepository;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.features.common.PageSpecs;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;
import com.mwalimubank.mbimsapi.features.notification.NotificationService;
import com.mwalimubank.mbimsapi.features.notification.dto.SendNotificationDto;
import com.mwalimubank.mbimsapi.features.notification.enums.NotificationChannelsEnum;
import com.mwalimubank.mbimsapi.features.role.RoleEntity;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
import com.mwalimubank.mbimsapi.features.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalActionService {
    private final ApprovalActionRepository repository;
    private final ApprovalLevelRepository approvalLevelRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "name", "createdBy.name"
    );

    private static final Map<String, String> SORT_ALIASES = Map.of(
            "createdByName", "createdBy.name"   // frontend sortBy=departmentName
    );



    public PagedResponse<ApprovalActionResponseDTO> findAll(
            PaginationRequest pagination,
            String search,
            String entityName,
            Long entityId
    ){
        Specification<ApprovalActionEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search, "name", "createdBy.name"),
                (root, query, cb) -> {
                    if (entityName == null || entityName.isBlank()) {
                        return cb.conjunction();
                    }
                    return cb.equal(root.get("entityName"), entityName);
                },

                // Filter by entityId
                (root, query, cb) -> {
                    if (entityId == null) {
                        return cb.conjunction();
                    }
                    return cb.equal(root.get("entityId"), entityId);
                }
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                ApprovalActionEntity.class,
                ApprovalActionEntity::getId,
                ApprovalActionResponseDTO::fromEntity,
                ApprovalActionResponseDTO::setApprovalStatus,
                SORT_FIELDS,
                SORT_ALIASES
        );
    }


    @Transactional
    public ApprovalActionEntity create(ApprovalActionRequestDTO request) throws JsonProcessingException {



        ApprovalLevelEntity approvalLevel = approvalLevelRepository.findById(request.getApprovalLevelId())
                .orElseThrow(() -> new IllegalStateException("Approval Level not found"));

        Optional<ApprovalActionEntity> existing =
                repository.findByApprovalLevelIdAndEntityId(
                        request.getApprovalLevelId(),
                        request.getEntityId()
                );

        if (existing.isPresent()) {
            throw new IllegalStateException( "Approval Action has been done for this level "+
                    approvalLevel.getName() + " and entity " + request.getEntityName());
        }

        if (request.getEntityCreatorId() == null) {
            throw new IllegalStateException("Entity Creator Id is missing in DTO");
        }

        UserEntity user = currentUserService.getCurrentUser();

        UserEntity entityCreator = userRepository.findById(request.getEntityCreatorId())
                .orElseThrow(() -> new IllegalStateException("User Not found"));

        ApprovalActionEntity action = new ApprovalActionEntity();
        action.setApprovalLevel(approvalLevel);
        action.setCreatedBy(user);
        action.setRole(user.getRole());
        action.setName(request.getName());
        action.setDescription(request.getDescription());
        action.setAction(request.getAction());
        action.setEntityName(request.getEntityName());
        action.setEntityId(request.getEntityId());
        action.setEntityCreatorId(request.getEntityCreatorId());




        ApprovalActionEntity saved = repository.save(action);

        // 🔔 Send Notifications (equivalent to NestJS)
       handleApprovalNotifications(
                request, approvalLevel, entityCreator, user
        );

        return saved;

    }

    public ApprovalAwareDTO<ApprovalActionResponseDTO> findOne  (Long  userId) {
        ApprovalActionEntity entity = repository.findById( userId)
                .orElseThrow(() -> new IllegalStateException(" User not found"));

        ApprovalActionResponseDTO dto = ApprovalActionResponseDTO.fromEntity(entity);

        return approvalStatusUtil.attachApprovalInfo(
                dto,
                entity
        );
    }

    @Transactional
    public ApprovalActionEntity update(Long id, ApprovalActionRequestDTO request) {
        ApprovalActionEntity action = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Approval Action not found"));

        ApprovalLevelEntity approvalLevel = approvalLevelRepository.findById(request.getApprovalLevelId())
                .orElseThrow(() -> new IllegalStateException("Approval Level not found"));

        Long userId = currentUserService.getCurrentUserId();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User Not found"));

        action.setApprovalLevel(approvalLevel);
        action.setCreatedBy(user);
        action.setName(request.getName());
        action.setDescription(request.getDescription());
        action.setAction(request.getAction());
        action.setEntityName(request.getEntityName());
        action.setEntityId(request.getEntityId());

        return repository.save(action);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        ApprovalActionEntity action = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("ApprovalAction not found"));

        if (soft) {
            action.setDeleted(true);
            repository.save(action);
        } else {
            repository.delete(action);
        }
    }

    @Transactional
    public void handleApprovalNotifications(
            ApprovalActionRequestDTO dto,
            ApprovalLevelEntity approvalLevel,
            UserEntity entityCreator,
            UserEntity performedByUser
    ) {

        // 🟥 CASE 1: Request Rejected
        if (dto.getAction().equals(ApprovalActionEnum.REJECTED)) {

            List<ApprovalLevelEntity> previousLevels =
                    approvalLevelRepository.findByUserApprovalIdAndLevelLessThanEqual(
                            approvalLevel.getUserApproval().getId(),
                            approvalLevel.getLevel()
                    );

            List<Long> roleIds = previousLevels.stream()
                    .filter(l -> l.getRole() != null)
                    .map(l -> l.getRole().getId())
                    .toList();

            List<UserEntity> previousApprovers = userRepository.findByRoleIdIn(roleIds);

            List<String> recipients = new ArrayList<>();
            recipients.add(entityCreator.getEmail());
            recipients.addAll(
                    previousApprovers.stream()
                            .map(UserEntity::getEmail)
                            .filter(Objects::nonNull)
                            .toList()
            );

            Map<String, Object> context = new HashMap<>();
            context.put("userName", entityCreator.getName());
            context.put("requestName", dto.getEntityName());
            context.put("assets", dto.getExtraData1());
            context.put("requestDescription", dto.getDescription());
            context.put("rejectedBy", performedByUser.getName());
            context.put("rejectionDate", LocalDate.now().toString());
            context.put("priority", "Normal");
            context.put("priorityColor", "red");
            context.put("approvalLink", dto.getRedirectUrl());
            context.put("year", Year.now().getValue());

            try {
                SendNotificationDto notification = new SendNotificationDto();
                notification.setChannel(NotificationChannelsEnum.EMAIL);
                notification.setRecipients(recipients);
                notification.setForName(approvalLevel.getUserApproval().getSysApproval().getEntityName());
                notification.setForId(approvalLevel.getId());
                notification.setContext(context);
                notification.setTemplate("approval-rejected");
                notification.setSubject("Approval Rejected");
                notification.setDescription(
                        "Approval For Entity " + dto.getEntityName() + " has been Rejected"
                );
                notification.setRedirectUrl(dto.getRedirectUrl());

                notificationService.sendNotification(notification);

                log.info("❌ Rejection email sent to: {}", recipients);

            } catch (Exception e) {
                log.error("❌ Failed to send rejection notifications", e);
            }

            return;
        }

        // 🟦 CASE 2: Check if there's a next level
        log.info("Checking for next approval level after {} ({})…",
                approvalLevel.getId(), approvalLevel.getName());

        Optional<ApprovalLevelEntity> nextLevel = approvalLevelRepository
                .findFirstByUserApprovalIdAndLevelGreaterThanOrderByLevelAsc(
                        approvalLevel.getUserApproval().getId(),
                        approvalLevel.getLevel()
                );

        // -----------------------------------------------
        // 🟩 FINAL LEVEL → Send final approval email
        // -----------------------------------------------
        if (nextLevel.isEmpty()) {

            log.info("checking extradata {}", dto.getExtraData1());

            Map<String, Object> context = new HashMap<>();
            context.put("userName", entityCreator.getName());
            context.put("requestId", dto.getEntityId());
            context.put("requestName", dto.getEntityName());
            context.put("assets", dto.getExtraData1());
            context.put("requestDescription", dto.getDescription());
            context.put("finalLevelName", approvalLevel.getName());
            context.put("approvedBy", performedByUser.getName());
            context.put("approvalDate", LocalDate.now().toString());
            context.put("priority", "Normal");
            context.put("priorityColor", "blue");
            context.put("approvalLink", dto.getRedirectUrl());
            context.put("year", Year.now().getValue());

            try {
                SendNotificationDto notification = new SendNotificationDto();
                notification.setChannel(NotificationChannelsEnum.EMAIL);
                notification.setRecipients(List.of(entityCreator.getEmail()));
                notification.setForName(approvalLevel.getUserApproval().getSysApproval().getEntityName());
                notification.setForId(approvalLevel.getId());
                notification.setContext(context);
                notification.setTemplate("request-approved");
                notification.setSubject("Approval Complete For Entity: " + dto.getEntityName());
                notification.setDescription(
                        "Approval request for the entity " + dto.getEntityName() + " has been completed"
                );
                notification.setRedirectUrl(dto.getRedirectUrl());

                notificationService.sendNotification(notification);

                log.info("✅ Final approval email sent to {}", entityCreator.getEmail());

            } catch (Exception e) {
                log.error("❌ Failed to send final approval email", e);
            }

            return;
        }

        ApprovalLevelEntity _nextLevel = nextLevel.get();

        // -----------------------------------------------
        // 🟦 CASE 3: There *is* a next level → Notify next approvers
        // -----------------------------------------------
        log.info("Next level found: {} (role: {})",
                _nextLevel.getName(),
                _nextLevel.getRole() != null ? _nextLevel.getRole().getName() : "N/A");

        RoleEntity role = _nextLevel.getRole();

        List<String> recipients = new ArrayList<>();

        if (role != null) {
            List<UserEntity> users = userRepository.findByRoleId(role.getId());
            recipients = users.stream()
                    .map(UserEntity::getEmail)
                    .filter(Objects::nonNull)
                    .toList();

            log.info("Users for role {} → {}", role.getName(), recipients);
        }

        if (recipients.isEmpty()) {
            log.warn("No recipients found for next approval level {}", _nextLevel.getName());
            return;
        }

        Map<String, Object> context = new HashMap<>();
        context.put("userName", performedByUser.getName());
        context.put("requestId", dto.getEntityId());
        context.put("requestName", dto.getEntityName());
        context.put("assets", dto.getExtraData1());
        context.put("requestDescription", dto.getDescription());
        context.put("currentLevelName", approvalLevel.getName());
        context.put("nextLevelName", _nextLevel.getName());
        context.put("submittedBy", performedByUser.getEmail());
        context.put("submissionDate", LocalDate.now().toString());
        context.put("priority", "Normal");
        context.put("priorityColor", "blue");
        context.put("dueDate", "Not specified");
        context.put("year", Year.now().getValue());
        context.put("approvalLink", dto.getRedirectUrl());

        try {
            SendNotificationDto notification = new SendNotificationDto();
            notification.setChannel(NotificationChannelsEnum.EMAIL);
            notification.setRecipients(recipients);
            notification.setForName(approvalLevel.getUserApproval().getSysApproval().getEntityName());
            notification.setForId(approvalLevel.getId());
            notification.setContext(context);
            notification.setTemplate("next-approval");
            notification.setSubject("Approval Required: " + dto.getEntityName());
            notification.setDescription("Approval Required for " + dto.getEntityName());
            notification.setRedirectUrl(dto.getRedirectUrl());

            notificationService.sendNotification(notification);

            log.info("📨 Next-level notification sent to {}", recipients);

        } catch (Exception e) {
            log.error("❌ Failed to send next-level notification", e);
        }
    }

}
