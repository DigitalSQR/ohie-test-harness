package com.argusoft.path.tht.testprocessmanagement.models.entity;

import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;

import java.io.Serializable;
import java.util.Objects;

/**
 * This model is to provide identifier for the testRequestUrl table.
 *
 * @author Dhruv
 */
public class TestRequestUrlEntityId implements Serializable {

    private String testRequestId;

    private ComponentEntity component;

    public String getTestRequestId() {
        return testRequestId;
    }

    public void setTestRequestId(String testRequestId) {
        this.testRequestId = testRequestId;
    }

    public ComponentEntity getComponent() {
        return component;
    }

    public void setComponent(ComponentEntity component) {
        this.component = component;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TestRequestUrlEntityId testRequestUrlEntityId = (TestRequestUrlEntityId) o;
        return testRequestId.equals(testRequestUrlEntityId.testRequestId)
                && component.getId().equals(testRequestUrlEntityId.component.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(testRequestId, component.getId());
    }
}