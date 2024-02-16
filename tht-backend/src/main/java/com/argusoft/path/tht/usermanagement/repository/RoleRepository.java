package com.argusoft.path.tht.usermanagement.repository;

import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository is for making queries on the User model.
 *
 * @author Dhruv
 */
@Repository
public interface RoleRepository
        extends JpaRepository<RoleEntity, String>, JpaSpecificationExecutor<RoleEntity> {

    @Query("SELECT DISTINCT entity FROM RoleEntity entity \n")
    public Page<RoleEntity> findRoles(Pageable pageable);

    @Query("SELECT DISTINCT entity FROM RoleEntity entity \n"
            + " WHERE entity.id IN (:ids)")
    public List<RoleEntity> findRolesByIds(@Param("ids") List<String> ids);
}
