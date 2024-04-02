package com.argusoft.path.tht.testprocessmanagement.models.dto;

public class AwardApplication {
    private int passedTestcases;
    private int totalTestcases;
    private String appName;

    public AwardApplication() {
    }

    public AwardApplication(int passedTestcases, int totalTestcases, String appName) {
        this.passedTestcases = passedTestcases;
        this.totalTestcases = totalTestcases;
        this.appName = appName;
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
        return "AwardApplication{" +
                "passedTestcases=" + passedTestcases +
                ", totalTestcases=" + totalTestcases +
                ", appName='" + appName + '\'' +
                '}';
    }
}
