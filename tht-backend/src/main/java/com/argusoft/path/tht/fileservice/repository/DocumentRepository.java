package com.argusoft.path.tht.fileservice.repository;

import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * This repository is for making queries on the Document model.
 *
 * @author Hardik
 */

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, String>, JpaSpecificationExecutor<DocumentEntity> {

}
