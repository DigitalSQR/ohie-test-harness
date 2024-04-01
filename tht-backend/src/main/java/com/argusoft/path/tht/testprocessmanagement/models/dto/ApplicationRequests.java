package com.argusoft.path.tht.testprocessmanagement.models.dto;

public class ApplicationRequests {
    private int month;
    private int year;
    private int compliant;
    private int nonCompliant;

    public ApplicationRequests() {
    }

    public ApplicationRequests(int month, int year, int compliant, int nonCompliant) {
        this.month = month;
        this.year = year;
        this.compliant = compliant;
        this.nonCompliant = nonCompliant;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getCompliant() {
        return compliant;
    }

    public void setCompliant(int compliant) {
        this.compliant = compliant;
    }

    public int getNonCompliant() {
        return nonCompliant;
    }

    public void setNonCompliant(int nonCompliant) {
        this.nonCompliant = nonCompliant;
    }

    @Override
    public String toString() {
        return "ApplicationRequests{" +
                "month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", compliant=" + compliant +
                ", nonCompliant=" + nonCompliant +
                '}';
    }
}
