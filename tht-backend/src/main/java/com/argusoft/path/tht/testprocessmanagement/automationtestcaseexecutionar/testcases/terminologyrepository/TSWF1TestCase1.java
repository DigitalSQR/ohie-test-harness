package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.terminologyrepository;

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


@Component
public class TSWF1TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(TSWF1TestCase1.class);
    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException {

        try {
            LOGGER.info("Start testing TSWF1TestCase1");
            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_TERMINOLOGY_SERVICE_ID);
            if (client == null) {
                return new ValidationResultInfo("testTSWF1Case1", ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            LOGGER.info("Creating codeSystems");

            Bundle codeSystemList =client.search()
                    .forResource(CodeSystem.class)
                    .where(CodeSystem.URL.matches().value("http://example.com/mycodesystem"))
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
            CodeSystem codeSystem= FHIRUtils.createCodeSystem("http://example.com/mycodesystem","1.0.0","MyCodeSystem","Example Code System","ACTIVE","HL7 International / Terminology Infrastructure","COMPLETE","12345","Blood Pressure","A measurement of the force of blood against the walls of the arteries.");
            MethodOutcome outcome=client.create()
                    .resource(codeSystem)
                    .execute();

            // check if codeSystem got created
            if(!outcome.getCreated()){
                return new ValidationResultInfo("testTSWF1Case1", ErrorLevel.ERROR, "Failed to create codeSystem");
            }

            //check if code exist or not
            String codeSystemUrl="http://example.com/mycodesystem";
            String code = "12345";

            // create parameter for $validate-code operation
            UriType url=new UriType(codeSystemUrl);
            CodeType codeType = new CodeType(code);
            Parameters parameters=new Parameters();
            parameters.addParameter().setName("url").setValue(url);
            parameters.addParameter().setName("code").setValue(codeType);

            //Perform $validate-code operation
            Parameters result = client
                    .operation()
                    .onType(CodeSystem.class)
                    .named("$validate-code")
                    .withParameters(parameters)
                    .execute();

            // comparing result
            if(isCodePresent(codeSystem,code)==result.getParameterBool("result")){
                LOGGER.info("testTSWF5Case1 Testcase successfully passed!");
                return new ValidationResultInfo("testTSWF1Case1", ErrorLevel.OK, "Passed");

            }
            else
                return new ValidationResultInfo("testTSWF1Case1", ErrorLevel.ERROR, "Unable to check if code present or not");


        }
        catch(Exception ex){
            LOGGER.error("Exception while TSWF5TestCase1 ", ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }

    }

    public static boolean isCodePresent(CodeSystem codeSystem,String code){
        for(CodeSystem.ConceptDefinitionComponent concept :codeSystem.getConcept()){
            if(code.equals(concept.getCode())){
                return true;
            }
        }
        return false;
    }

}



