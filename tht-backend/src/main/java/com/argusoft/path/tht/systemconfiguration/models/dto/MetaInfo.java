/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.models.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * This info is for meta DTO that contains
 * createdAt,createdBy,updatedAt,updatedBy and version.
 *
 * @author dhruv
 */
public class MetaInfo implements Serializable {

    //    @Parameter(description = "The creation time of the model data",
//            allowEmptyValue = true,
//            example = "2014-02-28",
//            dataType = "Date",
//            required = false,
//            readOnly = true)
    private Date createdAt;

    //    @Parameter(description = "The id of the creator",
//            allowEmptyValue = true,
//            example = "1",
//            dataType = "String/Integer",
//            required = false,
//            readOnly = true)
    private String createdBy;

    //    @Parameter(description = "The last updation time of the model data",
//            allowEmptyValue = true,
//            example = "2014-02-28",
//            dataType = "Date",
//            required = false,
//            readOnly = true)
    private Date updatedAt;

    //    @Parameter(description = "The id of the updator",
//            allowEmptyValue = true,
//            example = "1",
//            dataType = "String/Integer",
//            required = false,
//            readOnly = true)
    private String updatedBy;

    //    @Parameter(description = "The version of the model data",
//            allowEmptyValue = false,
//            example = "1",
//            dataType = "Long",
//            required = true,
//            readOnly = true)
    private Long version;

    public MetaInfo() {
    }

    public MetaInfo(Date createdAt, String createdBy, Date updatedAt, String updatedBy, Long version) {
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getCreatedAt() {
        return createdAt != null
                ? new Date(createdAt.getTime()) : null;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return updatedAt != null
                ? new Date(updatedAt.getTime()) : null;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
