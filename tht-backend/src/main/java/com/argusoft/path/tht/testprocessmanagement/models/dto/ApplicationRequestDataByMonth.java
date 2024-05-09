package com.argusoft.path.tht.testprocessmanagement.models.dto;

public class ApplicationRequestDataByMonth {
    private int month;
    private int compliant;
    private int nonCompliant;

    public ApplicationRequestDataByMonth() {
    }

    public ApplicationRequestDataByMonth(int month, int compliant, int nonCompliant) {
        this.month = month;
        this.compliant = compliant;
        this.nonCompliant = nonCompliant;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
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
}
