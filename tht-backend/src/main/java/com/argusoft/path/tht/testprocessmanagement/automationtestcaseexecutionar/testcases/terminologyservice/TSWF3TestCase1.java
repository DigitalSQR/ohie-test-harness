package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.terminologyservice;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
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
public class TSWF3TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(TSWF3TestCase1.class);
    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                 ContextInfo contextInfo) throws OperationFailedException {

        try{

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_TERMINOLOGY_SERVICE_ID);
            if (client == null) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            String codeSystemVersion="1.0.0";

            Bundle codeSystemList =client.search()
                    .forResource(CodeSystem.class)
                    .where(CodeSystem.URL.matches().value("http://example.com/mycodesystem"))
                    .and(CodeSystem.VERSION.exactly().code(codeSystemVersion))
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
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create codeSystem");
            }

            //search for value set
            Bundle searchValueSet = client
                    .search()
                    .forResource(ValueSet.class)
                    .where(new StringClientParam("url").matches().value("http://hl7.org/fhir/ValueSet/example"))
                    .returnBundle(Bundle.class)
                    .execute();

            // checking if any element present in codeSystemList and deleting it
            if(searchValueSet.hasEntry()){
                for(Bundle.BundleEntryComponent entry : searchValueSet.getEntry()){
                    ValueSet valueSet = (ValueSet) entry.getResource();
                    String id = valueSet.getIdElement().getIdPart();
                    client.delete().resourceById("ValueSet",id).execute();
                }
            }

            //create value set
            ValueSet valueSet = FHIRUtils.createValueSet("http://hl7.org/fhir/ValueSet/example", "LOINCCodesForCholesterolInSerumPlasma", "LOINC Codes for Cholesterol in Serum/Plasma", "ACTIVE");

            // Add a concept to the ValueSet
            FHIRUtils.addConceptValueSet(valueSet, "http://example.com/mycodesystem", "123", "Example Display");

            MethodOutcome valueSetOutcome=client.create()
                    .resource(valueSet)
                    .execute();

            // check if valueSet got created
            if(!valueSetOutcome.getCreated()){
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create valueSet");
            }


            // create parameter for $validate-code operation
            UriType url=new UriType("http://hl7.org/fhir/ValueSet/example");
            Parameters parameters=new Parameters();
            parameters.addParameter().setName("url").setValue(url);

            //expand and check the value set count
            //Perform $expand operation
            ValueSet result = client
                    .operation()
                    .onType(ValueSet.class)
                    .named("$expand")
                    .withParameter(Parameters.class, "url", url)
                    .returnResourceType(ValueSet.class)
                    .execute();

            if(result.getCompose().getInclude().size() == 1){
                return new ValidationResultInfo(ErrorLevel.OK, "Passed");
            }

            return new ValidationResultInfo(ErrorLevel.ERROR, "cannot expand the concept set");

        } catch (Exception e) {
            LOGGER.error(ValidateConstant.EXCEPTION + TSWF3TestCase1.class.getSimpleName(), e);
            throw new OperationFailedException(e.getMessage(), e);
        }
    }
}
