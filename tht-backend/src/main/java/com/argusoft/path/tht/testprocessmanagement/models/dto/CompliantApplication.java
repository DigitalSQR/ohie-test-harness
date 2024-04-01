package com.argusoft.path.tht.testprocessmanagement.models.dto;

import java.util.List;

public class CompliantApplication {
    private String applicationName;
    private int rank;
    private int testcasesPassed;
    private int totalTestcases;
    private List<Component> components;

    public CompliantApplication() {
    }

    public CompliantApplication(String applicationName, int rank, int testcasesPassed, int totalTestcases, List<Component> components) {
        this.applicationName = applicationName;
        this.rank = rank;
        this.testcasesPassed = testcasesPassed;
        this.totalTestcases = totalTestcases;
        this.components = components;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
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

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return "CompliantApplication{" +
                "applicationName='" + applicationName + '\'' +
                ", rank=" + rank +
                ", testcasesPassed=" + testcasesPassed +
                ", totalTestcases=" + totalTestcases +
                ", components=" + components +
                '}';
    }
}
