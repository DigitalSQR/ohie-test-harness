package com.argusoft.path.tht.testprocessmanagement.models.dto;

import java.util.List;

public class AwardGraph {
    private String componentName;
    private int componentRank;

    private List<AwardApplication> awardApplicationList;

    public AwardGraph() {
    }

    public AwardGraph(String componentName, int componentRank, List<AwardApplication> awardApplicationList) {
        this.componentName = componentName;
        this.componentRank = componentRank;
        this.awardApplicationList = awardApplicationList;
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

    public List<AwardApplication> getAwardApplicationList() {
        return awardApplicationList;
    }

    public void setAwardApplicationList(List<AwardApplication> awardApplicationList) {
        this.awardApplicationList = awardApplicationList;
    }

    @Override
    public String toString() {
        return "AwardGraph{" +
                "componentName='" + componentName + '\'' +
                ", componentRank=" + componentRank +
                ", awardApplicationList=" + awardApplicationList +
                '}';
    }
}
