package com.argusoft.path.tht.reportmanagement.service;

import com.argusoft.path.tht.reportmanagement.filter.TestResultRelationCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TestResultRelationService {

    /**
     * Creates a new TestcaseResult.In the TestcaseResult Id, Description, and Meta information may
     * not be set in the supplied TestcaseResultInfo.
     *
     * @param testResultRelationEntity TestcaseResult
     * @param contextInfo          information containing the principalId and locale
     *                             information about the caller of service operation
     * @return TestcaseResultInfo the TestcaseResult just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestcaseResultInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     */
    public TestResultRelationEntity createTestcaseResult(TestResultRelationEntity testResultRelationEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException;

    /**
     * Updates an existing TestcaseResult.
     *
     * @param testResultRelationEntity the new data for the TestcaseResult
     * @param contextInfo          information containing the principalId and locale
     *                             information about the caller of service operation
     * @return TestcaseResultInfo the details of TestcaseResult just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestcaseResultInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public TestResultRelationEntity updateTestcaseResult(TestResultRelationEntity testResultRelationEntity,
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
    public Page<TestResultRelationEntity> searchTestResultRelation(TestResultRelationCriteriaSearchFilter testResultRelationCriteriaSearchFilter,
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
    public List<TestResultRelationEntity> searchTestResultRelation(TestResultRelationCriteriaSearchFilter testResultRelationCriteriaSearchFilter,
                                                                   ContextInfo contextInfo)
            throws InvalidParameterException;

    /**
     * Validates a TestcaseResult.Depending on the value of validationType, this
     * validation could be limited to tests on just the current object and its
     * directly contained sub-objects or expanded to perform all tests related
     * to this object
     *
     * @param validationTypeKey    the identifier of the extent of validation
     * @param testResultRelationEntity the TestcaseResult information to be tested
     * @param contextInfo          information containing the principalId and locale
     *                             information about the caller of service operation
     * @return Results TestcaseResult performing the validation
     * @throws InvalidParameterException TestcaseResultInfo or contextInfo is not valid
     * @throws OperationFailedException  unable to complete request
     */
    public List<ValidationResultInfo> validateTestcaseResult(String validationTypeKey,
                                                             TestResultRelationEntity testResultRelationEntity,
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
    public TestResultRelationEntity getTestResultRelationById(String testResultRelationId,
                                                              ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException;


    public List<Object> getTestResultRelationEntitiesFromAuditMapping(List<String> resultRelationIds,
                                                                      ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException, OperationFailedException, DataValidationErrorException;


    public List<Object> getTestResultRelationEntitiesFromAuditMapping(String testcaseResultId, String refObjectUri, ContextInfo contextInfo)
            throws InvalidParameterException, DoesNotExistException,
            DataValidationErrorException, OperationFailedException;

}
