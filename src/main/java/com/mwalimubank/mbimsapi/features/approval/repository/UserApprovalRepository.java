package com.mwalimubank.mbimsapi.features.approval.repository;

import com.mwalimubank.mbimsapi.features.approval.entity.UserApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserApprovalRepository extends JpaRepository<UserApprovalEntity, Long>, JpaSpecificationExecutor<UserApprovalEntity> {
    UserApprovalEntity findByName(String name);

    Optional<UserApprovalEntity> findBySysApprovalId(Long sysApprovalId);

    boolean existsBySysApprovalId(Long sysApprovalId);

    // UserApprovalRepository
    @Query("SELECT MAX(u.order) FROM UserApprovalEntity u WHERE u.sysApproval.id = :sysApprovalId AND u.deleted = false")
    Optional<Integer> findMaxOrderBySysApprovalId(@Param("sysApprovalId") Long sysApprovalId);

    @Query("""
    SELECT u FROM UserApprovalEntity u
    WHERE u.sysApproval.id = :sysApprovalId
      AND u.deleted = false
    ORDER BY u.order DESC
    LIMIT 1
""")
    Optional<UserApprovalEntity> findTopBySysApprovalIdOrderByOrderDesc(
            @Param("sysApprovalId") Long sysApprovalId);

    @Query("""
    SELECT u FROM UserApprovalEntity u
    WHERE u.sysApproval.id = :sysApprovalId
      AND (u.deleted = false OR u.deleted IS NULL)
    ORDER BY COALESCE(u.order, 0) DESC
    """)
    List<UserApprovalEntity> findAllBySysApprovalIdOrderByOrderDesc(
            @Param("sysApprovalId") Long sysApprovalId);
}