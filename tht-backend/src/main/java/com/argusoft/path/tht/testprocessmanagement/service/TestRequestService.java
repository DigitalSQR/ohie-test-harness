package com.argusoft.path.tht.testprocessmanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestCriteriaSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This interface provides contract for TestRequest API.
 *
 * @author Dhruv
 */
public interface TestRequestService {

    /**
     * Creates a new TestRequest.In the TestRequest Id, Description, and Meta information may
     * not be set in the supplied TestRequestInfo.
     *
     * @param testRequestEntity TestRequest
     * @param contextInfo       information containing the principalId and locale
     *                          information about the caller of service operation
     * @return TestRequestInfo the TestRequest just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestRequestInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     */
    public TestRequestEntity createTestRequest(TestRequestEntity testRequestEntity,
                                               ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException;

    /**
     * Updates an existing TestRequest.
     *
     * @param testRequestEntity the new data for the TestRequest
     * @param contextInfo       information containing the principalId and locale
     *                          information about the caller of service operation
     * @return TestRequestInfo the details of TestRequest just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestRequestInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public TestRequestEntity updateTestRequest(TestRequestEntity testRequestEntity,
                                               ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException,
            InvalidParameterException;

    /**
     * Retrieves a list of TestRequests corresponding to the given TestRequest Name.The
     * returned list may be in any order with unique set.
     *
     * @param testRequestSearchFilter
     * @param pageable                Contains Index number of the Page, Max size of the single
     *                                page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo             information containing the principalId and locale
     *                                information about the caller of service operation
     * @return a list of TestRequest name start with given TestRequestName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<TestRequestEntity> searchTestRequests(TestRequestCriteriaSearchFilter testRequestSearchFilter,
                                                      Pageable pageable,
                                                      ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException, DoesNotExistException;


    /**
     * Retrieves a list of TestRequests corresponding to the given TestRequest Name.The
     * returned list may be in any order with unique set.
     *
     * @param testRequestSearchFilter
     * @param contextInfo             information containing the principalId and locale
     *                                information about the caller of service operation
     * @return a list of TestRequest name start with given TestRequestName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public List<TestRequestEntity> searchTestRequests(TestRequestCriteriaSearchFilter testRequestSearchFilter,
                                                      ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException, DoesNotExistException;

    /**
     * Validates a TestRequest.Depending on the value of validationType, this
     * validation could be limited to tests on just the current object and its
     * directly contained sub-objects or expanded to perform all tests related
     * to this object
     *
     * @param validationTypeKey the identifier of the extent of validation
     * @param testRequestEntity the TestRequest information to be tested
     * @param contextInfo       information containing the principalId and locale
     *                          information about the caller of service operation
     * @return Results TestRequest performing the validation
     * @throws InvalidParameterException TestRequestInfo or contextInfo is not valid
     * @throws OperationFailedException  unable to complete request
     */
    public List<ValidationResultInfo> validateTestRequest(String validationTypeKey,
                                                          TestRequestEntity testRequestEntity,
                                                          ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException;

    /**
     * Retrieves a TestRequest corresponding to the given TestRequest Id.
     *
     * @param testRequestId TestRequestId of TestRequest to be retrieved
     * @param contextInfo   information containing the principalId and locale
     *                      information about the caller of service operation
     * @return a list of TestRequest
     * @throws DoesNotExistException     a TestRequestId in TestRequestIds not found
     * @throws InvalidParameterException invalid contextInfo
     */
    public TestRequestEntity getTestRequestById(String testRequestId,
                                                ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException;

    /**
     * reinitialize automation testing process by killing process and make testcase results draft again.
     *
     * @param testRequestId testRequestWhich needs to be tested
     * @param isAutomated
     * @param contextInfo   information containing the principalId and locale
     *                      information about the caller of service operation
     */
    public void reinitializeTestingProcess(
            String testRequestId,
            String refObjUri,
            String refId,
            Boolean isManual,
            Boolean isAutomated,
            Boolean isRequired,
            Boolean isRecommended,
            Boolean isWorkflow,
            Boolean isFunctional,
            ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException;

    /**
     * start manual testing process.
     *
     * @param testRequestId testRequestWhich needs to be tested
     * @param isAutomated
     * @param contextInfo   information containing the principalId and locale
     *                      information about the caller of service operation
     */
    public void startTestingProcess(
            String testRequestId,
            String refObjUri,
            String refId,
            Boolean isManual,
            Boolean isAutomated,
            Boolean isRequired,
            Boolean isRecommended,
            Boolean isWorkflow,
            Boolean isFunctional,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException,
            DataValidationErrorException;


    /**
     * change state of testRequest with id and giving the expected state
     *
     * @param testRequestId id of the testRequest
     * @param stateKey      expected statekey
     * @param contextInfo   ContextInfo
     * @return DocumentEntity
     * @throws DoesNotExistException        when document does not exists for that id
     * @throws DataValidationErrorException when validation fails
     */
    public TestRequestEntity changeState(String testRequestId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException;
}
