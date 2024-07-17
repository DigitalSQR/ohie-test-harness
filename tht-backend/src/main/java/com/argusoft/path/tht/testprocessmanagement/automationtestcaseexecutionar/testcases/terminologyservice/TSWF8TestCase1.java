//package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.terminologyservice;
//
//import ca.uhn.fhir.rest.api.MethodOutcome;
//import ca.uhn.fhir.rest.client.api.IGenericClient;
//import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
//import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
//import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
//import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
//import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
//import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
//import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
//import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
//import org.hl7.fhir.r4.model.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
///**
// * Testcase For TSWF8TestCase1
// *
// * @author Nirabhra
// */
//
//@Component
//public class TSWF8TestCase1 implements TestCase {
//
//    public static final Logger LOGGER = LoggerFactory.getLogger(TSWF8TestCase1.class);
//
//    @Override
//    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap, ContextInfo contextInfo) throws OperationFailedException {
//        try {
//            String tableName = "testTSWF8Case1";
//            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_TERMINOLOGY_SERVICE_ID);
//
//            if (client == null) {
//                LOGGER.error("Failed to get IGenericClient");
//                return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Failed to get IGenericClient");
//            }
//
//            String codeSystemVersion = "1.0.0";
//            LOGGER.info("Searching for code systems that already exist with test url");
//            //Searching for code systems with test url
//            Bundle codeSystemList = client.search()
//                    .forResource(CodeSystem.class)
//                    .where(CodeSystem.URL.matches().value("http://example.com/example/mycodesystem"))
//                    .and(CodeSystem.VERSION.exactly().code(codeSystemVersion))
//                    .returnBundle(Bundle.class)
//                    .execute();
//
//            LOGGER.info("Checking if any element is present in codeSystemList and deleting it");
//            // checking if any element present in codeSystemList and deleting it
//            if (codeSystemList.hasEntry()) {
//                for (Bundle.BundleEntryComponent entry : codeSystemList.getEntry()) {
//                    CodeSystem codeSystem = (CodeSystem) entry.getResource();
//                    String id = codeSystem.getIdElement().getIdPart();
//                    client.delete().resourceById("CodeSystem", id).execute();
//                }
//            }
//
//            LOGGER.info("Checking if any element is still present after deleting");
//            //Searching if data still remained after deleting
//            Bundle testCodeSystemList = client.search()
//                    .forResource(CodeSystem.class)
//                    .where(CodeSystem.URL.matches().value("http://example.com/example/mycodesystem"))
//                    .and(CodeSystem.VERSION.exactly().code(codeSystemVersion))
//                    .returnBundle(Bundle.class)
//                    .execute();
//
//            //Check if data remained after deletion
//            if (testCodeSystemList.hasEntry()) {
//                LOGGER.error("First Code System Deletion failed");
//                return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "First Code System Deletion failed");
//            }
//
//            LOGGER.info("Creating a code system with test url");
//            //Creating a code system with test url
//            CodeSystem codeSystem = FHIRUtils.createCodeSystem("http://example.com/example/mycodesystem", "1.0.0", "MyCodeSystem", "Example Code System", "ACTIVE", "HL7 International / Terminology Infrastructure", "COMPLETE", "12345", "Blood Pressure", "A measurement of the force of blood against the walls of the arteries.");
//            MethodOutcome outcome = client.create()
//                    .resource(codeSystem)
//                    .execute();
//
//            //Check if first code system is being created
//            if (Boolean.FALSE.equals(outcome.getCreated())) {
//                LOGGER.error("Failed to create first code system");
//                return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Failed to create first code system");
//            }
//
//            String codeSystemVersion2 = "2.0.0";
//
//            LOGGER.info("Searching for code system with a second test url");
//            //Searching for code system with a second test url
//            Bundle codeSystemList2 = client.search()
//                    .forResource(CodeSystem.class)
//                    .where(CodeSystem.URL.matches().value("http://example.com/example/mycodesystem2"))
//                    .and(CodeSystem.VERSION.exactly().code(codeSystemVersion2))
//                    .returnBundle(Bundle.class)
//                    .execute();
//
//            LOGGER.info("Checking if any element present in codeSystemList and deleting it");
//            // checking if any element present in codeSystemList2 and deleting it
//            if (codeSystemList2.hasEntry()) {
//                for (Bundle.BundleEntryComponent entry : codeSystemList2.getEntry()) {
//                    CodeSystem codeSystem2 = (CodeSystem) entry.getResource();
//                    String id = codeSystem2.getIdElement().getIdPart();
//                    client.delete().resourceById("CodeSystem", id).execute();
//                }
//            }
//
//            LOGGER.info("Checking if any element is still present after deleting");
//            //Searching if data remained after deletion
//            Bundle testCodeSystemList2 = client.search()
//                    .forResource(CodeSystem.class)
//                    .where(CodeSystem.URL.matches().value("http://example.com/example/mycodesystem"))
//                    .and(CodeSystem.VERSION.exactly().code(codeSystemVersion))
//                    .returnBundle(Bundle.class)
//                    .execute();
//
//            //Checking if data remained after deletion
//            if (testCodeSystemList2.hasEntry()) {
//                LOGGER.error("Second Code System Deletion failed");
//                return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Second Code System Deletion failed");
//            }
//
//            LOGGER.info("Creating a code system with second test url");
//            //Creating a code system with second test url
//            CodeSystem codeSystem2 = FHIRUtils.createCodeSystem("http://example.com/example/mycodesystem2", "2.0.0", "MyCodeSystem2", "Example Code System2", "ACTIVE", "HL7 International / Terminology Infrastructure", "COMPLETE", "abcde", "BP", "A measurement of the force of blood against the walls of the arteries.");
//            MethodOutcome outcome2 = client.create()
//                    .resource(codeSystem2)
//                    .execute();
//
//            //Check if first code system is being created
//            if (Boolean.FALSE.equals(outcome2.getCreated())) {
//                LOGGER.error("Failed to create second code system");
//                return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Failed to create second code system");
//            }
//
//            LOGGER.info("Searching for concept map with test url");
//            //Searching for concept map with test url
//            Bundle conceptMapBundle = client.search()
//                    .forResource(ConceptMap.class)
//                    .where(ConceptMap.URL.matches().value("http://example.com/testing/myconceptmap"))
//                    .returnBundle(Bundle.class)
//                    .execute();
//
//            LOGGER.info("Checking if any element is present in the conceptMapBundle and deleting it");
//            //Checking if any element is present in the conceptMapBundle and deleting it
//            if (conceptMapBundle.hasEntry()) {
//                for (Bundle.BundleEntryComponent entry : conceptMapBundle.getEntry()) {
//                    ConceptMap mapEntry = (ConceptMap) entry.getResource();
//                    String id = mapEntry.getIdElement().getIdPart();
//                    client.delete().resourceById("ConceptMap", id).execute();
//                }
//            }
//
//            LOGGER.info("Searching for concept map with test url after deletion");
//            //Searching for concept map with test url after deletion
//            Bundle testConceptMapBundle = client.search()
//                    .forResource(ConceptMap.class)
//                    .where(ConceptMap.URL.matches().value("http://example.com/testing/myconceptmap"))
//                    .returnBundle(Bundle.class)
//                    .execute();
//
//            //Checking if data remained after deletion
//            if (testConceptMapBundle.hasEntry()) {
//                LOGGER.error("Concept Map Deletion failed");
//                return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Concept Map Deletion failed");
//            }
//
//            LOGGER.info("Creating concept map with test url");
//            //Creating concept map with test url
//            ConceptMap conceptMap = FHIRUtils.createConceptMap("MyConceptMap", "http://example.com/testing/myconceptmap", "ACTIVE", "http://example.com/example/mycodesystem", "http://example.com/example/mycodesystem2", "12345", "abcde", "Blood Pressure", "BP");
//
//            MethodOutcome conceptMapOutcome = client.create()
//                    .resource(conceptMap)
//                    .execute();
//
//            //Check if first code system is being created
//            if (Boolean.FALSE.equals(conceptMapOutcome.getCreated())) {
//                LOGGER.error("Failed to create concept map");
//                return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Failed to create concept map");
//            }
//
//            LOGGER.info("Adding parameters for $translate function");
//            //Adding parameters for $translate function
//            Parameters parameters = new Parameters();
//            parameters.addParameter().setName("url").setValue(new UriType("http://example.com/testing/myconceptmap"));
//            parameters.addParameter().setName("code").setValue(new StringType("12345"));
//            parameters.addParameter().setName("system").setValue((new UriType("http://example.com/example/mycodesystem")));
//
//            LOGGER.info("Executing $translate operation");
//            //Executing $translate operation
//            Parameters result = client
//                    .operation()
//                    .onType(ConceptMap.class)
//                    .named("$translate")
//                    .withParameters(parameters)
//                    .execute();
//
//            //Check if first code system is being created
//            if (result.isEmpty()) {
//                LOGGER.error("Empty result of $translate operation");
//                return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Empty result of $translate operation");
//            }
//
//            if (result.getParameter().size() == 2) {
//                if (!(((BooleanType) result.getParameter().get(0).getValue()).booleanValue())) {
//                    LOGGER.error("Result is true even though there are only 2 parameters in result list");
//                    return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Result is true even though there are only 2 parameters in result list");
//                }
//                if (!(((StringType) result.getParameter().get(1).getValue()).getValue().equals("Matches found"))) {
//                    LOGGER.error("Parameter list has 2 entries even though matches are found");
//                    return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Parameter list has 3 entries even though no matches are found");
//                }
//            } else {
//                if (result.getParameter().size() != 3) {
//                    LOGGER.error("Invalid result list size");
//                    return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Invalid result list size");
//                }
//                if (!((BooleanType) result.getParameter().get(0).getValue()).booleanValue()) {
//                    LOGGER.error("Result is false even though there are 3 parameters in result list");
//                    return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Result is false even though there are 3 parameters in result list");
//                }
//                if (!(((StringType) result.getParameter().get(1).getValue()).getValue().equals("Matches found"))) {
//                    LOGGER.error("Parameter list has 3 entries even though no matches are found");
//                    return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Parameter list has 3 entries even though no matches are found");
//                }
//                if (result.getParameter().get(2).getPart().size() != 3) {
//                    LOGGER.error("Invalid match list size");
//                    return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Invalid match list size");
//                }
//                if (!((result.getParameter().get(2).getPart().get(2).getValue()).toString().equals("UriType[http://example.com/testing/myconceptmap]"))) {
//                    LOGGER.error("Source URI is not the concept map URI that was created");
//                    return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Source URI is not the concept map URI that was created");
//                }
//                if (!(checkCoding((Coding) result.getParameter().get(2).getPart().get(1).getValue(), new Coding("http://example.com/example/mycodesystem2", "abcde", "BP")))) {
//                    LOGGER.error("Matched code does not match the code inserted");
//                    return new ValidationResultInfo(tableName, ErrorLevel.ERROR, "Matched code does not match the code inserted");
//                }
//
//            }
//
//            return new ValidationResultInfo(tableName, ErrorLevel.OK, "Passed");
//        } catch (Exception e) {
//            LOGGER.error(ValidateConstant.EXCEPTION + TSWF8TestCase1.class.getSimpleName(), e);
//            throw new OperationFailedException(e.getMessage(), e);
//
//        }
//    }
//
//    //To check if two coding type object match(Only system, code and display fields)
//    boolean checkCoding(Coding resultCode, Coding testCode) {
//
//        if (!((resultCode.getSystem()).equals(testCode.getSystem()))) {
//            return false;
//        } else if (!(resultCode.getCode().equals(testCode.getCode()))) {
//            return false;
//        } else {
//            return resultCode.getDisplay().equals(testCode.getDisplay());
//        }
//    }
//}
