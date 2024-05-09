package com.argusoft.path.tht.reportmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdEntity;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * This model is mapped to TestcaseResultAttributes table in database.
 *
 * @author Bhavi
 */

@Entity
@Audited
@Table(name = "TestCaseResultAttributes")
public class TestcaseResultAttributesEntity extends IdEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "testcaseResultId")
    private TestcaseResultEntity testcaseResultEntity;

    @Column(name = "attribute_key")
    private String key;

    @Column(name = "attribute_value")
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TestcaseResultEntity getTestcaseResultEntity() {
        return testcaseResultEntity;
    }

    public void setTestcaseResultEntity(TestcaseResultEntity testcaseResultEntity) {
        this.testcaseResultEntity = testcaseResultEntity;
    }

    @Override
    public String toString() {
        return "TestcaseResultAttributesEntity{"
                + "key='" + key + '\''
                + ", value='" + value + '\''
                + '}';
    }
}
