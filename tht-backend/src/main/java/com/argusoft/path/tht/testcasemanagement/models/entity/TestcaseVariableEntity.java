package com.argusoft.path.tht.testcasemanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateMetaEntity;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This model is mapped to testcaseVariablesEntity table in database.
 *
 * @author Aastha
 */
@Entity
@Audited
@Table(name = "testcase_variables")
public class TestcaseVariableEntity extends IdStateMetaEntity {

    @Column(name = "key")
    private String key;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "roleId")
    private String roleId;

    @Column(name = "testcase_id")
    private String testcaseId;

    public TestcaseVariableEntity(){

    }

    public TestcaseVariableEntity(TestcaseVariableEntity testcaseVariablesEntity) {
       super(testcaseVariablesEntity);
       this.setDefaultValue(testcaseVariablesEntity.getDefaultValue());
       this.setKey(testcaseVariablesEntity.getKey());
       this.setRoleId(testcaseVariablesEntity.getRoleId());
       this.setTestcaseId(testcaseVariablesEntity.getTestcaseId());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(String testcaseId) {
        this.testcaseId = testcaseId;
    }

}
