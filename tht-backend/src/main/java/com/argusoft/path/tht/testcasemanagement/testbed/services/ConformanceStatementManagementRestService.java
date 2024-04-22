package com.argusoft.path.tht.testcasemanagement.testbed.services;

import com.argusoft.path.tht.testcasemanagement.testbed.dto.conformance.create.restresponse.ConformanceStatementCreateRecord;
import org.springframework.web.client.RestClientException;

import java.net.URISyntaxException;

public interface ConformanceStatementManagementRestService {

    ConformanceStatementCreateRecord createConformanceStatement(String systemAPI, String actorAPI) throws RestClientException, URISyntaxException;

}
