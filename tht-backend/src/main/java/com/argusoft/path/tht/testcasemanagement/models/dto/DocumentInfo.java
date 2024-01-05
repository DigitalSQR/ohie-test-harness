package com.argusoft.path.tht.testcasemanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class DocumentInfo  extends IdStateNameMetaInfo implements Serializable {

    @ApiModelProperty(notes = "The unique rank of the component",
            allowEmptyValue = false,
            example = "1",
            dataType = "Integer",
            required = false)
    private Integer order;

    @ApiModelProperty(notes = "The refObjUri of the TestcaseResult",
            allowEmptyValue = false,
            example = "refObjUri",
            dataType = "String",
            required = false)
    private String refObjUri;

    @ApiModelProperty(notes = "The refId of the TestcaseResult",
            allowEmptyValue = false,
            example = "refId",
            dataType = "String",
            required = false)
    private String refId;

    @ApiModelProperty(notes = "The fileId of the Saved File",
            allowEmptyValue = true,
            example = "fileId",
            dataType = "String",
            required = false)
    private String fileId;

    @ApiModelProperty(notes = "The fileType of the Saved File",
            allowEmptyValue = true,
            example = "fileType",
            dataType = "String",
            required = false)
    private String fileType;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

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

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
