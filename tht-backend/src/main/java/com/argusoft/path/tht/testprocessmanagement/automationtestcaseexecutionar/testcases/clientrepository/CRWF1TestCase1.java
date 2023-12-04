package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.springframework.stereotype.Component;

/**
 * Implemantation of the CRWF1 Testcase1 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/create-patient-demographic-record-workflow-1.
 *
 * @author dhruv
 * @since 2023-09-25
 */
@Component
public class CRWF1TestCase1 implements TestCase {
    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            System.out.println("Started testCRWF1Case1");
            System.out.println("Finished testCRWF1Case1");


//instead of here in code, if we wants to store complete testcase code in database then we can do that by below logic
//create table apiTest
//@Entity
//public class ApiTest {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String apiUrl;
//    private String requestBody;
//    private String expectedResponse;
//    private String rank;
//    private String nameToStoreResponseResource;
//    private String testcaseId;
//
//    // getters and setters
//}
// And write common code like below

//            // Build and send the FHIR request
//            Map<String, Object> map = new HashMap<>();
//            for(ApiTest apitest: apitests (order by rank by testId))
//
//            Update next API call based on previous calls.
//            apiTest.setRequestBody(apiTest.getRequestBody(), map);
//
//            Bundle responseBundle = client
//                    .transaction()
//                    .withBundle(apiTest.getRequestBody())
//                    .execute()
//                    .getResource(Bundle.class);
//
//            // Convert the response to a string for easy comparison
//            String actualResponse = fhirContext.newJsonParser().encodeResourceToString(responseBundle);
//            // Assert the response
//            assertEquals(apiTest.getExpectedResponse(), actualResponse);
//            map.put("nameToStoreResponseResource", getResourceBasedOnBundle(responseBundle)); in case wants to use this as new request data.

            return new ValidationResultInfo("testCRWF1Case1", ErrorLevel.OK, "Passed");
        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
