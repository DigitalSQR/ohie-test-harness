package com.argusoft.path.tht.testprocessmanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestValueRepository;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestValueCriteriaSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestValueEntity;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestValueService;
import com.argusoft.path.tht.testprocessmanagement.validator.TestRequestValueValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This TestcaseVariableServiceServiceImpl contains implementation for TestcaseVariable service.
 *
 * @author Aastha
 */
@Service
public class TestRequestValueServiceServiceImpl implements TestRequestValueService {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestRequestValueServiceServiceImpl.class);

    TestRequestValueRepository testRequestValueRepository;

    private SpecificationService specificationService;

    private TestRequestService testRequestService;

    public SpecificationService getSpecificationService() {
        return specificationService;
    }

    @Autowired
    public void setSpecificationService(SpecificationService specificationService) {
        this.specificationService = specificationService;
    }

    @Autowired
    public void setTestRequestValueRepository(TestRequestValueRepository testRequestValueRepository) {
        this.testRequestValueRepository = testRequestValueRepository;
    }

    public TestRequestService getTestRequestService() {
        return testRequestService;
    }

    @Autowired
    public void setTestRequestService(TestRequestService testRequestService) {
        this.testRequestService = testRequestService;
    }

    @Override
    public List<TestRequestValueEntity> searchTestRequestValues(TestRequestValueCriteriaSearchFilter testRequestValueCriteriaSearchFilter, ContextInfo contextInfo) throws OperationFailedException, InvalidParameterException {
        Specification<TestRequestValueEntity> testRequestValueEntitySpecification = testRequestValueCriteriaSearchFilter.buildSpecification(contextInfo);
        return this.testRequestValueRepository.findAll(testRequestValueEntitySpecification);
    }

    @Override
    public TestRequestValueEntity getTestRequestValueById(String testRequestValueId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, OperationFailedException {
        if (!StringUtils.hasLength(testRequestValueId)) {
            throw new InvalidParameterException("TestRequestValueId is missing");
        }
        TestRequestValueCriteriaSearchFilter testRequestValueCriteriaSearchFilter = new TestRequestValueCriteriaSearchFilter(testRequestValueId);
        List<TestRequestValueEntity> testRequestValueEntities = this.searchTestRequestValues(testRequestValueCriteriaSearchFilter, contextInfo);
        return testRequestValueEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("TestRequestValueId does not found with id : " + testRequestValueId));
    }

    /**
     * {@inheritdoc}
     *
     * @return
//     */
    @Override
    @Transactional
    public List<TestRequestValueEntity> updateTestRequestValues(List<TestRequestValueEntity> testRequestValueEntities, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, OperationFailedException, DataValidationErrorException, VersionMismatchException {
        List<TestRequestValueEntity> updatedRequestValues = new ArrayList<>();

        TestRequestValueValidator.validateUpdateTestRequestValue(testRequestValueEntities, testRequestService, contextInfo);
        for(TestRequestValueEntity testRequestValueEntity : testRequestValueEntities) {
            if(testRequestValueEntity.getTestcaseVariableId() != null){
                TestRequestValueCriteriaSearchFilter testRequestValueCriteriaSearchFilter = new TestRequestValueCriteriaSearchFilter();
                testRequestValueCriteriaSearchFilter.setTestcaseVariableId(testRequestValueEntity.getTestcaseVariableId());
                List<TestRequestValueEntity> originalEntities = this.searchTestRequestValues(testRequestValueCriteriaSearchFilter, contextInfo);
                TestRequestValueEntity originalEntity;
                if(!originalEntities.isEmpty()){
                    originalEntity = originalEntities.get(0);
                    originalEntity.setTestRequest(testRequestValueEntity.getTestRequest());
                    originalEntity.setTestcaseVariableId(testRequestValueEntity.getTestcaseVariableId());
                    originalEntity.setValue(testRequestValueEntity.getValue());
                    testRequestValueEntity = originalEntity;
                }
            }
            else{
                TestRequestValueEntity newTestRequestValueEntity = new TestRequestValueEntity();
                newTestRequestValueEntity.setTestRequest(testRequestValueEntity.getTestRequest());
                newTestRequestValueEntity.setTestcaseVariableId(testRequestValueEntity.getTestcaseVariableId());
                newTestRequestValueEntity.setValue(testRequestValueEntity.getValue());
                testRequestValueEntity = newTestRequestValueEntity;
            }
            updatedRequestValues.add(testRequestValueEntity);
        }
        return this.testRequestValueRepository.saveAll(updatedRequestValues);
    }
}
