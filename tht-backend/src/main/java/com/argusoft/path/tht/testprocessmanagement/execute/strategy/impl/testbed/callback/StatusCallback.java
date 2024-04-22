package com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.status.response.StatusResponse;
import org.springframework.web.client.RestClientException;

public interface StatusCallback {
    void onSuccessStatusReceived(StatusResponse statusResponse, String testSessionId, ContextInfo contextInfo) throws OperationFailedException;

    void onFailureStatusReceived(StatusResponse statusResponse, String testSessionId, ContextInfo contextInfo) throws OperationFailedException;

    void onOutOfDurationTime(String testSessionId, ContextInfo contextInfo) throws OperationFailedException;

    void onRestClientExceptionReceived(String testSessionId, RestClientException e, ContextInfo contextInfo) throws OperationFailedException;

}
