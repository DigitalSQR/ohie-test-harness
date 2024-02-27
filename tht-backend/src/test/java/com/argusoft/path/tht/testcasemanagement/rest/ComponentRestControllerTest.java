package com.argusoft.path.tht.testcasemanagement.rest;

import com.argusoft.path.tht.TestingHarnessToolRestTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.mock.ComponentServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
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
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class ComponentRestControllerTest extends TestingHarnessToolRestTestConfiguration {

    @Autowired
    ComponentServiceMockImpl componentServiceMockImpl;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ComponentService componentService;

    @BeforeEach
    @Override
    public void init() {
        super.init();
        componentServiceMockImpl.init();

        super.login("ivasiwala@argusoft.com",
                "password",
                webTestClient);
    }


    @AfterEach
    void after() {
        componentServiceMockImpl.clear();

    }


    /*@Test
    void testCreateComponent() throws Exception {
        ComponentInfo componentInfo = new ComponentInfo();
        componentInfo.setId("component.601");
        componentInfo.setName("component 601");
        componentInfo.setDescription("component 601");
        componentInfo.setState("component.status.active");
        componentInfo.setRank(1);


        Set<String> spIds = new HashSet<>();
        componentInfo.setSpecificationIds(spIds);

        ComponentInfo createComponent = this.webTestClient
                .post()
                .uri("/component")
                .body(BodyInserters.fromValue(componentInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(ComponentInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("component 601", createComponent.getName());
//        assertEquals("component.status.active", createComponent.getState());

        //we have to check for the inactive as component default value is inactive
        assertEquals("component.status.draft", createComponent.getState());
    }*/


    @Test
    void testValidateComponent() {
        ComponentInfo componentInfo = new ComponentInfo();
        componentInfo.setId("component.04");
        componentInfo.setName("new component 4");
        componentInfo.setState("component.status.active");
        componentInfo.setDescription("component 4");
        componentInfo.setRank(1);

        Set<String> spIds = new HashSet<>();
        componentInfo.setSpecificationIds(spIds);

        List<ValidationResultInfo> errors = this.webTestClient
                .post()
                .uri(uriBuilder
                        -> uriBuilder
                        .path("/component/validate")
                        .queryParam("validationTypeKey", Constant.CREATE_VALIDATION)
                        .build())
                .body(BodyInserters.fromValue(componentInfo))
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
    void testUpdateComponent() {
        ComponentInfo componentInfo = this.webTestClient
                .get()
                .uri("/component/{componentId}", "component.02")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(ComponentInfo.class)
                .returnResult()
                .getResponseBody();

        // Before
        assertEquals("Component 2", componentInfo.getName());

        componentInfo.setName("Component 21");

        ComponentInfo updatedComponent = this.webTestClient
                .put()
                .uri("/component")
                .body(BodyInserters.fromValue(componentInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(ComponentInfo.class)
                .returnResult()
                .getResponseBody();

         // After
        assertEquals("Component 21", updatedComponent.getName());

    }

    @Test
    void updateComponentState() throws Exception {
        ComponentInfo componentInfo = this.webTestClient
                .get()
                .uri("/component/{componentId}", "component.02")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(ComponentInfo.class)
                .returnResult()
                .getResponseBody();

        // Before
        assertEquals(ComponentServiceConstants.COMPONENT_STATUS_ACTIVE, componentInfo.getState());

        ComponentInfo updatedComponent = this.webTestClient
                .patch()
                .uri("/component/state/{componentId}/{changeState}", "component.02", ComponentServiceConstants.COMPONENT_STATUS_INACTIVE)
                .body(BodyInserters.fromValue(componentInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(ComponentInfo.class)
                .returnResult()
                .getResponseBody();

        // After
        assertEquals(ComponentServiceConstants.COMPONENT_STATUS_INACTIVE, updatedComponent.getState());

    }



    @Test
    void testGetComponentById() throws Exception {

        ComponentInfo componentInfo = this.webTestClient
                .get()
                .uri("/component/{componentId}", "component.02")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(ComponentInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("component.02", componentInfo.getId());
    }


    @Test
    void testSearchComponent() throws Exception {
        this.webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/component")
                        .queryParam("name", "Component")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo("3");
    }

    @Test
    void testGetComponentMapping(){

        List strings = this.webTestClient
                .get()
                .uri("/component/status/mapping?sourceStatus=component.status.active")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(List.class)
                .returnResult()
                .getResponseBody();

        assertEquals("component.status.inactive", strings.get(0));

    }
}