package com.argusoft.path.tht.testcasemanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseVariableCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseVariableEntity;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestValueRepository;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseVariableRepository;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseVariableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This TestcaseVariableServiceServiceImpl contains implementation for TestcaseVariable service.
 *
 * @author Aastha
 */
@Service
public class TestcaseVariableServiceServiceImpl implements TestcaseVariableService {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseVariableServiceServiceImpl.class);

    TestcaseVariableRepository testcaseVariablesRepository;

    TestRequestValueRepository testRequestValueRepository;
    private TestcaseVariableService testcaseVariablesService;

    private SpecificationService specificationService;

    private TestcaseService testcaseService;

    public TestcaseVariableService getTestcaseVariablesService() {
        return testcaseVariablesService;
    }

    @Autowired
    public void setTestcaseVariablesService(TestcaseVariableService testcaseVariablesService) {
        this.testcaseVariablesService = testcaseVariablesService;
    }

    public SpecificationService getSpecificationService() {
        return specificationService;
    }

    @Autowired
    public void setSpecificationService(SpecificationService specificationService) {
        this.specificationService = specificationService;
    }

    @Autowired
    public void setTestcaseService(TestcaseService testcaseService) {
        this.testcaseService = testcaseService;
    }

    @Autowired
    public void setTestcaseVariablesRepository(TestcaseVariableRepository testcaseVariablesRepository) {
        this.testcaseVariablesRepository = testcaseVariablesRepository;
    }

    @Autowired
    public void setTestRequestValueRepository(TestRequestValueRepository testRequestValueRepository) {
        this.testRequestValueRepository = testRequestValueRepository;
    }


    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public List<TestcaseVariableEntity> searchTestcaseVariables(TestcaseVariableCriteriaSearchFilter testcaseVariablesCriteriaSearchFilter, ContextInfo contextInfo) throws OperationFailedException, InvalidParameterException {
        Specification<TestcaseVariableEntity> testcaseVariableEntitySpecification = testcaseVariablesCriteriaSearchFilter.buildSpecification(contextInfo);
        return this.testcaseVariablesRepository.findAll(testcaseVariableEntitySpecification);
    }


    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public List<TestcaseVariableEntity> getTestcaseVariablesByComponentId(String componentId, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        List<TestcaseVariableEntity> testcaseVariablesEntities = new ArrayList<>();
        SpecificationCriteriaSearchFilter specificationCriteriaSearchFilter = new SpecificationCriteriaSearchFilter();
        specificationCriteriaSearchFilter.setComponentId(componentId);

        List<SpecificationEntity> specifications= specificationService.searchSpecifications(specificationCriteriaSearchFilter, contextInfo);
        for(SpecificationEntity specification : specifications){
            if(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE.equals(specification.getState())){
                TestcaseCriteriaSearchFilter testcaseCriteriaSearchFilter = new TestcaseCriteriaSearchFilter();
                testcaseCriteriaSearchFilter.setSpecificationId(specification.getId());
                List<TestcaseEntity> testcases = testcaseService.searchTestcases(testcaseCriteriaSearchFilter, contextInfo);
                for(TestcaseEntity testcase : testcases) {
                    if (TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE.equals(testcase.getState())) {
                        for (TestcaseVariableEntity testcaseVariablesEntity : testcase.getTestcaseVariables()) {
                            if (TestcaseServiceConstants.TESTCASE_VARIABLE_STATUS_ACTIVE.equals(testcaseVariablesEntity.getState())) {
                                testcaseVariablesEntities.add(testcaseVariablesEntity);
                            }
                        }
                    }
                }
            }
        }
        return testcaseVariablesEntities;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestcaseVariableEntity getTestcaseVariableById(String testcaseVariablesId,
                                                           ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException, OperationFailedException {
        if (!StringUtils.hasLength(testcaseVariablesId)) {
            throw new InvalidParameterException("TestcaseVariableId is missing");
        }
        TestcaseVariableCriteriaSearchFilter testcaseVariablesCriteriaSearchFilter = new TestcaseVariableCriteriaSearchFilter(testcaseVariablesId);
        List<TestcaseVariableEntity> testcaseVariablesEntities = this.searchTestcaseVariables(testcaseVariablesCriteriaSearchFilter, contextInfo);
        return testcaseVariablesEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("TestcaseVariableId does not found with id : " + testcaseVariablesId));
    }
}
