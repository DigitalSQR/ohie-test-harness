package com.argusoft.path.tht.systemconfiguration.indexapi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This Controller contains APIs for the system base apis.
 *
 * @author Dhruv
 */
@RestController
@RequestMapping("")
@Api(value = "REST Endpoints for Build Info", tags = {"Build Info API"})
public class AppBuildInfoController {

    private Environment env;
    @Autowired
    public void setEnvironment(Environment env){
        this.env = env;
    }

    /**
     * {@inheritdoc}
     * @return 
     */
    @ApiOperation(value = "View current API version", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200,
                message = "Current API virsion"),
        @ApiResponse(code = 401,
                message = "You are not authorized to create the resource"),
        @ApiResponse(code = 403,
                message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping("/build")
    public String apiVersion() {
        String version = env.getProperty("info.app.version");
        String commitsha = env.getProperty("info.build.commit.sha");
        return "<strong>API Version:</strong>  " + version
                + "<br><br>"
                + "<strong>Commit SHA:</strong>  " + commitsha;
    }

    /**
     * {@inheritdoc}
     * @return 
     */
    @ApiOperation(value = "View Index for API", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200,
                message = "Index API"),
        @ApiResponse(code = 401,
                message = "You are not authorized to create the resource"),
        @ApiResponse(code = 403,
                message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping("")
    public String apiIndex() {
        return "        <h2>Testing Harness Tool</h2>"
                + "        <h4>Some useful links</h4>"
                + "        <ul>"
                + "            <li><a href=\"./swagger-ui.html#/\">API Docs</a></li>"
                + "            <li><a href=\"./build\">Build Details</a></li>"
                + "        </ul>";
    }

    /**
     * {@inheritdoc}
     * @return 
     */
    @ApiOperation(value = "View service", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200,
                message = "View service"),
        @ApiResponse(code = 401,
                message = "You are not authorized to create the resource"),
        @ApiResponse(code = 403,
                message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping("/service")
    public ResponseEntity<Object> requestCsrf() {
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }
}
