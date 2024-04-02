package com.argusoft.path.tht.notification.rest;

import com.argusoft.path.tht.TestingHarnessToolRestTestConfiguration;
import com.argusoft.path.tht.notification.mock.NotificationServiceMockImpl;
import com.argusoft.path.tht.notificationmanagement.constant.NotificationServiceConstants;
import com.argusoft.path.tht.notificationmanagement.models.dto.NotificationInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class NotificationRestControllerTest extends TestingHarnessToolRestTestConfiguration {

    @Autowired
    NotificationServiceMockImpl notificationServiceMock;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    @Override
    public void init() {
        super.init();
        notificationServiceMock.init();

        super.login("dummyuser1@testmail.com",
                "password",
                webTestClient);
    }


    @AfterEach
    void after() {
        notificationServiceMock.clear();

    }

    @Test
    void testUpdateNotificationState() {
        NotificationInfo notificationInfo = this.webTestClient
                .get()
                .uri("/notification/{notificationId}", "notification.02")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(NotificationInfo.class)
                .returnResult()
                .getResponseBody();

        // Before
        assertEquals(NotificationServiceConstants.NOTIFICATION_STATUS_UNREAD, notificationInfo.getState());

        NotificationInfo updatedNotification = this.webTestClient
                .patch()
                .uri("/notification/state/{notificationId}/{changeState}", "notification.02", NotificationServiceConstants.NOTIFICATION_STATUS_ARCHIVED)
                .body(BodyInserters.fromValue(notificationInfo))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(NotificationInfo.class)
                .returnResult()
                .getResponseBody();

        // After
        assertEquals(NotificationServiceConstants.NOTIFICATION_STATUS_ARCHIVED, updatedNotification.getState());

    }


    @Test
    void testGetNotificationById() {

        NotificationInfo notificationInfo = this.webTestClient
                .get()
                .uri("/notification/{notificationId}", "notification.02")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(NotificationInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("notification.02", notificationInfo.getId());
    }

    @Test
    void testSearchNotification() {
        this.webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/notification")
                        .queryParam("state", "notification.status.archived")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo("2");
    }
}