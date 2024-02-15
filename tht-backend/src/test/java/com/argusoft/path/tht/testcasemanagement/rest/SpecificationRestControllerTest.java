package com.argusoft.path.tht.testcasemanagement.rest;

import com.argusoft.path.tht.TestingHarnessToolRestTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.mock.ComponentServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.mock.SpecificationServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class SpecificationRestControllerTest extends TestingHarnessToolRestTestConfiguration {

    @Autowired
    ComponentServiceMockImpl componentServiceMockImpl;

    @Autowired
    SpecificationServiceMockImpl specificationServiceMockImpl;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ComponentService componentService;

    @Autowired
    SpecificationService specificationService;


    @BeforeEach
    @Override
    public void init() {
        super.init();
        specificationServiceMockImpl.init();

        super.login("ivasiwala@argusoft.com",
                "password",
                webTestClient);
    }


    @AfterEach
    void after() {
        specificationServiceMockImpl.clear();
    }

    /*@Test
    void testCreateSpecification(){
        SpecificationInfo specificationInfo = new SpecificationInfo();
        specificationInfo.setId("specification.601");
        specificationInfo.setName("specification 601");
        specificationInfo.setDescription("specification 601");

        specificationInfo.setComponentId("component.02");
        Set<String> testcaseIds = new HashSet<>();
        specificationInfo.setTestcaseIds(testcaseIds);

        SpecificationInfo createSpecification = this.webTestClient
                .post()
                .uri("/specification")
                .body(BodyInserters.fromValue(specificationInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(SpecificationInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("specification 601", createSpecification.getName());
        assertEquals("component.02", createSpecification.getComponentId());
    }*/


    @Test
    void testValidateSpecification() {
        SpecificationInfo specificationInfo = new SpecificationInfo();
        specificationInfo.setId("specification.01");
        specificationInfo.setName("new specification 1");
        specificationInfo.setState("specification.status.active");
        specificationInfo.setDescription("specification 1");

        Set<String> testCaseIds = new HashSet<>();
        specificationInfo.setTestcaseIds(testCaseIds);
        specificationInfo.setComponentId("component.02");

        List<ValidationResultInfo> errors = this.webTestClient
                .post()
                .uri(uriBuilder
                        -> uriBuilder
                        .path("/specification/validate")
                        .queryParam("validationTypeKey", Constant.CREATE_VALIDATION)
                        .build())
                .body(BodyInserters.fromValue(specificationInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBodyList(ValidationResultInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals(errors.size(), 1);
        assertEquals("The id supplied to the create already exists", errors.get(0).getMessage());
    }

    @Test
    void testUpdateSpecification() {
        SpecificationInfo specificationInfo = this.webTestClient
                .get()
                .uri("/specification/{specificationId}", "specification.01")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(SpecificationInfo.class)
                .returnResult()
                .getResponseBody();

//        Before
        assertEquals("Specification 1", specificationInfo.getName());
        assertEquals("component.02", specificationInfo.getComponentId());

        specificationInfo.setName("Specification 11");
        specificationInfo.setComponentId("component.04");

        SpecificationInfo updatedSpecification = this.webTestClient
                .put()
                .uri("/specification")
                .body(BodyInserters.fromValue(specificationInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(SpecificationInfo.class)
                .returnResult()
                .getResponseBody();

//        After
        assertEquals("Specification 11", updatedSpecification.getName());
        assertEquals("component.04", updatedSpecification.getComponentId());

    }

    @Test
    void updateSpecificationState() throws Exception {
        SpecificationInfo specificationInfo = this.webTestClient
                .get()
                .uri("/specification/{specificationId}", "specification.01")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(SpecificationInfo.class)
                .returnResult()
                .getResponseBody();

//        Before
        assertEquals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE, specificationInfo.getState());

        SpecificationInfo updatedSpecification = this.webTestClient
                .patch()
                .uri("/specification/state/{specificationId}/{changeState}", "specification.01", SpecificationServiceConstants.SPECIFICATION_STATUS_INACTIVE)
                .body(BodyInserters.fromValue(specificationInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(SpecificationInfo.class)
                .returnResult()
                .getResponseBody();

//       After
        assertEquals(SpecificationServiceConstants.SPECIFICATION_STATUS_INACTIVE, updatedSpecification.getState());

    }



    @Test
    void testGetSpecificationById() throws Exception {

        SpecificationInfo specificationInfo = this.webTestClient
                .get()
                .uri("/specification/{specificationId}", "specification.01")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(SpecificationInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("specification.01", specificationInfo.getId());
    }


    @Test
    void testSearchSpecification() throws Exception {
        this.webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/specification")
                        .queryParam("name", "Specification")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo("4");
    }
}