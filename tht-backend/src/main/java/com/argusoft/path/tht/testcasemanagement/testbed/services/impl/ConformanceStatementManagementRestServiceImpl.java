package com.argusoft.path.tht.testcasemanagement.testbed.services.impl;

import com.argusoft.path.tht.testcasemanagement.testbed.dto.conformance.create.response.ConformanceResponse;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.conformance.create.restresponse.ConformanceStatementCreateRecord;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.start.request.StartRequest;
import com.argusoft.path.tht.testcasemanagement.testbed.services.ConformanceStatementManagementRestService;
import com.argusoft.path.tht.testcasemanagement.testbed.util.TestBedGenericUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class ConformanceStatementManagementRestServiceImpl implements ConformanceStatementManagementRestService {

    @Value("${testbed.url}")
    private String testBedUrl;


    @Value("${testbed.organization-api}")
    private String organizationApiKeyForHeader;

    @Value("${testbed.conformance-management.create-conformance-statement.endpoint}")
    private String conformanceStartEndpoint;


    @Override
    public ConformanceStatementCreateRecord createConformanceStatement(String systemAPI, String actorAPI) throws RestClientException, URISyntaxException {

        URI uri = TestBedGenericUtil.buildURI(testBedUrl, conformanceStartEndpoint);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            protected boolean hasError(HttpStatus statusCode) {
                return false;
            }
        });
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add("ITB_API_KEY", organizationApiKeyForHeader);

        String createURI = uri.toString();
        createURI = createURI.replace("SYSTEMAPIKEY",systemAPI);
        createURI = createURI.replace("ACTORAPIKEY",actorAPI);

        HttpEntity<StartRequest> request = new HttpEntity<>(headers);
        ResponseEntity<ConformanceResponse> exchange = restTemplate.exchange(createURI, HttpMethod.PUT, request, ConformanceResponse.class);
        if(exchange.getStatusCode().is2xxSuccessful()){
            return new ConformanceStatementCreateRecord(true, null);
        }
        else {
            return new ConformanceStatementCreateRecord(false, exchange.getBody());
        }
    }
}
