package com.argusoft.path.tht.testcasemanagement.rest;

import com.argusoft.path.tht.TestingHarnessToolRestTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.diff.JsonDiff;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

public class TestcaseRestControllerTest extends TestingHarnessToolRestTestConfiguration {


    @Autowired
    TestcaseServiceMockImpl testcaseServiceMock;

    @Autowired
    WebTestClient webTestClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    @Override
    public void init() {
        super.init();
        testcaseServiceMock.init();

        super.login("noreplytestharnesstool@gmail.com",
                "password",
                webTestClient);
    }


    @AfterEach
    void after() {
        testcaseServiceMock.clear();
    }

    @Test
    @Disabled
    void testCreateTestcase(){
        TestcaseInfo testcaseInfo = new TestcaseInfo();
        testcaseInfo.setId("testcase.04");
        testcaseInfo.setName("Verify inbound/outbound transaction");
        testcaseInfo.setDescription("Testcase client repository functional 2");
        testcaseInfo.setState("testcase.status.active");
        testcaseInfo.setRank(1);
        testcaseInfo.setManual(true);


        testcaseInfo.setSpecificationId("specification.06");

        TestcaseInfo createTestcase = this.webTestClient
                .post()
                .uri("/testcase")
                .body(BodyInserters.fromValue(testcaseInfo))
                .header(CONTENT_TYPE, MULTIPART_FORM_DATA_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("Verify inbound/outbound transaction", createTestcase.getName());
        assertEquals("specification.06", createTestcase.getSpecificationId());
    }


    @Test
    void testUpdateTestcase() throws IOException {
        TestcaseInfo testcaseInfo = this.webTestClient
                .get()
                .uri("/testcase/{testcaseId}", "testcase.03")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseInfo.class)
                .returnResult()
                .getResponseBody();

//        Before
        assertEquals("Verify code membership", testcaseInfo.getName());
        assertEquals("specification.01", testcaseInfo.getSpecificationId());

        testcaseInfo.setName("Testcase 1");
        testcaseInfo.setSpecificationId("specification.07");

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("name","Testcase 1");
        parts.add("specificationId", "specification.07");
        parts.add("manual", testcaseInfo.getManual());
        parts.add("rank",testcaseInfo.getRank());
        parts.add("description", testcaseInfo.getDescription());
        parts.add("state", testcaseInfo.getState());


        TestcaseInfo updatedTestcase = this.webTestClient
                .put()
                .uri("/testcase")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .body(BodyInserters.fromMultipartData(parts))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TestcaseInfo.class)
                .returnResult()
                .getResponseBody();

//        After
        assertEquals("Testcase 1", updatedTestcase.getName());
        assertEquals("specification.07", updatedTestcase.getSpecificationId());

    }


    @Test
    void testGetTestcaseById() throws Exception {

        TestcaseInfo testcaseInfo = this.webTestClient
                .get()
                .uri("/testcase/{testcaseId}", "testcase.03")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("testcase.03", testcaseInfo.getId());
        assertEquals("Verify code membership", testcaseInfo.getName());
    }


    @Test
    void testSearchTestcase() throws Exception {
        this.webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/testcase")
                        .queryParam("name", "Verify")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo("4");
    }


    @Test
    void testValidateTestcase() {
        TestcaseInfo testcaseInfo = new TestcaseInfo();
        testcaseInfo.setId("testcase.02");
        testcaseInfo.setName("Verify inbound/outbound transaction");
        testcaseInfo.setDescription("Testcase client repository functional 2");
        testcaseInfo.setState("testcase.status.active");
        testcaseInfo.setRank(1);
        testcaseInfo.setManual(true);

        testcaseInfo.setSpecificationId("specification.06");

        List<ValidationResultInfo> errors = this.webTestClient
                .post()
                .uri(uriBuilder
                        -> uriBuilder
                        .path("/testcase/validate")
                        .queryParam("validationTypeKey", Constant.CREATE_VALIDATION)
                        .build())
                .body(BodyInserters.fromValue(testcaseInfo))
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
    void updateTestcaseState() throws Exception {
        TestcaseInfo testcaseInfo = this.webTestClient
                .get()
                .uri("/testcase/{testcaseId}", "testcase.03")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseInfo.class)
                .returnResult()
                .getResponseBody();

//        Before
        assertEquals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE, testcaseInfo.getState());

        TestcaseInfo updatedTestcase = this.webTestClient
                .patch()
                .uri("/testcase/state/{testcaseId}/{changeState}", "testcase.03", TestcaseServiceConstants.TESTCASE_STATUS_INACTIVE)
                .body(BodyInserters.fromValue(testcaseInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseInfo.class)
                .returnResult()
                .getResponseBody();

//       After
        assertEquals(TestcaseServiceConstants.TESTCASE_STATUS_INACTIVE, updatedTestcase.getState());

    }

    @Test
    void updateTestcaseRank() throws Exception {
        TestcaseInfo testcaseInfo = this.webTestClient
                .get()
                .uri("/testcase/{testcaseId}", "testcase.03")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseInfo.class)
                .returnResult()
                .getResponseBody();

        // Before
        assertEquals(3, testcaseInfo.getRank());

        TestcaseInfo updatedtestcase = this.webTestClient
                .patch()
                .uri("/testcase/rank/{testcaseId}/{rank}", "testcase.03", 1)
                .body(BodyInserters.fromValue(testcaseInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseInfo.class)
                .returnResult()
                .getResponseBody();

        // After
        assertEquals(1, updatedtestcase.getRank());
    }

    @Test
    void testGetTestcaseMapping() {

        List strings = this.webTestClient
                .get()
                .uri("/testcase/status/mapping?sourceStatus=testcase.status.active")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(List.class)
                .returnResult()
                .getResponseBody();

        assertEquals("testcase.status.inactive", strings.get(0));
    }

    @Test
    void testPatchTestcase() throws IOException {

        // Retrieve the original TestcaseOptionInfo object
        TestcaseInfo originalTestcaseInfo = this.webTestClient
                .get()
                .uri("/testcase/{testcaseId}", "testcase.03")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectBody(TestcaseInfo.class)
                .returnResult()
                .getResponseBody();

        // Before modification, assert the initial state
        assertEquals("Verify code membership", originalTestcaseInfo.getName());


        // Creating JSON Patch
        JsonNode originalNode = objectMapper.valueToTree(originalTestcaseInfo);
        originalTestcaseInfo.setName("Testcase 3");
        JsonNode updatedNode = objectMapper.valueToTree(originalTestcaseInfo);
        JsonPatch jsonPatch = JsonDiff.asJsonPatch(originalNode, updatedNode);

        // Apply the JSON Patch to update the TestcaseOptionInfo object
        TestcaseInfo updatedTestcaseInfo = this.webTestClient
                .patch()
                .uri("/testcase/{testcaseId}", "testcase.03")
                .body(BodyInserters.fromValue(jsonPatch))
                .header(CONTENT_TYPE, "application/json-patch+json")  // Set the correct Content-Type
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseInfo.class)
                .returnResult()
                .getResponseBody();

        // After modification, assert the updated state
        assertEquals("Testcase 3", updatedTestcaseInfo.getName());


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
                    .uri("/testcase/{testcaseId}", "testcase.03")
                    .body(BodyInserters.fromValue(finalJsonPatch))
                    .header(CONTENT_TYPE, "application/json-patch+json")  // Set the correct Content-Type
                    .header(ACCEPT, APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                    .exchange()
                    .expectStatus()
                    .isEqualTo(OK)
                    .expectBody(TestcaseInfo.class)
                    .returnResult()
                    .getResponseBody();
        });
    }
}
