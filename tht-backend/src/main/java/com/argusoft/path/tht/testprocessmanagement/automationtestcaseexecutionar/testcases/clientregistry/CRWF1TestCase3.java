package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientregistry;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementation of the CRWF1TestCase1.
 *
 * @author Dhruv
 */
@Component
public class CRWF1TestCase3 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(CRWF1TestCase3.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            System.out.println("=========================================CRWF1Testcase3");
            String[] args = {"--glue",
                    "com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientregistry",
                    "classpath:feature/CRWF1Testcase1.feature"};
            io.cucumber.core.cli.Main.run(args, Thread.currentThread().getContextClassLoader());
        } catch (Throwable t) {
            System.out.println("=========================================" + t.getMessage());
            t.printStackTrace();
            return new ValidationResultInfo("testCRWF1Case3", ErrorLevel.ERROR, "Failed to create patient");
        }
        return new ValidationResultInfo("testCRWF1Case3", ErrorLevel.OK, "Passed");
    }
}
