package com.argusoft.path.tht.testprocessmanagement.models.dto;

public class Component {
    private String componentName;
    private int componentRank;
    private int testcasesPassed;
    private int totalTestcases;

    public Component() {
    }

    public Component(String componentName, int componentRank, int testcasesPassed, int totalTestcases) {
        this.componentName = componentName;
        this.componentRank = componentRank;
        this.testcasesPassed = testcasesPassed;
        this.totalTestcases = totalTestcases;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public int getComponentRank() {
        return componentRank;
    }

    public void setComponentRank(int componentRank) {
        this.componentRank = componentRank;
    }

    public int getTestcasesPassed() {
        return testcasesPassed;
    }

    public void setTestcasesPassed(int testcasesPassed) {
        this.testcasesPassed = testcasesPassed;
    }

    public int getTotalTestcases() {
        return totalTestcases;
    }

    public void setTotalTestcases(int totalTestcases) {
        this.totalTestcases = totalTestcases;
    }

    @Override
    public String toString() {
        return "Component{" +
                "componentName='" + componentName + '\'' +
                ", componentRank=" + componentRank +
                ", testcasesPassed=" + testcasesPassed +
                ", totalTestcases=" + totalTestcases +
                '}';
    }
}
