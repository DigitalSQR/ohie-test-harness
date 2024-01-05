/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
            DataValidationErrorException;

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
     * @param ids                     list of ids to retrieve
     * @param testRequestSearchFilter
     * @param pageable                Contains Index number of the Page, Max size of the single
     *                                page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo             information containing the principalId and locale
     *                                information about the caller of service operation
     * @return a list of TestRequest name start with given TestRequestName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<TestRequestEntity> searchTestRequests(List<String> ids,
                                                      TestRequestSearchFilter testRequestSearchFilter,
                                                      Pageable pageable,
                                                      ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

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
     * Retrieves a list of TestRequests.The returned list may be in any order with
     * unique set.
     *
     * @param pageable    Contains Index number of the Page, Max size of the single
     *                    page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return a list of TestRequest
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<TestRequestEntity> getTestRequests(Pageable pageable,
                                                   ContextInfo contextInfo)
            throws InvalidParameterException;

    /**
     * start automation testing process.
     *
     * @param testRequestId testRequestWhich needs to be tested
     * @param contextInfo   information containing the principalId and locale
     *                      information about the caller of service operation
     * @return message
     */
    public void startAutomationTestingProcess(String testRequestId, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException;

    /**
     * start manual testing process.
     *
     * @param testRequestId testRequestWhich needs to be tested
     * @param contextInfo   information containing the principalId and locale
     *                      information about the caller of service operation
     * @return message
     */
    public void startManualTestingProcess(String testRequestId, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException, DoesNotExistException, VersionMismatchException;
}