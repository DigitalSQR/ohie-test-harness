package com.argusoft.path.tht.reportmanagement.rest;

import com.argusoft.path.tht.TestingHarnessToolRestTestConfiguration;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultAnswerInfo;
import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseOptionServiceMockImpl;
import com.argusoft.path.tht.reportmanagement.mock.TestcaseResultServiceMockImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class TestcaseResultRestControllerTest extends TestingHarnessToolRestTestConfiguration {

    ContextInfo contextInfo;
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    private TestcaseResultServiceMockImpl testcaseResultServiceMock;
    @Autowired
    private TestcaseOptionServiceMockImpl testcaseOptionServiceMock;


    @BeforeEach
    @Override
    public void init() {
        super.init();
        testcaseOptionServiceMock.init();
        testcaseResultServiceMock.init();
        contextInfo = Constant.SUPER_USER_CONTEXT;
        super.login("noreplytestharnesstool@gmail.com",
                "password",
                webTestClient);
    }

    @AfterEach
    void after() {
        testcaseResultServiceMock.clear();
        testcaseOptionServiceMock.clear();
    }

    @Test
    void testSearchTestcaseResults()
    {
        TestcaseResultCriteriaSearchFilter filter = new TestcaseResultCriteriaSearchFilter();
        filter.setName("Verify Create Patient");
        this.webTestClient.get().uri(uriBuilder -> uriBuilder.path("/testcase-result")
                        .queryParam("name", filter.getName())
                        .build()
                ).header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo("1");

    }

    @Test
    void testGetTestcaseResultById() {

//        Mock Data
        String testcaseResultId = "TestcaseResult.02";

        TestcaseResultInfo testcaseResultInfo =  this.webTestClient.get()
                .uri("/testcase-result/{testcaseResultId}", testcaseResultId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseResultInfo.class)
                .returnResult()
                .getResponseBody();
        assertEquals("TestcaseResult.02", testcaseResultInfo.getId());
        assertEquals("component.client.registry",testcaseResultInfo.getRefId());

    }

    @Test
    void testGetTestcaseResultStatus()
    {
//        Mock data
        String testcaseResultId = "TestcaseResult.02";


        TestcaseResultInfo testcaseResultInfo =  this.webTestClient.get()
                .uri("/testcase-result/status/{testcaseResultId}?manual=true&required=true&workflow=true", testcaseResultId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(TestcaseResultInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("TestcaseResult.02", testcaseResultInfo.getId());
        assertEquals("component.client.registry",testcaseResultInfo.getRefId());
    }


    @Test
    @Disabled
    void testSubmitTestcaseResult()
    {

        List<TestcaseResultAnswerInfo> testcaseResultAnswerInfos = new ArrayList<>();
        TestcaseResultAnswerInfo testcaseResultAnswerInfo = new TestcaseResultAnswerInfo();
        testcaseResultAnswerInfo.setTestcaseResultId("TestcaseResult.06");
        Set<String> selectedOptionIds = new HashSet<>();
        selectedOptionIds.add("testcase.cr.crf.9.1.option.1");
        testcaseResultAnswerInfo.setSelectedTestcaseOptionIds(selectedOptionIds);
        testcaseResultAnswerInfos.add(testcaseResultAnswerInfo);


        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("testcaseResultAnswerInfos", testcaseResultAnswerInfos);


        List<TestcaseResultInfo> testcaseOptionInfo = this.webTestClient.patch()
                .uri("/testcase-result/submit")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(requestBody)
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBodyList(TestcaseResultInfo.class).returnResult().getResponseBody();
    }
    @Test
    void testGetSubClassesNameForTestCase() {

        this.webTestClient.get()
                .uri("/testcase-result/sub-classes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class);

    }


    @Test
    void testGetStatusMapping() {

        // Mock data
        String sourceStatus = "testcase.result.status.pending";


        this.webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/testcase-result/status/mapping")
                        .queryParam("sourceStatus", sourceStatus)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class);
    }

}
