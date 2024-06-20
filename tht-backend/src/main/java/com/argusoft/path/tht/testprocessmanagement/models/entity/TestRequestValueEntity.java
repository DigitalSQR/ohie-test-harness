package com.argusoft.path.tht.testprocessmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdEntity;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * This model is mapped to testRequestValue table in database.
 *
 * @author Aastha
 */
@Entity
@Audited
@Table(name = "test_request_value")
public class TestRequestValueEntity extends IdEntity {

    @Column(name = "testcase_default_value_id")
    private String testcaseVariableId;

    @Column(name = "testRequestValueInput")
    private String testRequestValueInput;

    @ManyToOne
    @JoinColumn(name = "test_request_id")
    private TestRequestEntity testRequest;


    public TestRequestValueEntity() {
    }

    public TestRequestValueEntity(TestRequestValueEntity testRequestValueEntity){
        this.setTestcaseVariableId(testRequestValueEntity.getTestcaseVariableId());
        this.setTestRequestValueInput(testRequestValueEntity.getTestRequestValueInput());
        this.setTestRequest(testRequestValueEntity.getTestRequest());
    }


    public String getTestcaseVariableId() {
        return testcaseVariableId;
    }

    public void setTestcaseVariableId(String testcaseVariableId) {
        this.testcaseVariableId = testcaseVariableId;
    }

    public String getTestRequestValueInput() {
        return testRequestValueInput;
    }

    public void setTestRequestValueInput(String testRequestValueInput) {
        this.testRequestValueInput = testRequestValueInput;
    }

    public TestRequestEntity getTestRequest() {
        return testRequest;
    }

    public void setTestRequest(TestRequestEntity testRequest) {
        this.testRequest = testRequest;
    }

}
