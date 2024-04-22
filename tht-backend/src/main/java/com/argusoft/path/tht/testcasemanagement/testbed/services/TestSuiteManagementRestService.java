package com.argusoft.path.tht.testcasemanagement.testbed.services;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.deploy.request.DeployRequest;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.deploy.response.DeployResponse;
import org.springframework.web.client.RestClientException;

import javax.print.attribute.URISyntax;
import java.net.URISyntaxException;

public interface TestSuiteManagementRestService {

    DeployResponse deployTestSuite(DeployRequest deployRequest, ContextInfo contextInfo) throws URISyntaxException, RestClientException;


}