package com.testing.harness.tool.systemconfiguration.metricsmanagement.service.impl.rest;

import com.testing.harness.tool.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.testing.harness.tool.systemconfiguration.metricsmanagement.util.MetricsUtil;
import com.testing.harness.tool.systemconfiguration.models.dto.ContextInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;

@RestController
@RequestMapping("/metrics")
@Api(value = "REST Endpoints for Metrics", tags = {"Metrics"})
public class MetricsServiceRestController {

    private static final Map<String, ByteArrayOutputStream> map
            = new HashMap<>();

    @Autowired
    MetricsUtil metricsUtil;

    private static final String EQUALS = "===========================================================";

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
        } catch(UnsupportedEncodingException ex) {
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
