package com.argusoft.path.tht.testcasemanagement.rest;

import com.argusoft.path.tht.TestingHarnessToolRestTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseOptionServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseOptionServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseOptionInfo;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.diff.JsonDiff;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class TestcaseOptionRestControllerTest extends TestingHarnessToolRestTestConfiguration {

    @Autowired
    TestcaseServiceMockImpl testcaseServiceMock;

    @Autowired
    TestcaseOptionServiceMockImpl testcaseOptionServiceMock;

    @Autowired
    TestcaseService testcaseService;

    @Autowired
    TestcaseOptionService testcaseOptionService;

    @Autowired
    WebTestClient webTestClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    @Override
    public void init() {
        super.init();
        testcaseOptionServiceMock.init();
        super.login("noreplytestharnesstool@gmail.com",
                "password",
                webTestClient);
    }

    @AfterEach
    void after() {
        testcaseOptionServiceMock.clear();
    }
    @Test
    void testCreateTestcaseOption(){

        TestcaseOptionInfo testcaseOptionInfo = new TestcaseOptionInfo();
        testcaseOptionInfo.setId("testcaseOption.03");
        testcaseOptionInfo.setName("Testcase Option 3");
        testcaseOptionInfo.setDescription("Just a testcase Option");
        testcaseOptionInfo.setState("testcase.option.status.active");
        testcaseOptionInfo.setRank(2);
        testcaseOptionInfo.setTestcaseId("testcase.03");
        testcaseOptionInfo.setSuccess(false);

        TestcaseOptionInfo createTestcaseOption = this.webTestClient
                .post()
                .uri("/testcase-option")
                .body(BodyInserters.fromValue(testcaseOptionInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseOptionInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("Testcase Option 3", createTestcaseOption.getName());
        assertEquals("testcase.03", createTestcaseOption.getTestcaseId());
    }


    @Test
    void testUpdateTestcaseOption() {
        TestcaseOptionInfo testcaseOptionInfo = this.webTestClient
                .get()
                .uri("/testcase-option/{testcaseOptionId}", "testcaseOption.02")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseOptionInfo.class)
                .returnResult()
                .getResponseBody();

//        Before
        assertEquals("No, I couldn't find a way to choose specifics fields to be merged.", testcaseOptionInfo.getName());
        assertEquals("testcase.03", testcaseOptionInfo.getTestcaseId());

        testcaseOptionInfo.setName("TestcaseOption one");
        testcaseOptionInfo.setTestcaseId("testcase.222");

        TestcaseOptionInfo updatedTestcaseOptionInfo = this.webTestClient
                .put()
                .uri("/testcase-option")
                .body(BodyInserters.fromValue(testcaseOptionInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseOptionInfo.class)
                .returnResult()
                .getResponseBody();

//        After
        assertEquals("TestcaseOption one", updatedTestcaseOptionInfo.getName());
        assertEquals("testcase.222", updatedTestcaseOptionInfo.getTestcaseId());

    }

//
    @Test
    void testGetTestcaseOptionById() throws Exception {

        TestcaseOptionInfo testcaseOptionInfo = this.webTestClient
                .get()
                .uri("/testcase-option/{testcaseOptionId}", "testcaseOption.02")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseOptionInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("testcaseOption.02", testcaseOptionInfo.getId());
        assertEquals("No, I couldn't find a way to choose specifics fields to be merged.", testcaseOptionInfo.getName());
    }


    @Test
    void testSearchTestcaseOption() throws Exception {
        this.webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/testcase-option")
                        .queryParam("name", "Yes")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo("2");
    }


    @Test
    void testValidateTestcaseOption() {
        TestcaseOptionInfo testcaseOptionInfo = new TestcaseOptionInfo();
        testcaseOptionInfo.setId("testcaseOption.01");
        testcaseOptionInfo.setName("Testcase Option 3");
        testcaseOptionInfo.setDescription("Just a testcase Option");
        testcaseOptionInfo.setState("testcase.option.status.active");
        testcaseOptionInfo.setRank(2);
        testcaseOptionInfo.setTestcaseId("testcase.03");
        testcaseOptionInfo.setSuccess(false);

        List<ValidationResultInfo> errors = this.webTestClient
                .post()
                .uri(uriBuilder
                        -> uriBuilder
                        .path("/testcase-option/validate")
                        .queryParam("validationTypeKey", Constant.CREATE_VALIDATION)
                        .build())
                .body(BodyInserters.fromValue(testcaseOptionInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBodyList(ValidationResultInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals(errors.size(), 1);
        assertEquals("The id supplied for the create already exists", errors.get(0).getMessage());
    }

    @Test
    void updateTestcaseOptionState() throws Exception {
        TestcaseOptionInfo testcaseOptionInfo = this.webTestClient
                .get()
                .uri("/testcase-option/{testcaseOptionId}", "testcaseOption.01")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseOptionInfo.class)
                .returnResult()
                .getResponseBody();

//        Before
        assertEquals(TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_ACTIVE, testcaseOptionInfo.getState());

        TestcaseOptionInfo updatedTestcaseOption = this.webTestClient
                .patch()
                .uri("/testcase-option/state/{testcaseOptionId}/{changeState}", "testcaseOption.01",TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_INACTIVE)
                .body(BodyInserters.fromValue(testcaseOptionInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseOptionInfo.class)
                .returnResult()
                .getResponseBody();

//       After
        assertEquals(TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_INACTIVE, updatedTestcaseOption.getState());

    }

    @Test
    void testPatchTestcaseOption() throws IOException {

        // Retrieve the original TestcaseOptionInfo object
        TestcaseOptionInfo originalTestcaseOptionInfo = this.webTestClient
                .get()
                .uri("/testcase-option/{testcaseOptionId}", "testcaseOption.02")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectBody(TestcaseOptionInfo.class)
                .returnResult()
                .getResponseBody();

        // Before modification, assert the initial state
        assertEquals("No, I couldn't find a way to choose specifics fields to be merged.", originalTestcaseOptionInfo.getName());


        // Creating JSON Patch
        JsonNode originalNode = objectMapper.valueToTree(originalTestcaseOptionInfo);
        originalTestcaseOptionInfo.setName("TestcaseOption one");
        JsonNode updatedNode = objectMapper.valueToTree(originalTestcaseOptionInfo);
        JsonPatch jsonPatch = JsonDiff.asJsonPatch(originalNode, updatedNode);

        // Apply the JSON Patch to update the TestcaseOptionInfo object
        TestcaseOptionInfo updatedTestcaseOptionInfo = this.webTestClient
                .patch()
                .uri("/testcase-option/{testcaseOptionId}", "testcaseOption.02")
                .body(BodyInserters.fromValue(jsonPatch))
                .header(CONTENT_TYPE, "application/json-patch+json")  // Set the correct Content-Type
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseOptionInfo.class)
                .returnResult()
                .getResponseBody();

        // After modification, assert the updated state
        assertEquals("TestcaseOption one", updatedTestcaseOptionInfo.getName());


        //Test case 2 If exception occurred
        ObjectMapper objectMapper = new ObjectMapper();

        // Create JsonNode representing the patch
        JsonNode patchNode = objectMapper.createArrayNode()
                .add(objectMapper.createObjectNode()
                        .put("op", "replace")
                        .put("path", "/wrong-name")
                        .put("value", "TestcaseOption one"));

        // Convert JsonNode to JsonPatch
        jsonPatch = JsonPatch.fromJson(patchNode);

        JsonPatch finalJsonPatch = jsonPatch;
        Assertions.assertThrows(AssertionError.class, () -> {
            this.webTestClient
                    .patch()
                    .uri("/testcase-option/{testcaseOptionId}", "testcaseOption.02")
                    .body(BodyInserters.fromValue(finalJsonPatch))
                    .header(CONTENT_TYPE, "application/json-patch+json")  // Set the correct Content-Type
                    .header(ACCEPT, APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                    .exchange()
                    .expectStatus()
                    .isEqualTo(OK)
                    .expectBody(TestcaseOptionInfo.class)
                    .returnResult()
                    .getResponseBody();
        });
    }

    @Test
    void testGetTestcaseOptionMapping() {

        List strings = this.webTestClient
                .get()
                .uri("/testcase-option/status/mapping?sourceStatus=testcase.option.status.active")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(List.class)
                .returnResult()
                .getResponseBody();

        assertEquals("testcase.option.status.inactive", strings.get(0));
    }

}
