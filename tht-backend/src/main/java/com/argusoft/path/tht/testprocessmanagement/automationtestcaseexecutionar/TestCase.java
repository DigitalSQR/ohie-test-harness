package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;

/**
 * This interface provides contract for Testcases which can be executed by the Testcase Executioner.
 *
 * @author Dhruv
 */
public interface TestCase {
    public ValidationResultInfo test(
            IGenericClient client,
            ContextInfo contextInfo) throws Exception;
}
