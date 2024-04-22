package com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.status.request.StatusRequest;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.status.response.StatusResponse;
import com.argusoft.path.tht.testcasemanagement.testbed.services.TestSessionManagementRestService;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.StatusCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SessionChecker {

    public static final Logger LOGGER = LoggerFactory.getLogger(SessionChecker.class);

    private final TestSessionManagementRestService testSessionManagementRestService;
    private final StatusRequest statusRequest;
    private final ContextInfo contextInfo;

    private final String testSessionId;

    private static final int INTERVAL_MS = 2000; // Interval in milliseconds
    private static final long DURATION_MS = 600000; // Duration in milliseconds (e.g., 1 minute)

    public SessionChecker(String testSessionId, TestSessionManagementRestService testSessionManagementRestService,  ContextInfo contextInfo) {
        this.testSessionManagementRestService = testSessionManagementRestService;
        this.statusRequest = buildStatusRequest(testSessionId);
        this.contextInfo = contextInfo;
        this.testSessionId = testSessionId;
    }

    public void checkSessionStatus(StatusCallback statusCallback) throws OperationFailedException, URISyntaxException {
        long startTime = System.currentTimeMillis();
        long lastCheckedTime = startTime;

        while (true) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - startTime > DURATION_MS) {
                // Notify if out of duration
                statusCallback.onOutOfDurationTime(testSessionId, contextInfo);
                break; // Exit the loop if duration is exceeded
            }

            if (currentTime - lastCheckedTime >= INTERVAL_MS) {
                lastCheckedTime = currentTime;
                try {
                    // Call the method to check session status
                    StatusResponse statusResponse = testSessionManagementRestService.statusTestSession(statusRequest, contextInfo);

                    if (statusResponse.getSessions() == null || statusResponse.getSessions().isEmpty()) {
                        LOGGER.info("Session is not present. Waiting for next check for ...");
                    } else {
                        switch (statusResponse.getSessions().get(0).getResult().value()) {
                            case "UNDEFINED":
                                LOGGER.info("Result is UNDEFINED. Waiting for next check for ...");
                                continue;
                            case "SUCCESS":
                                statusCallback.onSuccessStatusReceived(statusResponse, testSessionId, contextInfo);
                                return; // Exit the loop if success is received
                            case "FAILURE":
                                statusCallback.onFailureStatusReceived(statusResponse, testSessionId, contextInfo);
                                return; // Exit the loop if failure is received
                            default:
                                LOGGER.info("Result is UNEXPECTED. Waiting for next check for ...");
                        }
                    }
                } catch (OperationFailedException e) {
                    LOGGER.error("OperationFailedException occurred while checking session status", e);
                } catch (RestClientException restClientException) {
                    LOGGER.error("RestClientException occurred while checking session status", restClientException);
                    statusCallback.onRestClientExceptionReceived(testSessionId, restClientException, contextInfo);
                }
            }
        }
    }

    private static StatusRequest buildStatusRequest(String testSessionId) {
        StatusRequest statusRequest = new StatusRequest();
        statusRequest.setSession(Collections.singletonList(testSessionId));
        statusRequest.setWithLogs(Boolean.TRUE);
        statusRequest.setWithReports(Boolean.TRUE);
        return statusRequest;
    }

}