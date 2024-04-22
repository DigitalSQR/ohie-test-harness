package com.argusoft.path.tht.testcasemanagement.testbed.services.impl;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.deploy.request.DeployRequest;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.deploy.response.DeployResponse;
import com.argusoft.path.tht.testcasemanagement.testbed.services.TestSuiteManagementRestService;
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
public class TestSuiteManagementRestServiceImpl implements TestSuiteManagementRestService {

    @Value("${testbed.testsuite-management.deploy-non-shared-url-endpoint}")
    private String deployNonSharedEndpoint;

    @Value("${testbed.url}")
    private String testBedUrl;


    @Value("${testbed.community-api}")
    private String apiKeyForHeader;

    public static final Logger LOGGER = LoggerFactory.getLogger(TestSuiteManagementRestServiceImpl.class);

    @Override
    public DeployResponse deployTestSuite(DeployRequest deployRequest, ContextInfo contextInfo) throws URISyntaxException, RestClientException {
        URI uri = TestBedGenericUtil.buildURI(testBedUrl, deployNonSharedEndpoint);
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add("ITB_API_KEY",apiKeyForHeader);
        HttpEntity<DeployRequest> request = new HttpEntity<>(deployRequest, headers);
        return restTemplate.postForObject(uri.toString(), request, DeployResponse.class);
    }
}
