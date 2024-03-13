package com.argusoft.path.tht.reportmanagement.service;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;

import java.util.Optional;

/**
 * This interface provides contract for TestcaseResultAttributes API.
 *
 * @author Bhavi
 */
public interface TestcaseResultAttributesService {

    /**
     * Change test case result attributes if already exist if not then it will
     * create new attributes
     *
     * @param testcaseResultEntity Attribute related to testcase result
     * @param Key Attribute key
     * @param Value Attribute value
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return testcase result attribute
     * @throws InvalidParameterException invalid contextInfo
     */
    public TestcaseResultAttributesEntity createAndChangeTestcaseResultAttributes(TestcaseResultEntity testcaseResultEntity, String Key, String Value,
            ContextInfo contextInfo)
            throws
            InvalidParameterException;

    /**
     * Retrieve test case result attribute
     *
     * @param testcaseResultEntity test case result whose attribute should be
     * retrieved
     * @param key key to get value
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return test case result attribute
     * @throws InvalidParameterException
     */
    Optional<TestcaseResultAttributesEntity> getTestcaseResultAttributes(TestcaseResultEntity testcaseResultEntity, String key, ContextInfo contextInfo)
            throws InvalidParameterException;

    /**
     * Delete test case result attribute after test request is finished
     *
     * @param testcaseResultEntity test case result whose attributes should be
     * deleted
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @throws DoesNotExistException testcase result not found
     */
    public void deleteTestcaseResultAttributesEntities(TestcaseResultEntity testcaseResultEntity, ContextInfo contextInfo)
            throws DoesNotExistException;

}
