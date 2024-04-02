package com.argusoft.path.tht.testprocessmanagement.models.dto;

import java.util.List;
import java.util.Map;

public class GraphInfo {

    private int totalApplications;
    private int assesseeRegistered;
    private float complianceRate;
    private float testingRate;
    private List<ApplicationRequests> applicationRequestsByMonth;
    private List<CompliantApplication> compliantApplications;
    private Map<String,Integer> pieChart;
    private List<AwardGraph> awardGraph;
    private List<PercentageCumulativeGraph> percentageCumulativeGraph;

    public GraphInfo() {
    }

    public GraphInfo(int totalApplications, int assesseeRegistered, float complianceRate, float testingRate, List<ApplicationRequests> applicationRequestsByMonth, List<CompliantApplication> compliantApplications, Map<String, Integer> pieChart, List<AwardGraph> awardGraph, List<PercentageCumulativeGraph> percentageCumulativeGraph) {
        this.totalApplications = totalApplications;
        this.assesseeRegistered = assesseeRegistered;
        this.complianceRate = complianceRate;
        this.testingRate = testingRate;
        this.applicationRequestsByMonth = applicationRequestsByMonth;
        this.compliantApplications = compliantApplications;
        this.pieChart = pieChart;
        this.awardGraph = awardGraph;
        this.percentageCumulativeGraph = percentageCumulativeGraph;
    }

    public int getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(int totalApplications) {
        this.totalApplications = totalApplications;
    }

    public int getAssesseeRegistered() {
        return assesseeRegistered;
    }

    public void setAssesseeRegistered(int assesseeRegistered) {
        this.assesseeRegistered = assesseeRegistered;
    }

    public float getComplianceRate() {
        return complianceRate;
    }

    public void setComplianceRate(float complianceRate) {
        this.complianceRate = complianceRate;
    }

    public float getTestingRate() {
        return testingRate;
    }

    public void setTestingRate(float testingRate) {
        this.testingRate = testingRate;
    }

    public List<CompliantApplication> getCompliantApplications() {
        return compliantApplications;
    }

    public void setCompliantApplications(List<CompliantApplication> compliantApplications) {
        this.compliantApplications = compliantApplications;
    }

    public List<ApplicationRequests> getApplicationRequestsByMonth() {
        return applicationRequestsByMonth;
    }

    public void setApplicationRequestsByMonth(List<ApplicationRequests> applicationRequestsByMonth) {
        this.applicationRequestsByMonth = applicationRequestsByMonth;
    }

    public Map<String, Integer> getPieChart() {
        return pieChart;
    }

    public void setPieChart(Map<String, Integer> pieChart) {
        this.pieChart = pieChart;
    }

    public List<AwardGraph> getAwardGraph() {
        return awardGraph;
    }

    public void setAwardGraph(List<AwardGraph> awardGraph) {
        this.awardGraph = awardGraph;
    }

    public List<PercentageCumulativeGraph> getPercentageCumulativeGraph() {
        return percentageCumulativeGraph;
    }

    public void setPercentageCumulativeGraph(List<PercentageCumulativeGraph> percentageCumulativeGraph) {
        this.percentageCumulativeGraph = percentageCumulativeGraph;
    }

    @Override
    public String toString() {
        return "GraphInfo{" +
                "totalApplications=" + totalApplications +
                ", assesseeRegistered=" + assesseeRegistered +
                ", complianceRate=" + complianceRate +
                ", testingRate=" + testingRate +
                ", applicationRequestsByMonth=" + applicationRequestsByMonth +
                ", compliantApplications=" + compliantApplications +
                ", pieChart=" + pieChart +
                ", awardGraph=" + awardGraph +
                ", percentageCumulativeGraph=" + percentageCumulativeGraph +
                '}';
    }
}
