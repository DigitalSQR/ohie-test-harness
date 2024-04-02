package com.argusoft.path.tht.testprocessmanagement.models.dto;

import java.util.List;

public class ApplicationRequests {
    private int year;
    private List<ApplicationRequestDataByMonth> applicationRequestDataByMonthList;
    public ApplicationRequests() {
    }

    public ApplicationRequests(int year, List<ApplicationRequestDataByMonth> applicationRequestDataByMonthList) {
        this.year = year;
        this.applicationRequestDataByMonthList = applicationRequestDataByMonthList;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<ApplicationRequestDataByMonth> getApplicationRequestDataByMonthList() {
        return applicationRequestDataByMonthList;
    }

    public void setApplicationRequestDataByMonthList(List<ApplicationRequestDataByMonth> applicationRequestDataByMonthList) {
        this.applicationRequestDataByMonthList = applicationRequestDataByMonthList;
    }

    @Override
    public String toString() {
        return "ApplicationRequests{" +
                "year=" + year +
                ", applicationRequestDataByMonthList=" + applicationRequestDataByMonthList +
                '}';
    }
}
