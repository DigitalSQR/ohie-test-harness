package com.argusoft.path.tht.testprocessmanagement.rest;


import com.argusoft.path.tht.TestingHarnessToolRestTestConfiguration;
import com.argusoft.path.tht.reportmanagement.mock.TestcaseResultServiceMockImpl;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseOptionServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestCriteriaSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.mock.TestRequestServiceMockImpl;
import com.argusoft.path.tht.testprocessmanagement.models.dto.GraphInfo;
import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo;
import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestUrlInfo;
import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestViewInfo;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.mapper.TestRequestMapper;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


public class TestRequestRestControllerTest extends TestingHarnessToolRestTestConfiguration{

    @Autowired
    private TestcaseResultServiceMockImpl testcaseResultServiceMockImpl;

    @Autowired
    private TestcaseOptionServiceMockImpl testcaseOptionServiceMockImpl;

    @Autowired
    private TestRequestService testRequestService;

    @Autowired
    TestRequestMapper testRequestMapper;

    @Autowired
    WebTestClient webTestClient;

    private ContextInfo contextInfo;

    @BeforeEach
    @Override
    public void init() {
        super.init();
        testcaseOptionServiceMockImpl.init();
        testcaseResultServiceMockImpl.init();
        contextInfo = Constant.SUPER_USER_CONTEXT;
        super.login("noreplytestharnesstool@gmail.com",
                "password",
                webTestClient);
    }

    @AfterEach
    void after() {
        testcaseResultServiceMockImpl.clear();
        testcaseOptionServiceMockImpl.clear();

    }

    @Test
    void createTestRequest() throws InvalidParameterException, DoesNotExistException {

        super.login("dummyuser1@testmail.com",
                "password",
                webTestClient);

        TestRequestInfo testRequestInfo = new TestRequestInfo();
        testRequestInfo.setId("TestRequest1");
        testRequestInfo.setName("Test1");
        testRequestInfo.setApproverId("SYSTEM_USER");
        testRequestInfo.setAssesseeId("user.01");
        testRequestInfo.setMessage("Testing");
        testRequestInfo.setDescription("Test1 to be done");

        TestRequestUrlInfo testRequestUrlInfo = new TestRequestUrlInfo();

        testRequestUrlInfo.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlInfo.setUsername("username");
        testRequestUrlInfo.setPassword("password");
        testRequestUrlInfo.setComponentId("component.02");
        testRequestUrlInfo.setWebsiteUIBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlInfo.setFhirVersion("R4");

        Set<TestRequestUrlInfo> testRequestUrlInfoSet = new HashSet<>();

        testRequestUrlInfoSet.add(testRequestUrlInfo);

        testRequestInfo.setTestRequestUrls(testRequestUrlInfoSet);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("testcaseRequestInfo", testRequestInfo);

        TestRequestInfo testRequestInfoResult = this.webTestClient
                .post()
                .uri("/test-request")
                .body(BodyInserters.fromValue(testRequestInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestRequestInfo.class)
                .returnResult()
                .getResponseBody();

    }

    @Test
    void updateTestRequest() throws InvalidParameterException, DoesNotExistException {

        TestRequestEntity testRequestEntity = testRequestService.getTestRequestById("TestRequest.10", contextInfo);

        TestRequestInfo testRequestInfo = testRequestMapper.modelToDto(testRequestEntity);

        testRequestInfo.setName("Changed Test Name");

        TestRequestInfo testRequestInfoResult = this.webTestClient
                .put()
                .uri("/test-request")
                .body(BodyInserters.fromValue(testRequestInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestRequestInfo.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void searchTestRequests() {

        TestRequestCriteriaSearchFilter testRequestCriteriaSearchFilter = new TestRequestCriteriaSearchFilter();
        testRequestCriteriaSearchFilter.setName("Test Request 1");

       this.webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/test-request")
                        .queryParam("assesseeId", "user.08")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo("2");

    }

    @Test
    void testGetTestRequestById(){
        TestRequestInfo testRequestInfo = this.webTestClient
                .get()
                .uri("/test-request/{testRequestId}", "TestRequest.01")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestRequestInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("TestRequest.01", testRequestInfo.getId());

    }

    @Test
    void testValidateTestRequest(){

        super.login("dummyuser1@testmail.com",
                "password",
                webTestClient);

        TestRequestInfo testRequestInfo = new TestRequestInfo();
        testRequestInfo.setId("TestRequest1");
        testRequestInfo.setName("Test1");
        testRequestInfo.setApproverId("SYSTEM_USER");
        testRequestInfo.setAssesseeId("user.01");
        testRequestInfo.setMessage("Testing");
        testRequestInfo.setDescription("Test1 to be done");
        testRequestInfo.setState("component.status.accepted");

        TestRequestUrlInfo testRequestUrlInfo = new TestRequestUrlInfo();

        testRequestUrlInfo.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlInfo.setUsername("username");
        testRequestUrlInfo.setPassword("password");
        testRequestUrlInfo.setComponentId("component.02");
        testRequestUrlInfo.setWebsiteUIBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlInfo.setFhirVersion("R4");

        Set<TestRequestUrlInfo> testRequestUrlInfoSet = new HashSet<>();

        testRequestUrlInfoSet.add(testRequestUrlInfo);

        testRequestInfo.setTestRequestUrls(testRequestUrlInfoSet);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("testcaseRequestInfo", testRequestInfo);

        List<ValidationResultInfo> validationResultInfos = this.webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/test-request/validate")
                        .queryParam("validationTypeKey", "create.validation")
                        .build())
                .body(BodyInserters.fromValue(testRequestInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBodyList(ValidationResultInfo.class)
                .returnResult()
                .getResponseBody();

        assert validationResultInfos != null;
        assertEquals(0, validationResultInfos.size());

    }

    @Test
    void testValidateTestRequestState(){

        Map<String, String> requestMap = new HashMap<>();

        requestMap.put("message","Accepted");

        List<ValidationResultInfo> validationResultInfos = this.webTestClient
                .patch()
                .uri("/test-request/validate/state/{testRequestId}/{changeState}", "TestRequest.06", TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED)
                .body(BodyInserters.fromValue(requestMap))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBodyList(ValidationResultInfo.class)
                .returnResult()
                .getResponseBody();


        assertEquals(0, validationResultInfos.size());
    }

    @Test
    void testUpdateTestRequestState(){
        Map<String, String> requestMap = new HashMap<>();

        requestMap.put("message","Rejected");

        List<ValidationResultInfo> validationResultInfos = this.webTestClient
                .patch()
                .uri("/test-request/state/{testRequestId}/{changeState}", "TestRequest.06", TestRequestServiceConstants.TEST_REQUEST_STATUS_REJECTED)
                .body(BodyInserters.fromValue(requestMap))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBodyList(ValidationResultInfo.class)
                .returnResult()
                .getResponseBody();


        assertEquals(1, validationResultInfos.size());

        assertEquals(ErrorLevel.OK, validationResultInfos.get(0).getLevel());
    }

    @Test
    void getStatusMapping(){
        List<String> result = this.webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/test-request/status/mapping")
                        .queryParam("sourceStatus", "test.request.status.pending")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBodyList(String.class)
                .returnResult()
                .getResponseBody();

        assertEquals(1, result.size());

    }

    @Test
    void testGetApplicationStats(){
        List<TestRequestViewInfo> result = this.webTestClient
                .get()
                .uri("/test-request/applications-stats")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBodyList(TestRequestViewInfo.class)
                .returnResult()
                .getResponseBody();

        assert result != null;
        assertEquals(7, result.size());

    }

    @Test
    void testGetDashboard(){
        GraphInfo result = this.webTestClient
                .get()
                .uri("/test-request/dashboard")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(GraphInfo.class)
                .returnResult()
                .getResponseBody();

        assert result != null;

    }

}
