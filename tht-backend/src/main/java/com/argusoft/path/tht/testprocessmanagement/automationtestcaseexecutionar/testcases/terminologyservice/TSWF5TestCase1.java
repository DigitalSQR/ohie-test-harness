package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.terminologyservice;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class TSWF5TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(TSWF5TestCase1.class);
    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException {

        try {
            LOGGER.info("Start testing TSWF5TestCase1");
        IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_TERMINOLOGY_SERVICE_ID);
        if (client == null) {
            return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
        }

        LOGGER.info("Creating codeSystems");
        //creating codeSystems
        if(!Objects.requireNonNull(addCodeSystem(client, "http://example.com/blood-pressure", "1.0.0", "BloodPressure", "Blood Pressure Codes", "ACTIVE", "HL7 International / Terminology Infrastructure", "COMPLETE", "systolic", "Systolic Pressure", "The pressure in the arteries when the heart muscle contracts.")).getCreated())
            return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create codeSystem");

        if(!Objects.requireNonNull(addCodeSystem(client, "http://example.com/gender", "1.0.0", "Gender", "Codes for gender", "ACTIVE", "HL7 International / Terminology Infrastructure", "COMPLETE", "male", "Male", "HL7-defined gender codes")).getCreated())
            return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create codeSystem");

        // Publisher name to search for
        String publisherName = "HL7 International / Terminology Infrastructure";

        // Use $search to search for CodeSystem by publisher
        Bundle searchResults = client
                .search()
                .forResource(CodeSystem.class)
                .where(CodeSystem.PUBLISHER.matches().value(publisherName))
                .returnBundle(Bundle.class)
                .execute();

        // checking if it contains codeSystem with particular publisherName
        if(!isCodeSystemPresent(searchResults,"http://example.com/blood-pressure", "1.0.0") && !isCodeSystemPresent(searchResults,"http://example.com/gender", "1.0.0"))
            return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to search codeSystem by publisher name");

        // Pass the test case if all the above conditions are passed
        LOGGER.info("testTSWF5Case1 Testcase successfully passed!");
        return new ValidationResultInfo(ErrorLevel.OK, "Passed");

    } catch (Exception ex) {
        LOGGER.error("Exception while TSWF5TestCase1 ", ex);
        throw new OperationFailedException(ex.getMessage(), ex);
    }
}

    public static MethodOutcome addCodeSystem(IGenericClient client, String url, String version, String name, String title, String status, String publisher, String content, String code, String display, String definition){

        // checking if codeSystem exist or not
        Bundle codeSystemList =client.search()
                .forResource(CodeSystem.class)
                .where(CodeSystem.URL.matches().value(url))
                .and(CodeSystem.VERSION.exactly().code(version))
                .returnBundle(Bundle.class)
                .execute();


        // checking if any element present in codeSystemList and deleting it
        if(codeSystemList.hasEntry()){
            for(Bundle.BundleEntryComponent entry : codeSystemList.getEntry()){
                CodeSystem codeSystem = (CodeSystem) entry.getResource();
                String id = codeSystem.getIdElement().getIdPart();
                client.delete().resourceById("CodeSystem",id).execute();
            }
        }

        // creating a codeSystem
            CodeSystem codeSystem= FHIRUtils.createCodeSystem(url,version,name,title,status,publisher,content,code,display,definition);
            MethodOutcome outcome=client.create()
                    .resource(codeSystem)
                    .execute();
            return outcome;

    }
    public static boolean isCodeSystemPresent(Bundle searchResults,String url,String version){
        if(searchResults.hasEntry()){
            for(Bundle.BundleEntryComponent entry : searchResults.getEntry()){
                CodeSystem codeSystem = (CodeSystem) entry.getResource();
                if(codeSystem.getUrl().equalsIgnoreCase(url) && codeSystem.getVersion().matches(version)){
                    return true;
                }
            }
        }
        return false;
    }

}