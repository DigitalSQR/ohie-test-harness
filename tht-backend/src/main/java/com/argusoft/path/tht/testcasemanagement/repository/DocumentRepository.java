package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.testcasemanagement.models.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, String>, DocumentCustomRepository {

    @Query("SELECT entity FROM DocumentEntity entity WHERE entity.fileId = (:fileId)")
    Optional<DocumentEntity> findDocumentByFileId(@Param("fileId") String fileId);

    @Query("SELECT entity FROM DocumentEntity entity WHERE entity.refObjUri = (:refObjectUri) AND entity.refId = (:refObjectId)")
    List<DocumentEntity> findDocumentByRefObjectUriAndId(@Param("refObjectUri") String refObjectUri, @Param("refObjectId") String refObjectId);

}
