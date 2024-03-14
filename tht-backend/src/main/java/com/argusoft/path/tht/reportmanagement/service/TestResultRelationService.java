package com.argusoft.path.tht.reportmanagement.service;

import com.argusoft.path.tht.reportmanagement.filter.TestResultRelationCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * This interface provides contract for TestResultRelation API.
 *
 * @author Hardik
 */
public interface TestResultRelationService {

    /**
     * Creates a new TestcaseResultRelation
     *
     * @param testResultRelationEntity TestcaseResultRelation
     * @param contextInfo              information containing the principalId and locale
     *                                 information about the caller of service operation
     * @return TestcaseResultRelation the TestcaseResultRelation just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestcaseResultInfo or contextInfo is
     *                                      not valid
     * @throws OperationFailedException     unable to complete request
     */
    public TestResultRelationEntity createTestcaseResult(TestResultRelationEntity testResultRelationEntity,
                                                         ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException;

    /**
     * Updates an existing TestcaseResultRelation.
     *
     * @param testResultRelationEntity the new data for the
     *                                 TestcaseResultRelation
     * @param contextInfo              information containing the principalId and locale
     *                                 information about the caller of service operation
     * @return TestcaseResultRelation the details of TestcaseResultRelation just
     * updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    TestcaseResultInfo or contextInfo is
     *                                      not valid
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
     * Retrieves a list of TestcaseResultRelation corresponding to the criteria
     * returned list may be in any order with unique set.
     *
     * @param testResultRelationCriteriaSearchFilter criteria to search and
     *                                               filter test result relation
     * @param pageable                               Contains Index number of the Page, Max size of the single
     *                                               page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo                            information containing the principalId and locale
     *                                               information about the caller of service operation
     * @return a list of TestcaseResultRelation
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<TestResultRelationEntity> searchTestResultRelation(TestResultRelationCriteriaSearchFilter testResultRelationCriteriaSearchFilter,
                                                                   Pageable pageable,
                                                                   ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Retrieves a list of TestcaseResultRelation corresponding to the
     * criteria.The returned list may be in any order with unique set.
     *
     * @param testResultRelationCriteriaSearchFilter criteria to search and
     *                                               filter test result relation
     * @param contextInfo                            information containing the principalId and locale
     *                                               information about the caller of service operation
     * @return a list of TestcaseResultRelation
     * @throws InvalidParameterException invalid contextInfo
     */
    public List<TestResultRelationEntity> searchTestResultRelation(TestResultRelationCriteriaSearchFilter testResultRelationCriteriaSearchFilter,
                                                                   ContextInfo contextInfo)
            throws InvalidParameterException;

    /**
     * Validates a TestcaseResultRelation.Depending on the value of
     * validationType, this validation could be limited to tests on just the
     * current object and its directly contained sub-objects or expanded to
     * perform all tests related to this object
     *
     * @param validationTypeKey        the identifier of the extent of validation
     * @param testResultRelationEntity the TestcaseResult information to be
     *                                 tested
     * @param contextInfo              information containing the principalId and locale
     *                                 information about the caller of service operation
     * @return Results TestcaseResultRelation performing the validation
     * @throws InvalidParameterException TestcaseResultInfo or contextInfo is
     *                                   not valid
     * @throws OperationFailedException  unable to complete request
     */
    public List<ValidationResultInfo> validateTestcaseResult(String validationTypeKey,
                                                             TestResultRelationEntity testResultRelationEntity,
                                                             ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException, DataValidationErrorException;

    /**
     * Retrieves a TestcaseResultRelation corresponding to the given
     * TestcaseResultRelation id.
     *
     * @param testResultRelationId TestcaseResultRelationId of
     *                             TestcaseResultRelation to be retrieved
     * @param contextInfo          information containing the principalId and locale
     *                             information about the caller of service operation
     * @return TestResultRelation
     * @throws DoesNotExistException     a TestcaseResultRelationId in
     *                                   TestcaseResultRelationIds not found
     * @throws InvalidParameterException invalid contextInfo
     */
    public TestResultRelationEntity getTestResultRelationById(String testResultRelationId,
                                                              ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException;

    /**
     * Retrieves list of object of TestResultRelation from audit mapping
     *
     * @param resultRelationIds list of result relation ids
     * @param contextInfo       information containing the principalId and locale
     *                          information about the caller of service operation
     * @return list of object from audit mapping
     * @throws DoesNotExistException        TestcaseResultRelationId in
     *                                      TestcaseResultRelationIds not found
     * @throws InvalidParameterException    invalid contextInfo
     * @throws OperationFailedException     unable to complete request
     * @throws DataValidationErrorException supplied data is invalid
     */
    public List<Object> getTestResultRelationEntitiesFromAuditMapping(List<String> resultRelationIds,
                                                                      ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException, OperationFailedException, DataValidationErrorException;

    /**
     * Retrieves list of object of TestResultRelation from audit mapping
     *
     * @param testcaseResultId id of testcase result
     * @param refObjectUri     refObjectUri
     * @param contextInfo      information containing the principalId and locale
     *                         information about the caller of service operation
     * @return list of object from audit mapping
     * @throws InvalidParameterException    invalid contextInfo
     * @throws DoesNotExistException        TestcaseResultId in TestcaseResultIds not
     *                                      found
     * @throws DataValidationErrorException supplied data is invalid
     * @throws OperationFailedException     unable to complete request
     */
    public List<Object> getTestResultRelationEntitiesFromAuditMapping(String testcaseResultId, String refObjectUri, ContextInfo contextInfo)
            throws InvalidParameterException, DoesNotExistException,
            DataValidationErrorException, OperationFailedException;

}
