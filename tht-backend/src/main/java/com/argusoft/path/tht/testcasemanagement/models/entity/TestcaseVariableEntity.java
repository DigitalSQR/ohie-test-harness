package com.argusoft.path.tht.testcasemanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateMetaEntity;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * This model is mapped to testcaseVariablesEntity table in database.
 *
 * @author Aastha
 */
@Entity
@Audited
@Table(name = "testcase_variables")
public class TestcaseVariableEntity extends IdStateMetaEntity {

    @Column(name = "testcase_variable_key")
    private String testcaseVariableKey;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "roleId")
    private String roleId;

    @ManyToOne
    @JoinColumn(name = "testcase_id")
    private TestcaseEntity testcase;

    public TestcaseVariableEntity(){

    }

    public TestcaseVariableEntity(TestcaseVariableEntity testcaseVariablesEntity) {
       super(testcaseVariablesEntity);
       this.setDefaultValue(testcaseVariablesEntity.getDefaultValue());
       this.setTestcaseVariableKey(testcaseVariablesEntity.getTestcaseVariableKey());
       this.setRoleId(testcaseVariablesEntity.getRoleId());
       this.setTestcase(testcaseVariablesEntity.getTestcase());
    }

    public String getTestcaseVariableKey() {
        return testcaseVariableKey;
    }

    public void setTestcaseVariableKey(String testcaseVariableKey) {
        this.testcaseVariableKey = testcaseVariableKey;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public TestcaseEntity getTestcase() {
        return testcase;
    }

    public void setTestcase(TestcaseEntity testcase) {
        this.testcase = testcase;
    }
}
