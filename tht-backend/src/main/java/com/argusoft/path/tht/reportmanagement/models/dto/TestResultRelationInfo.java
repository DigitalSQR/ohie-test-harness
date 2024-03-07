package com.argusoft.path.tht.reportmanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdMetaInfo;

public class TestResultRelationInfo extends IdMetaInfo {

    private String refObjUri;

    private String refId;

    private boolean isSelected;

    private long versionOfRefEntity;

    private String testcaseResultEntityId;

    public String getRefObjUri() {
        return refObjUri;
    }

    public void setRefObjUri(String refObjUri) {
        this.refObjUri = refObjUri;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getVersionOfRefEntity() {
        return versionOfRefEntity;
    }

    public void setVersionOfRefEntity(long versionOfRefEntity) {
        this.versionOfRefEntity = versionOfRefEntity;
    }

    public String getTestcaseResultEntityId() {
        return testcaseResultEntityId;
    }

    public void setTestcaseResultEntityId(String testcaseResultEntityId) {
        this.testcaseResultEntityId = testcaseResultEntityId;
    }
}
