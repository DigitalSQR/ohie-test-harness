/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.usermanagement.repository;

import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * This repository is for making queries on the User model.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@Repository
public interface UserRepository
        extends JpaRepository<UserEntity, String>, UserCustomRepository {

    public Optional<UserEntity> findByUserName(String userName);

    @Query("SELECT DISTINCT entity FROM UserEntity entity \n")
    public Page<UserEntity> findUsers(Pageable pageable);

    @Query("SELECT DISTINCT entity FROM UserEntity entity \n"
            + " WHERE entity.id IN (:ids)")
    public List<UserEntity> findUsersByIds(@Param("ids") List<String> ids);
}
