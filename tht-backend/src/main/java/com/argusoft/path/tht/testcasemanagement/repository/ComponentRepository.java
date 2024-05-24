package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

/**
 * This repository is for making queries on the Component model.
 *
 * @author Dhruv
 */
@Repository
public interface ComponentRepository
        extends JpaRepository<ComponentEntity, String>, JpaSpecificationExecutor<ComponentEntity> {

//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    Optional<ComponentEntity> findById(String id);
//
//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    Page<ComponentEntity> findAll(Specification<ComponentEntity> feature1Specifications, Pageable pageable);
//
//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    List<ComponentEntity> findAll(Specification<ComponentEntity> feature1Specifications);

    @Query("SELECT e FROM ComponentEntity e WHERE e.id IN (SELECT a.refId FROM TestcaseResultEntity a WHERE a.refObjUri = :componentRefId AND a.testRequest =(SELECT i FROM TestRequestEntity i WHERE i.id =:testRequestId))")
    List<ComponentEntity> searchComponentsByTestRequest(@Param("testRequestId") String testRequestId,@Param("componentRefId") String componentRefId);

    @Query("SELECT c.rank, c.name, t.compliant, t.nonCompliant " +
            "FROM ComponentEntity c " +
            "JOIN TestcaseResultEntity t ON c.id = t.refId " +
            "WHERE t.refObjUri = :componentRefId " +
            "AND t.testRequest IN (SELECT i FROM TestRequestEntity i WHERE i.id = :testRequestId)")
    List<Object[]> searchComponentPartsByTestRequest(@Param("testRequestId") String testRequestId,@Param("componentRefId") String componentRefId);

    @Query("SELECT e.id, e.name FROM ComponentEntity e")
    List<Object[]> findAllIdName();

    @Query("SELECT e.name FROM ComponentEntity e")
    List<String> findAllName();

}
