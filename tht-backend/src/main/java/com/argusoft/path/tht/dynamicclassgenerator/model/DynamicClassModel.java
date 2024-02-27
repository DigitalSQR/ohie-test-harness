package com.argusoft.path.tht.dynamicclassgenerator.model;

import java.util.List;

public class DynamicClassModel {

    private String methodBody;

    private List<String> input;

    public String getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(String methodBody) {
        this.methodBody = methodBody;
    }

    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
    }
}
