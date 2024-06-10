package com.argusoft.path.tht.testprocessmanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestValueCriteriaSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestValueEntity;

import java.util.List;

/**
 * This interface provides contract for TestRequestValue API.
 *
 * @author Aastha
 */
public interface TestRequestValueService {


    /**
     * Retrieves a list of TestcaseVariable corresponding to the given Testcase
     * Name.The returned list may be in any order with unique set.
     *
     * @param testRequestValueCriteriaSearchFilter
     * @param contextInfo                  information containing the principalId and locale
     *                                     information about the caller of service operation
     * @return a list of Testcase name start with given TestcaseName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */

    List<TestRequestValueEntity> searchTestRequestValues(TestRequestValueCriteriaSearchFilter testRequestValueCriteriaSearchFilter, ContextInfo contextInfo) throws OperationFailedException, InvalidParameterException;

    /**
     * Retrieves a TestRequestValue corresponding to the given TestRequestValueId.
     *
     * @param testRequestValueId  testRequestValueId of TestRequestValue to be retrieved
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return a list of Testcase
     * @throws DoesNotExistException     a TestcaseId in TestcaseIds not found
     * @throws InvalidParameterException invalid contextInfo
     */
    public TestRequestValueEntity getTestRequestValueById(String testRequestValueId,
                                                                  ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException, OperationFailedException;


    /**
     * Updates an existing TestRequestValue.
     *
     * @param testRequestValueEntities the new data for the TestRequestValue
     * @param contextInfo    information containing the principalId and locale
     *                       information about the caller of service operation
     * @return TestcaseInfo the details of Testcase just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestcaseInfo or contextInfo is not
     *                                      valid
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public List<TestRequestValueEntity> updateTestRequestValues(List<TestRequestValueEntity> testRequestValueEntities, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, OperationFailedException, DataValidationErrorException, VersionMismatchException;
}

