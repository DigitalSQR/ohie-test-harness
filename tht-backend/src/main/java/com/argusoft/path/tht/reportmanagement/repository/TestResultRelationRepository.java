package com.argusoft.path.tht.reportmanagement.repository;

import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TestResultRelationRepository extends JpaRepository<TestResultRelationEntity,String>, JpaSpecificationExecutor<TestResultRelationEntity> {
}
