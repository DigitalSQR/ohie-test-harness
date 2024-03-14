package com.argusoft.path.tht.reportmanagement.service.impl;

import com.argusoft.path.tht.reportmanagement.filter.TestResultRelationCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import com.argusoft.path.tht.reportmanagement.repository.TestResultRelationRepository;
import com.argusoft.path.tht.reportmanagement.service.TestResultRelationService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.reportmanagement.validator.TestResultRelationValidator;
import com.argusoft.path.tht.systemconfiguration.audit.constant.AuditServiceConstant;
import com.argusoft.path.tht.systemconfiguration.audit.filter.SearchFilter;
import com.argusoft.path.tht.systemconfiguration.audit.service.AuditService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.models.entity.IdMetaEntity;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TestResultRelationServiceServiceImpl implements TestResultRelationService {

    private TestcaseResultService testcaseResultService;
    private TestResultRelationRepository testResultRelationRepository;
    private AuditService auditService;

    @Autowired
    public void setTestcaseResultService(TestcaseResultService testcaseResultService) {
        this.testcaseResultService = testcaseResultService;
    }

    @Autowired
    public void setTestResultRelationRepository(TestResultRelationRepository testResultRelationRepository) {
        this.testResultRelationRepository = testResultRelationRepository;
    }

    @Autowired
    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public TestResultRelationEntity createTestcaseResult(TestResultRelationEntity testResultRelationEntity, ContextInfo contextInfo) throws OperationFailedException, InvalidParameterException, DataValidationErrorException {
        TestResultRelationValidator.validateCreateUpdateTestResultRelation(Constant.CREATE_VALIDATION, testResultRelationEntity, this, testcaseResultService, contextInfo);
        testResultRelationEntity = testResultRelationRepository.saveAndFlush(testResultRelationEntity);
        return testResultRelationEntity;
    }

    @Override
    public TestResultRelationEntity updateTestcaseResult(TestResultRelationEntity testResultRelationEntity, ContextInfo contextInfo) throws OperationFailedException, DataValidationErrorException, InvalidParameterException {
        TestResultRelationValidator.validateCreateUpdateTestResultRelation(Constant.UPDATE_VALIDATION, testResultRelationEntity, this, testcaseResultService, contextInfo);
        testResultRelationEntity = testResultRelationRepository.saveAndFlush(testResultRelationEntity);
        return testResultRelationEntity;
    }

    @Override
    public Page<TestResultRelationEntity> searchTestResultRelation(TestResultRelationCriteriaSearchFilter testResultRelationCriteriaSearchFilter, Pageable pageable, ContextInfo contextInfo) throws InvalidParameterException {
        Specification<TestResultRelationEntity> testResultRelationEntitySpecification = testResultRelationCriteriaSearchFilter.buildSpecification(contextInfo);
        return testResultRelationRepository.findAll(testResultRelationEntitySpecification, pageable);
    }

    @Override
    public List<TestResultRelationEntity> searchTestResultRelation(TestResultRelationCriteriaSearchFilter testResultRelationCriteriaSearchFilter, ContextInfo contextInfo) throws InvalidParameterException {
        Specification<TestResultRelationEntity> testResultRelationEntitySpecification = testResultRelationCriteriaSearchFilter.buildSpecification(contextInfo);
        return testResultRelationRepository.findAll(testResultRelationEntitySpecification);
    }

    @Override
    public List<ValidationResultInfo> validateTestcaseResult(String validationTypeKey, TestResultRelationEntity testResultRelationEntity, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException {
        return TestResultRelationValidator.validateTestResultRelation(validationTypeKey, this, testcaseResultService, testResultRelationEntity, contextInfo);
    }

    @Override
    public TestResultRelationEntity getTestResultRelationById(String testRelationResultId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException {
        TestResultRelationCriteriaSearchFilter testResultRelationCriteriaSearchFilter = new TestResultRelationCriteriaSearchFilter();
        testResultRelationCriteriaSearchFilter.setId(Collections.singletonList(testRelationResultId));
        List<TestResultRelationEntity> testResultRelationEntities = this.searchTestResultRelation(testResultRelationCriteriaSearchFilter, contextInfo);
        return testResultRelationEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("TestResultRelation does not found with id : " + testRelationResultId));
    }

    @Override
    public List<Object> getTestResultRelationEntitiesFromAuditMapping(List<String> resultRelationIds, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, OperationFailedException, DataValidationErrorException {

        List<Object> objects = new ArrayList<>();

        TestResultRelationCriteriaSearchFilter testResultRelationCriteriaSearchFilter = new TestResultRelationCriteriaSearchFilter();
        testResultRelationCriteriaSearchFilter.setId(resultRelationIds);

        List<TestResultRelationEntity> testResultRelationEntities = this.searchTestResultRelation(testResultRelationCriteriaSearchFilter, contextInfo);

        if (resultRelationIds != null && resultRelationIds.size() != testResultRelationEntities.size()) {
            throw new DoesNotExistException("Failed to find all the test result related records from the database ");
        }

        // get from audit
        for (TestResultRelationEntity testResultRelationEntity : testResultRelationEntities) {
            SearchFilter searchFilter = new SearchFilter();
            searchFilter.setIds(Collections.singletonList(testResultRelationEntity.getRefId()));
            searchFilter.setVersionNumber(testResultRelationEntity.getVersionOfRefEntity());
            searchFilter.setName(AuditServiceConstant.EntityType.getEntityTypeBasedOnRefObjectUri(testResultRelationEntity.getRefObjUri()).name());

            List<Object> auditObjects = auditService.searchAudit(searchFilter, contextInfo);

            Optional<Object> testcaseOptionFromAudit = auditObjects.stream().findFirst();
            if (testcaseOptionFromAudit.isEmpty()) {
                throw new DoesNotExistException("Failed to find the testcase Option in TestcaseResultRelation Audit data with refId " + testResultRelationEntity.getRefId());
            }

            objects.add(testcaseOptionFromAudit.get());

        }
        return objects;

    }

    @Override
    public List<Object> getTestResultRelationEntitiesFromAuditMapping(String testcaseResultId, String refObjectUri, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {

        TestResultRelationCriteriaSearchFilter testResultRelationCriteriaSearchFilter = new TestResultRelationCriteriaSearchFilter();
        testResultRelationCriteriaSearchFilter.setTestcaseResultId(testcaseResultId);
        testResultRelationCriteriaSearchFilter.setRefObjUri(refObjectUri);

        List<TestResultRelationEntity> testResultRelationEntities = this.searchTestResultRelation(testResultRelationCriteriaSearchFilter, contextInfo);

        if (testResultRelationEntities.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> resultRelationIds = testResultRelationEntities.stream().map(IdMetaEntity::getId).toList();

        return this.getTestResultRelationEntitiesFromAuditMapping(resultRelationIds, contextInfo);
    }
}
