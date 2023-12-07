/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.reportmanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * This info is for TestcaseResult DTO that contains all the TestcaseResult model's data.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class TestcaseResultInfo extends IdStateNameMetaInfo implements Serializable {

    @ApiModelProperty(notes = "The unique rank of the TestcaseResult",
            allowEmptyValue = false,
            example = "1",
            dataType = "Integer",
            required = true)
    private Integer rank;

    @ApiModelProperty(notes = "The testerId of the TestcaseResult",
            allowEmptyValue = false,
            example = "testerId",
            dataType = "String",
            required = false)
    private String testerId;

    @ApiModelProperty(notes = "The refObjUri of the TestcaseResult",
            allowEmptyValue = false,
            example = "refObjUri",
            dataType = "String",
            required = true)
    private String refObjUri;

    @ApiModelProperty(notes = "The refId of the TestcaseResult",
            allowEmptyValue = false,
            example = "refId",
            dataType = "String",
            required = true)
    private String refId;

    @ApiModelProperty(notes = "The message of the TestcaseResult",
            allowEmptyValue = false,
            example = "message",
            dataType = "String",
            required = false)
    private String message;

    @ApiModelProperty(notes = "The testRequestId of the TestcaseResult",
            allowEmptyValue = false,
            example = "testRequestId",
            dataType = "String",
            required = true)
    private String testRequestId;

    @ApiModelProperty(notes = "The hasSystemError of the TestcaseResult",
            allowEmptyValue = false,
            example = "false",
            dataType = "Boolean",
            required = true)
    private Boolean hasSystemError;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer order) {
        this.rank = rank;
    }
}
