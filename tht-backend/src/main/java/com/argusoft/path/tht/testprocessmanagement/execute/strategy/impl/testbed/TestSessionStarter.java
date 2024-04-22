package com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed;

import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.start.request.AnyContent;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.start.request.Input;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.start.request.StartRequest;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.start.response.StartResponse;
import com.argusoft.path.tht.testcasemanagement.testbed.services.TestSessionManagementRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class TestSessionStarter {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestSessionStarter.class);

    @Value("${testbed.system-api}")
    private String testbedSystemApiKey;

    private TestSessionManagementRestService testSessionManagementRestService;

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public StartResponse process(String testsuiteId , String actorApiKey, ContextInfo contextInfo) throws URISyntaxException, RestClientException {
        StartRequest startRequest = buildStartRequest(testsuiteId, actorApiKey);
        return getStartResponse(contextInfo, startRequest);
    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public StartResponse processWithInputParams(String testsuiteId, String actorApiKey, Map<String,String> inputParameters, ContextInfo contextInfo) throws URISyntaxException, RestClientException {
        StartRequest startRequest = buildStartRequestWithParams(testsuiteId, actorApiKey, inputParameters);
        return getStartResponse(contextInfo, startRequest);
    }

    private StartRequest buildStartRequestWithParams(String testsuiteId, String actorApiKey, Map<String, String> inputParameters) {
        StartRequest startRequest = buildStartRequest(testsuiteId, actorApiKey);
        List<Input> inputList = new ArrayList<>();
        inputParameters.forEach((key, value) -> {
            Input input = new Input();
            AnyContent anyContent = new AnyContent();
            anyContent.setName(key);
            anyContent.setValue(value);
            input.setInput(anyContent);
            inputList.add(input);
        });
        startRequest.setInputMapping(inputList);
        return startRequest;
    }

    private StartResponse getStartResponse(ContextInfo contextInfo, StartRequest startRequest) throws URISyntaxException {
        return testSessionManagementRestService.startTestSession(startRequest, contextInfo);
    }

    private StartRequest buildStartRequest(String testsuiteId, String actorApiKey) {
        StartRequest startRequest = new StartRequest();
        startRequest.setActor(actorApiKey);
        startRequest.setSystem(testbedSystemApiKey);
        startRequest.setTestSuite(Collections.singletonList(testsuiteId));
        return startRequest;
    }

    @Autowired
    public void setTestSessionManagementRestService(TestSessionManagementRestService testSessionManagementRestService) {
        this.testSessionManagementRestService = testSessionManagementRestService;
    }
}
