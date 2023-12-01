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
 * SearchFilter for Componenet.
 *
 * @author dhruv
 * @since 2023-09-13
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
            value = "stateSearchType of the TestcaseResult"
    )
    private SearchType stateSearchType;

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

    public TestcaseResultSearchFilter() {
    }

    public TestcaseResultSearchFilter(String name,
                                      SearchType nameSearchType,
                                      String state,
                                      SearchType stateSearchType,
                                      String testerId,
                                      String refObjUri,
                                      String refId,
                                      String testRequestId) {
        this.name = name;
        this.nameSearchType = nameSearchType;
        this.state = state;
        this.stateSearchType = stateSearchType;
        this.testerId = testerId;
        this.refObjUri = refObjUri;
        this.refId = refId;
        this.testRequestId = testRequestId;

    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(name)
                && StringUtils.isEmpty(state)
                && StringUtils.isEmpty(testerId)
                && StringUtils.isEmpty(refObjUri)
                && StringUtils.isEmpty(refId)
                && StringUtils.isEmpty(testRequestId);
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

    public SearchType getStateSearchType() {
        if (stateSearchType == null) {
            return SearchType.CONTAINING;
        }
        return stateSearchType;
    }

    public void setStateSearchType(SearchType stateSearchType) {
        this.stateSearchType = stateSearchType;
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
}
