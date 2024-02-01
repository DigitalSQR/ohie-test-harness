package com.argusoft.path.tht.reportmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "test_result_option")
@IdClass(TestResultOptionEntityId.class)
public class TestResultOptionEntity implements Serializable {

    @Id
    @Column(name = "test_case_result_id")
    private String testcaseResult;

    @Id
    @Column(name = "test_case_option_id")
    private String testcaseOption;

    @Column(name = "version")
    private long version;

    @Column(name = "is_selected")
    private boolean isSelected;

    public String getTestcaseResult() {
        return testcaseResult;
    }

    public void setTestcaseResult(String testcaseResult) {
        this.testcaseResult = testcaseResult;
    }

    public String getTestcaseOption() {
        return testcaseOption;
    }

    public void setTestcaseOption(String testcaseOption) {
        this.testcaseOption = testcaseOption;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
