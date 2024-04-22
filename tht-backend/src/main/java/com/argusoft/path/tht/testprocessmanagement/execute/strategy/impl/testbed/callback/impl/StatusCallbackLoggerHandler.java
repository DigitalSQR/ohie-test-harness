package com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.impl;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.status.response.StatusResponse;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.StatusCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
public class StatusCallbackLoggerHandler implements StatusCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusCallbackLoggerHandler.class);

    private StatusResponse statusResponse;

    @Override
    public void onSuccessStatusReceived(StatusResponse statusResponse, String testSessionId, ContextInfo contextInfo) throws OperationFailedException {
        LOGGER.info("*".repeat(25));
        LOGGER.info("* "+statusResponse.getSessions().get(0).getResult().value() +" *");
        LOGGER.info("*".repeat(25));
        this.setStatusResponse(statusResponse);

    }

    @Override
    public void onFailureStatusReceived(StatusResponse statusResponse, String testSessionId, ContextInfo contextInfo) throws OperationFailedException {
        LOGGER.info("*".repeat(25));
        LOGGER.info("* "+statusResponse.getSessions().get(0).getResult().value() +" *");
        LOGGER.info("*".repeat(25));
        this.setStatusResponse(statusResponse);
    }

    @Override
    public void onOutOfDurationTime(String testSessionId, ContextInfo contextInfo) throws OperationFailedException {
        LOGGER.info("*".repeat(25));
        LOGGER.info("* out of duration *");
        LOGGER.info("*".repeat(25));
        throw new OperationFailedException("out of duration");
    }

    @Override
    public void onRestClientExceptionReceived(String testSessionId, RestClientException e, ContextInfo contextInfo) throws OperationFailedException {
        LOGGER.info("*".repeat(25));
        LOGGER.info("* REST CLIENT EXCEPTION *");
        LOGGER.info("*".repeat(25));
        LOGGER.error("rest client exception ",e);
        throw new OperationFailedException(e.getMessage(), e);
    }

    public StatusResponse getStatusResponse() {
        return statusResponse;
    }

    public void setStatusResponse(StatusResponse statusResponse) {
        this.statusResponse = statusResponse;
    }
}
