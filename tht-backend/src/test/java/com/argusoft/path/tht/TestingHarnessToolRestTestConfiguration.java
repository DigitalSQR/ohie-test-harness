package com.argusoft.path.tht;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;


public class TestingHarnessToolRestTestConfiguration extends TestingHarnessToolTestConfiguration {

    @Value("${app.authBasicToken}")
    String authBasicToken;

    @Autowired
    private RestTemplate restTemplate;

    protected Map tokenMap;

    private MockRestServiceServer mockServer;


    public void init() {
        super.init();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    public void login(
            String username,
            String password,
            WebTestClient webTestClient
    ) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", username);
        formData.add("password", password);
        formData.add("grant_type", "password");

        this.tokenMap = webTestClient
                .post()
                .uri(uriBuilder
                        -> uriBuilder
                        .path("/oauth/token")
                        .build())
                .bodyValue(formData)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + authBasicToken)
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();
    }

}
