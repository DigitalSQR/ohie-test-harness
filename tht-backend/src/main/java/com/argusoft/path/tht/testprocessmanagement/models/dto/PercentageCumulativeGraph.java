package com.argusoft.path.tht.testprocessmanagement.models.dto;

public class PercentageCumulativeGraph {
    private String componentName;
    private int componentRank;
    private Long compliantTestRequests;
    private Long totalTestRequests;

    public PercentageCumulativeGraph() {
    }

    public PercentageCumulativeGraph(String componentName, int componentRank, Long compliantTestRequests, Long totalTestRequests) {
        this.componentName = componentName;
        this.componentRank = componentRank;
        this.compliantTestRequests = compliantTestRequests;
        this.totalTestRequests = totalTestRequests;
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

    public Long getCompliantTestRequests() {
        return compliantTestRequests;
    }

    public void setCompliantTestRequests(Long compliantTestRequests) {
        this.compliantTestRequests = compliantTestRequests;
    }

    public Long getTotalTestRequests() {
        return totalTestRequests;
    }

    public void setTotalTestRequests(Long totalTestRequests) {
        this.totalTestRequests = totalTestRequests;
    }

    @Override
    public String toString() {
        return "PercentageCumulativeGraph{" +
                "componentName='" + componentName + '\'' +
                ", componentRank=" + componentRank +
                ", compliantTestRequests=" + compliantTestRequests +
                ", totalTestRequests=" + totalTestRequests +
                '}';
    }
}
