package com.argusoft.path.tht.testcasemanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.utils.CommonStateChangeValidator;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseOptionServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseOptionRepository;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.validator.TestcaseOptionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This TestcaseOptionServiceServiceImpl contains implementation for
 * TestcaseOption service.
 *
 * @author Dhruv
 */
@Service
public class TestcaseOptionServiceServiceImpl implements TestcaseOptionService {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseOptionServiceServiceImpl.class);

    TestcaseOptionRepository testcaseOptionRepository;
    @Autowired
    public void setTestcaseOptionRepository(TestcaseOptionRepository testcaseOptionRepository){
        this.testcaseOptionRepository = testcaseOptionRepository;
    }

    TestcaseService testcaseService;
    @Autowired
    public void setTestcaseService(TestcaseService testcaseService){
        this.testcaseService = testcaseService;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestcaseOptionEntity createTestcaseOption(TestcaseOptionEntity testcaseOptionEntity,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        if (testcaseOptionEntity == null) {
            LOGGER.error("{}{}",ValidateConstant.INVALID_PARAM_EXCEPTION,  TestcaseOptionServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("testcaseOptionEntity is missing");
        }

        defaultValueCreateTestCaseOption(testcaseOptionEntity, contextInfo);

        TestcaseOptionValidator.validateCreateUpdateTestcaseOption(Constant.CREATE_VALIDATION,
                this,
                testcaseService,
                testcaseOptionEntity,
                contextInfo);

        testcaseOptionEntity = testcaseOptionRepository.saveAndFlush(testcaseOptionEntity);

        return testcaseOptionEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestcaseOptionEntity updateTestcaseOption(TestcaseOptionEntity testcaseOptionEntity,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        if (testcaseOptionEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestcaseOptionServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("testcaseOptionEntity is missing");
        }

        TestcaseOptionValidator.validateCreateUpdateTestcaseOption(Constant.UPDATE_VALIDATION,
                this,
                testcaseService,
                testcaseOptionEntity,
                contextInfo);

        testcaseOptionEntity = testcaseOptionRepository.save(testcaseOptionEntity);
        return testcaseOptionEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<TestcaseOptionEntity> searchTestcaseOptions(
            TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseOptionEntity> testcaseOptionEntitySpecification = testcaseOptionCriteriaSearchFilter.buildSpecification(contextInfo);
        return testcaseOptionRepository.findAll(testcaseOptionEntitySpecification, pageable);
    }

    @Override
    public List<TestcaseOptionEntity> searchTestcaseOptions(
            TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseOptionEntity> testcaseOptionEntitySpecification = testcaseOptionCriteriaSearchFilter.buildSpecification(contextInfo);
        return testcaseOptionRepository.findAll(testcaseOptionEntitySpecification);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestcaseOptionEntity getTestcaseOptionById(String testcaseOptionId,
            ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (!StringUtils.hasLength(testcaseOptionId)) {
            throw new InvalidParameterException("TestcaseOptionId is missing");
        }
        TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter = new TestcaseOptionCriteriaSearchFilter(testcaseOptionId);
        List<TestcaseOptionEntity> testcaseOptionEntities = this.searchTestcaseOptions(testcaseOptionCriteriaSearchFilter, contextInfo);
        return testcaseOptionEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("Testcase does not found with id : " + testcaseOptionId));
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<ValidationResultInfo> validateTestcaseOption(
            String validationTypeKey,
            TestcaseOptionEntity testcaseOptionEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        if (testcaseOptionEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestcaseOptionServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("testcaseOptionEntity is missing");
        }
        List<ValidationResultInfo> errors = TestcaseOptionValidator.validateTestcaseOption(validationTypeKey, testcaseOptionEntity, this, testcaseService, contextInfo);
        return errors;
    }

    @Override
    public TestcaseOptionEntity changeState(String testcaseOptionId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {

        List<ValidationResultInfo> errors = new ArrayList<>();

        TestcaseOptionEntity testcaseOptionEntity = this.getTestcaseOptionById(testcaseOptionId, contextInfo);

        CommonStateChangeValidator.validateStateChange(TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS, TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_MAP, testcaseOptionEntity.getState(), stateKey, errors);

        testcaseOptionEntity.setState(stateKey);
        testcaseOptionEntity = testcaseOptionRepository.saveAndFlush(testcaseOptionEntity);

        return testcaseOptionEntity;
    }

    private void defaultValueCreateTestCaseOption(TestcaseOptionEntity testcaseOptionEntity, ContextInfo contextInfo) throws InvalidParameterException {
        if (!StringUtils.hasLength(testcaseOptionEntity.getId())) {
            testcaseOptionEntity.setId(UUID.randomUUID().toString());
        }
        testcaseOptionEntity.setState(TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_ACTIVE);
        TestcaseOptionCriteriaSearchFilter searchFilter = new TestcaseOptionCriteriaSearchFilter();

        testcaseOptionEntity.setRank(1);
        if (testcaseOptionEntity.getTestcase() != null) {
            searchFilter.setTestcaseId(testcaseOptionEntity.getTestcase().getId());
            List<TestcaseOptionEntity> testCaseOptions = this.searchTestcaseOptions(searchFilter, Constant.SINGLE_PAGE_SORT_BY_RANK, contextInfo).getContent();
            if (!testCaseOptions.isEmpty()) {
                testcaseOptionEntity.setRank(testCaseOptions.get(0).getRank() + 1);
            }
        }
    }
}
