package com.argusoft.path.tht.reportmanagement.service;

import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * This interface provides contract for TestcaseResult API.
 *
 * @author Dhruv
 */
public interface TestcaseResultService {

    /**
     * Creates a new TestcaseResult.In the TestcaseResult Id, Description, and Meta information may
     * not be set in the supplied TestcaseResultInfo.
     *
     * @param testcaseResultEntity TestcaseResult
     * @param contextInfo          information containing the principalId and locale
     *                             information about the caller of service operation
     * @return TestcaseResultInfo the TestcaseResult just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestcaseResultInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     */
    public TestcaseResultEntity createTestcaseResult(TestcaseResultEntity testcaseResultEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException, VersionMismatchException;

    /**
     * Updates an existing TestcaseResult.
     *
     * @param testcaseResultEntity the new data for the TestcaseResult
     * @param contextInfo          information containing the principalId and locale
     *                             information about the caller of service operation
     * @return TestcaseResultInfo the details of TestcaseResult just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestcaseResultInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public TestcaseResultEntity updateTestcaseResult(TestcaseResultEntity testcaseResultEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException,
            InvalidParameterException, DoesNotExistException;

    /**
     * Submit TestcaseResult for the manual testing.
     *
     * @param testcaseResultId
     * @param selectedTestcaseOptionIds
     * @param contextInfo               information containing the principalId and locale
     *                                  information about the caller of service operation
     * @return TestcaseResultInfo the details of TestcaseResult just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestcaseResultInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public TestcaseResultEntity submitTestcaseResult(String testcaseResultId,
                                                     Set<String> selectedTestcaseOptionIds,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException,
            InvalidParameterException, DoesNotExistException;

    /**
     * Retrieves a list of TestcaseResults corresponding to the given TestcaseResult Name.The
     * returned list may be in any order with unique set.
     *
     * @param testcaseResultSearchFilter
     * @param pageable                   Contains Index number of the Page, Max size of the single
     *                                   page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo                information containing the principalId and locale
     *                                   information about the caller of service operation
     * @return a list of TestcaseResult name start with given TestcaseResultName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<TestcaseResultEntity> searchTestcaseResults(TestcaseResultCriteriaSearchFilter testcaseResultSearchFilter,
                                                            Pageable pageable,
                                                            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;


    /**
     * Retrieves a list of TestcaseResults corresponding to the given TestcaseResult Name.The
     * returned list may be in any order with unique set.
     *
     * @param testcaseResultSearchFilter
     * @param contextInfo                information containing the principalId and locale
     *                                   information about the caller of service operation
     * @return a list of TestcaseResult name start with given TestcaseResultName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public List<TestcaseResultEntity> searchTestcaseResults(TestcaseResultCriteriaSearchFilter testcaseResultSearchFilter,
                                                            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Validates a TestcaseResult.Depending on the value of validationType, this
     * validation could be limited to tests on just the current object and its
     * directly contained sub-objects or expanded to perform all tests related
     * to this object
     *
     * @param validationTypeKey    the identifier of the extent of validation
     * @param testcaseResultEntity the TestcaseResult information to be tested
     * @param contextInfo          information containing the principalId and locale
     *                             information about the caller of service operation
     * @return Results TestcaseResult performing the validation
     * @throws InvalidParameterException TestcaseResultInfo or contextInfo is not valid
     * @throws OperationFailedException  unable to complete request
     */
    public List<ValidationResultInfo> validateTestcaseResult(String validationTypeKey,
                                                             TestcaseResultEntity testcaseResultEntity,
                                                             ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException, DataValidationErrorException;

    /**
     * Retrieves a TestcaseResult corresponding to the given TestcaseResult Id.
     *
     * @param testcaseResultId TestcaseResultId of TestcaseResult to be retrieved
     * @param contextInfo      information containing the principalId and locale
     *                         information about the caller of service operation
     * @return a list of TestcaseResult
     * @throws DoesNotExistException     a TestcaseResultId in TestcaseResultIds not found
     * @throws InvalidParameterException invalid contextInfo
     */
    public TestcaseResultEntity getTestcaseResultById(String testcaseResultId,
                                                      ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException;


    /**
     * change state of testcaseResult with id and giving the expected state
     *
     * @param testcaseResultId id of the testcaseResult
     * @param stateKey         expected statekey
     * @param contextInfo      ContextInfo
     * @return DocumentEntity
     * @throws DoesNotExistException        when document does not exists for that id
     * @throws DataValidationErrorException when validation fails
     */
    public TestcaseResultEntity changeState(String testcaseResultId, String stateKey, ContextInfo contextInfo)
            throws DoesNotExistException,
            DataValidationErrorException,
            InvalidParameterException, OperationFailedException, VersionMismatchException;

    /**
     * Retrieves a TestcaseResult corresponding to the given filters.
     *
     * @param testcaseResultId TestcaseResultId of TestcaseResult to be retrieved
     * @param isManual
     * @param isAutomated
     * @param isRequired
     * @param isRecommended
     * @param isWorkflow
     * @param isFunctional
     * @param contextInfo      information containing the principalId and locale
     *                         information about the caller of service operation
     * @return a list of TestcaseResult
     * @throws DoesNotExistException     a TestcaseResultId in TestcaseResultIds not found
     * @throws InvalidParameterException invalid contextInfo
     */
    public TestcaseResultEntity getTestcaseResultStatus(
            String testcaseResultId,
            Boolean isManual,
            Boolean isAutomated,
            Boolean isRequired,
            Boolean isRecommended,
            Boolean isWorkflow,
            Boolean isFunctional,
            ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException, OperationFailedException;

    public List<String> getSubClassesNameForTestCase();

    TestcaseResultEntity fetchTestcaseResultStatusByInputs(
            Boolean isManual,
            Boolean isAutomated,
            Boolean isRequired,
            Boolean isRecommended,
            Boolean isWorkflow,
            Boolean isFunctional,
            String testcaseResultEntityId,
            ContextInfo contextInfo)
            throws InvalidParameterException, OperationFailedException, DoesNotExistException;
}
