package com.argusoft.path.tht.reportmanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdMetaInfo;

import javax.persistence.Column;

public class GradeInfo extends IdMetaInfo {

    private Integer percentage;

    private String grade;


    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
