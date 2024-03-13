package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * This interface provides contract for Testcase API.
 *
 * @author Dhruv
 */
public interface TestcaseService {

    /**
     * Creates a new Testcase.In the Testcase Id, Description, and Meta
     * information may not be set in the supplied TestcaseInfo.
     *
     * @param testcaseEntity Testcase
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return TestcaseInfo the Testcase just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException TestcaseInfo or contextInfo is not
     * valid
     * @throws OperationFailedException unable to complete request
     */
    public TestcaseEntity createTestcase(TestcaseEntity testcaseEntity,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException;

    /**
     * Updates an existing Testcase.
     *
     * @param testcaseEntity the new data for the Testcase
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return TestcaseInfo the details of Testcase just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException TestcaseInfo or contextInfo is not
     * valid
     * @throws OperationFailedException unable to complete request
     * @throws VersionMismatchException optimistic locking failure or the action
     * was attempted on an out of date version
     */
    public TestcaseEntity updateTestcase(TestcaseEntity testcaseEntity,
            ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException,
            InvalidParameterException;

    /**
     * Retrieves a list of Testcases corresponding to the given Testcase
     * Name.The returned list may be in any order with unique set.
     *
     * @param testcaseCriteriaSearchFilter
     * @param pageable Contains Index number of the Page, Max size of the single
     * page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return a list of Testcase name start with given TestcaseName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException unable to complete request
     */
    public Page<TestcaseEntity> searchTestcases(TestcaseCriteriaSearchFilter testcaseCriteriaSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Retrieves a list of Testcases corresponding to the given Testcase
     * Name.The returned list may be in any order with unique set.
     *
     * @param testcaseCriteriaSearchFilter
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return a list of Testcase name start with given TestcaseName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException unable to complete request
     */
    public List<TestcaseEntity> searchTestcases(TestcaseCriteriaSearchFilter testcaseCriteriaSearchFilter,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Validates a Testcase.Depending on the value of validationType, this
     * validation could be limited to tests on just the current object and its
     * directly contained sub-objects or expanded to perform all tests related
     * to this object
     *
     * @param validationTypeKey the identifier of the extent of validation
     * @param testcaseEntity the Testcase information to be tested
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return Results Testcase performing the validation
     * @throws InvalidParameterException TestcaseInfo or contextInfo is not
     * valid
     * @throws OperationFailedException unable to complete request
     */
    public List<ValidationResultInfo> validateTestcase(String validationTypeKey,
            TestcaseEntity testcaseEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException;

    /**
     * Retrieves a Testcase corresponding to the given Testcase Id.
     *
     * @param testcaseId TestcaseId of Testcase to be retrieved
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return a list of Testcase
     * @throws DoesNotExistException a TestcaseId in TestcaseIds not found
     * @throws InvalidParameterException invalid contextInfo
     */
    public TestcaseEntity getTestcaseById(String testcaseId,
            ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException;

    /**
     * Change the state of testcase option
     *
     * @param testcaseId TestcaseId of Testcase to be retrieved
     * @param stateKey state type to which test case state to be changed
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return changed state testcase
     * @throws DoesNotExistException a TestcaseId in TestcaseIds not found
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException unable to complete request
     * @throws VersionMismatchException optimistic locking failure or the action
     * was attempted on an out of date version
     */
    public TestcaseEntity changeState(String testcaseId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException;


    /**
     * Change the state of testcase option
     *
     * @param testcaseId  TestcaseId of Testcase to be retrieved
     * @param rank    rank to which test case rank to be changed
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return changed state testcase
     * @throws DoesNotExistException        a TestcaseId in TestcaseIds not found
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    invalid contextInfo
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public TestcaseEntity changeRank(String testcaseId, Integer rank, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException;

}
