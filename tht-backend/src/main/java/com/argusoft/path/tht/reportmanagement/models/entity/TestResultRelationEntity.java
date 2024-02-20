package com.argusoft.path.tht.reportmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdMetaEntity;
import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "testcase_result_relation")
public class TestResultRelationEntity extends IdMetaEntity {

    @Column(name = "ref_obj_uri")
    private String refObjUri;

    @Column(name = "ref_id")
    private String refId;

    @Column(name = "is_selected")
    private Boolean isSelected;

    @Column(name = "version_of_ref_entity")
    private Long versionOfRefEntity;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "testresult_id")
    private TestcaseResultEntity testcaseResultEntity;


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

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Long getVersionOfRefEntity() {
        return versionOfRefEntity;
    }

    public void setVersionOfRefEntity(Long versionOfRefEntity) {
        this.versionOfRefEntity = versionOfRefEntity;
    }

    public TestcaseResultEntity getTestcaseResultEntity() {
        return testcaseResultEntity;
    }

    public void setTestcaseResultEntity(TestcaseResultEntity testcaseResultEntity) {
        this.testcaseResultEntity = testcaseResultEntity;
    }
}
