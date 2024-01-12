/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.reportmanagement.filter;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;

/**
 * SearchFilter for TestCaseResult.
 *
 * @author Dhruv
 */
public class TestcaseResultSearchFilter {

    @ApiParam(
            value = "name of the TestcaseResult"
    )
    private String name;

    @ApiParam(
            value = "nameSearchType of the TestcaseResult"
    )
    private SearchType nameSearchType;

    @ApiParam(
            value = "state of the TestcaseResult"
    )
    private String state;

    @ApiParam(
            value = "testerId of the TestcaseResult"
    )
    private String testerId;

    @ApiParam(
            value = "refObjUri of the TestcaseResult"
    )
    private String refObjUri;

    @ApiParam(
            value = "refId of the TestcaseResult"
    )
    private String refId;

    @ApiParam(
            value = "testRequestId of the TestcaseResult"
    )
    private String testRequestId;

    @ApiParam(
            value = "parentTestcaseResultId of the TestcaseResult"
    )
    private String parentTestcaseResultId;

    @ApiParam(
            value = "isManual of the TestcaseResult"
    )
    private Boolean isManual;

    public TestcaseResultSearchFilter() {
    }

    public TestcaseResultSearchFilter(String name,
                                      SearchType nameSearchType,
                                      String state,
                                      String testerId,
                                      String refObjUri,
                                      String refId,
                                      String testRequestId,
                                      Boolean isManual,
                                      String parentTestcaseResultId) {
        this.name = name;
        this.nameSearchType = nameSearchType;
        this.state = state;
        this.testerId = testerId;
        this.refObjUri = refObjUri;
        this.refId = refId;
        this.testRequestId = testRequestId;
        this.isManual = isManual;
        this.parentTestcaseResultId = parentTestcaseResultId;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(name)
                && StringUtils.isEmpty(state)
                && StringUtils.isEmpty(testerId)
                && StringUtils.isEmpty(refObjUri)
                && StringUtils.isEmpty(refId)
                && StringUtils.isEmpty(testRequestId)
                && isManual == null
                && StringUtils.isEmpty(parentTestcaseResultId);
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public String getParentTestcaseResultId() {
        return parentTestcaseResultId;
    }

    public void setParentTestcaseResultId(String parentTestcaseResultId) {
        this.parentTestcaseResultId = parentTestcaseResultId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SearchType getNameSearchType() {
        if (nameSearchType == null) {
            return SearchType.CONTAINING;
        }
        return nameSearchType;
    }

    public void setNameSearchType(SearchType nameSearchType) {
        this.nameSearchType = nameSearchType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTesterId() {
        return testerId;
    }

    public void setTesterId(String testerId) {
        this.testerId = testerId;
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

    public String getTestRequestId() {
        return testRequestId;
    }

    public void setTestRequestId(String testRequestId) {
        this.testRequestId = testRequestId;
    }

    public Boolean getIsManual() {
        return isManual;
    }

    public void setIsManual(Boolean isManual) {
        this.isManual = isManual;
    }
}
