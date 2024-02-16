package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This interface provides contract for TestcaseOption API.
 *
 * @author Dhruv
 */
public interface TestcaseOptionService {

    /**
     * Creates a new TestcaseOption.In the TestcaseOption Id, Description, and Meta information may
     * not be set in the supplied TestcaseOptionInfo.
     *
     * @param testcaseOptionEntity TestcaseOption
     * @param contextInfo          information containing the principalId and locale
     *                             information about the caller of service operation
     * @return TestcaseOptionInfo the TestcaseOption just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestcaseOptionInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     */
    public TestcaseOptionEntity createTestcaseOption(TestcaseOptionEntity testcaseOptionEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException;

    /**
     * Updates an existing TestcaseOption.
     *
     * @param testcaseOptionEntity the new data for the TestcaseOption
     * @param contextInfo          information containing the principalId and locale
     *                             information about the caller of service operation
     * @return TestcaseOptionInfo the details of TestcaseOption just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestcaseOptionInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public TestcaseOptionEntity updateTestcaseOption(TestcaseOptionEntity testcaseOptionEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException,
            InvalidParameterException;

    /**
     * Retrieves a list of TestcaseOptions corresponding to the given TestcaseOption Name.The
     * returned list may be in any order with unique set.
     *
     * @param testcaseOptionCriteriaSearchFilter
     * @param pageable                           Contains Index number of the Page, Max size of the single
     *                                           page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo                        information containing the principalId and locale
     *                                           information about the caller of service operation
     * @return a list of TestcaseOption name start with given TestcaseOptionName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<TestcaseOptionEntity> searchTestcaseOptions(TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter,
                                                            Pageable pageable,
                                                            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Retrieves a list of TestcaseOptions corresponding to the given TestcaseOption Name.The
     * returned list may be in any order with unique set.
     *
     * @param testcaseOptionCriteriaSearchFilter
     * @param contextInfo                        information containing the principalId and locale
     *                                           information about the caller of service operation
     * @return a list of TestcaseOption name start with given TestcaseOptionName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public List<TestcaseOptionEntity> searchTestcaseOptions(TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter,
                                                            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Validates a TestcaseOption.Depending on the value of validationType, this
     * validation could be limited to tests on just the current object and its
     * directly contained sub-objects or expanded to perform all tests related
     * to this object
     *
     * @param validationTypeKey    the identifier of the extent of validation
     * @param testcaseOptionEntity the TestcaseOption information to be tested
     * @param contextInfo          information containing the principalId and locale
     *                             information about the caller of service operation
     * @return Results TestcaseOption performing the validation
     * @throws InvalidParameterException TestcaseOptionInfo or contextInfo is not valid
     * @throws OperationFailedException  unable to complete request
     */
    public List<ValidationResultInfo> validateTestcaseOption(String validationTypeKey,
                                                             TestcaseOptionEntity testcaseOptionEntity,
                                                             ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException;

    /**
     * Retrieves a TestcaseOption corresponding to the given TestcaseOption Id.
     *
     * @param testcaseOptionId TestcaseOptionId of TestcaseOption to be retrieved
     * @param contextInfo      information containing the principalId and locale
     *                         information about the caller of service operation
     * @return a list of TestcaseOption
     * @throws DoesNotExistException     a TestcaseOptionId in TestcaseOptionIds not found
     * @throws InvalidParameterException invalid contextInfo
     */
    public TestcaseOptionEntity getTestcaseOptionById(String testcaseOptionId,
                                                      ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException;

    public TestcaseOptionEntity changeState(String testcaseOptionId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException;

}
