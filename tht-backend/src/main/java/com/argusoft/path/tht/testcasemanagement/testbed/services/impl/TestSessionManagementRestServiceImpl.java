package com.argusoft.path.tht.testcasemanagement.testbed.services.impl;

import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.start.request.StartRequest;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.start.response.StartResponse;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.status.request.StatusRequest;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.status.response.StatusResponse;
import com.argusoft.path.tht.testcasemanagement.testbed.services.TestSessionManagementRestService;
import com.argusoft.path.tht.testcasemanagement.testbed.util.TestBedGenericUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class TestSessionManagementRestServiceImpl implements TestSessionManagementRestService {

    @Value("${testbed.url}")
    private String testBedUrl;

    @Value("${testbed.organization-api}")
    private String organizationApiKeyForHeader;

    @Value("${testbed.testsession-management.start-test-session-endpoint}")
    private String testSessionStartEndpoint;

    @Value("${testbed.testsession-management.session-test-session-endpoint}")
    private String testSessionStatusEndpoint;

    public static final Logger LOGGER = LoggerFactory.getLogger(TestSessionManagementRestServiceImpl.class);

    @Override
    public StartResponse startTestSession(StartRequest startRequest, ContextInfo contextInfo) throws URISyntaxException, RestClientException {

            URI uri = TestBedGenericUtil.buildURI(testBedUrl, testSessionStartEndpoint);
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
            headers.add("ITB_API_KEY", organizationApiKeyForHeader);

            HttpEntity<StartRequest> request = new HttpEntity<>(startRequest, headers);
            return restTemplate.postForObject(uri.toString(), request, StartResponse.class);

    }

    @Override
    public StatusResponse statusTestSession(StatusRequest statusRequest, ContextInfo contextInfo) throws URISyntaxException, RestClientException {
            URI uri = TestBedGenericUtil.buildURI(testBedUrl, testSessionStatusEndpoint);
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
            headers.add("ITB_API_KEY", organizationApiKeyForHeader);

            HttpEntity<StatusRequest> request = new HttpEntity<>(statusRequest, headers);
            return restTemplate.postForObject(uri.toString(), request, StatusResponse.class);
    }
}
