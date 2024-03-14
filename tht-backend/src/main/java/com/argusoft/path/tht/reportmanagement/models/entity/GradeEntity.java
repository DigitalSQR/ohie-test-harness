package com.argusoft.path.tht.reportmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdMetaEntity;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This model is mapped to Grade table in database.
 *
 * @author Dhruv
 */

@Entity
@Table(name = "grade")
@Audited
public class GradeEntity extends IdMetaEntity {

    @Column(name = "percentage", nullable = false)
    private Integer percentage;

    @Column(name = "grade", nullable = false)
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
