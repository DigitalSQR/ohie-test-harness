package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseVariableCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseVariableEntity;

import java.util.List;

/**
 * This interface provides contract for TestcaseVariables API.
 *
 * @author Aastha
 */
public interface TestcaseVariableService {

    List<TestcaseVariableEntity> searchTestcaseVariables(TestcaseVariableCriteriaSearchFilter testcaseVariablesCriteriaSearchFilter, ContextInfo contextInfo) throws OperationFailedException, InvalidParameterException;


    /**
     * get test case variable by component id
     *
     * @param componentId  ComponentId of Testcase to be retrieved
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
    public List<TestcaseVariableEntity> getTestcaseVariablesByComponentId(String componentId, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException;

    /**
     * Retrieves a TestcaseVariables corresponding to the given TestcaseVariableId.
     *
     * @param testcaseVariableId  testcaseVariableId of TestcaseVariables to be retrieved
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return a list of Testcase
     * @throws DoesNotExistException     a TestcaseId in TestcaseIds not found
     * @throws InvalidParameterException invalid contextInfo
     */
    public TestcaseVariableEntity getTestcaseVariableById(String testcaseVariableId,
                                                           ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException, OperationFailedException;
}
