package com.argusoft.path.tht.systemconfiguration.metricsmanagement.service.impl.rest;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.metricsmanagement.util.MetricsUtil;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * This Controller contains APIs related to matrics.
 *
 * @author Dhruv
 */
@RestController
@RequestMapping("/metrics")
@Api(value = "REST Endpoints for Metrics", tags = {"Metrics"})
public class MetricsServiceRestController {

    public static final Logger LOGGER = LoggerFactory.getLogger(MetricsServiceRestController.class);

    private static final Map<String, ByteArrayOutputStream> map
            = new HashMap<>();
    private static final String EQUALS = "===========================================================";
    @Autowired
    MetricsUtil metricsUtil;

    @Scheduled(cron = "0 0 * * * *")
    private void resetMetricsOutputStream() {
        for (String registryName : map.keySet()) {
            removeOldData(registryName);
        }
    }

    private void removeOldData(String registryName) {
        String value = map.get(registryName).toString();
        if (value.contains(EQUALS)) {
            String[] values = value.split(EQUALS);
            map.get(registryName).reset();
            map.get(registryName).writeBytes((values[values.length - 1]).getBytes());
        }

    }

    @PostConstruct
    void init() {
        try {
            metricsUtil.initMetricsReports(map);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("caught UnsupportedEncodingException in MetricsServiceRestController ", ex);
            ex.printStackTrace();
        }
    }

    @ApiOperation(value = "View metrics", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200,
                    message = "View metrics"),
            @ApiResponse(code = 401,
                    message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403,
                    message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping("/{registryName}")
    public ResponseEntity<Object> getmetrics(
            @PathVariable("registryName") String registryName,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo
    ) throws DoesNotExistException {
        if (map.get(registryName) == null) {
            LOGGER.error("caught DoesNotExistException in MetricsServiceRestController ");
            throw new DoesNotExistException("registry with name: "
                    + registryName
                    + " Does not exists.");
        }
        removeOldData(registryName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(map.get(registryName).toString().replace("\n", "<BR>"));
    }

    @ApiOperation(value = "View all registry", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200,
                    message = "View metrics"),
            @ApiResponse(code = 401,
                    message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403,
                    message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping("")
    public ResponseEntity<Object> getRegistryNames(
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo
    ) {
        StringBuilder name = new StringBuilder();
        for (String n : map.keySet()) {
            name
                    .append("<a href=\"./metrics/")
                    .append(n)
                    .append("\">")
                    .append(n)
                    .append("</a><BR>");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(name);
    }

}
