package com.argusoft.path.tht.dynamicclassgenerator.restcontroller;

import com.argusoft.path.tht.dynamicclassgenerator.executor.DynamicClassExecutor;
import com.argusoft.path.tht.dynamicclassgenerator.model.DynamicClassModel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dynamic-class")
public class DynamicClassRestController {

    @PostMapping("/configure")
    public ValidationResultInfo result(@RequestBody DynamicClassModel config) {
        try {
            String methodBody = config.getMethodBody();
            List<String> input = config.getInput();

            return DynamicClassExecutor.executeDynamicCode(methodBody, input);
        } catch (Exception e) {
            throw new RuntimeException("Error executing dynamically generated code: " + e.getMessage());
        }
    }

}
