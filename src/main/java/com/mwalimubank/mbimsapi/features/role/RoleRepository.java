package com.mwalimubank.mbimsapi.features.role;

import com.mwalimubank.mbimsapi.features.administration.position.PositionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long>, JpaSpecificationExecutor<RoleEntity> {
    Optional<RoleEntity> findByName(String name);

    @EntityGraph(attributePaths = { "permissions" })
    Optional<RoleEntity> findWithPermissionsById(Long id);

    // // Best way: Using EntityGraph
    // @EntityGraph(attributePaths = {"permissions"})
    // Optional<RoleEntity> findByIdWithPermissions(Long id);

    @Query("""
                SELECT r
                FROM RoleEntity r
                LEFT JOIN FETCH r.permissions
                WHERE r.id = :id
            """)
    Optional<RoleEntity> findRoleWithPermissions(@Param("id") Long id);
}
