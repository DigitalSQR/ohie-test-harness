package com.argusoft.path.tht.testcasemanagement.testbed.services;

import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.start.request.StartRequest;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.start.response.StartResponse;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.status.request.StatusRequest;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.status.response.StatusResponse;
import org.springframework.web.client.RestClientException;

import java.net.URISyntaxException;

public interface TestSessionManagementRestService {

    StartResponse startTestSession(StartRequest startRequest, ContextInfo contextInfo) throws URISyntaxException, RestClientException;

    StatusResponse statusTestSession(StatusRequest statusRequest, ContextInfo contextInfo) throws URISyntaxException, RestClientException;

}
