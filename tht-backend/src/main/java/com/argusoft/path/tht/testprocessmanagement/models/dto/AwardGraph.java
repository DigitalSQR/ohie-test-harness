package com.argusoft.path.tht.testprocessmanagement.models.dto;

public class AwardGraph {
    private String componentName;
    private int componentRank;
    private int passedTestcases;
    private int totalTestcases;
    private String appName;

    public AwardGraph() {
    }

    public AwardGraph(String componentName, int componentRank, int passedTestcases, int totalTestcases, String appName) {
        this.componentName = componentName;
        this.componentRank = componentRank;
        this.passedTestcases = passedTestcases;
        this.totalTestcases = totalTestcases;
        this.appName = appName;
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

    public int getPassedTestcases() {
        return passedTestcases;
    }

    public void setPassedTestcases(int passedTestcases) {
        this.passedTestcases = passedTestcases;
    }

    public int getTotalTestcases() {
        return totalTestcases;
    }

    public void setTotalTestcases(int totalTestcases) {
        this.totalTestcases = totalTestcases;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "AwardGraph{" +
                "componentName='" + componentName + '\'' +
                ", componentRank=" + componentRank +
                ", passedTestcases=" + passedTestcases +
                ", totalTestcases=" + totalTestcases +
                ", appName='" + appName + '\'' +
                '}';
    }
}
