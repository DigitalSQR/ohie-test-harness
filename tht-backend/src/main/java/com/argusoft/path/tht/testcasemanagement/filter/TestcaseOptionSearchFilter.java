/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;

/**
 * SearchFilter for TestcaseOption.
 *
 * @author Dhruv
 */
public class TestcaseOptionSearchFilter {

    @ApiParam(
            value = "name of the testcaseOption"
    )
    private String name;

    @ApiParam(
            value = "nameSearchType of the testcaseOption"
    )
    private SearchType nameSearchType;

    @ApiParam(
            value = "state of the testcaseOption"
    )
    private String state;

    @ApiParam(
            value = "stateSearchType of the testcaseOption"
    )
    private SearchType stateSearchType;

    @ApiParam(
            value = "testcaseId of the testcaseOption"
    )
    private String testcaseId;

    public TestcaseOptionSearchFilter() {
    }

    public TestcaseOptionSearchFilter(String name,
                                      SearchType nameSearchType,
                                      String state,
                                      SearchType stateSearchType,
                                      String testcaseId) {
        this.name = name;
        this.nameSearchType = nameSearchType;
        this.state = state;
        this.stateSearchType = stateSearchType;
        this.testcaseId = testcaseId;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(name) && StringUtils.isEmpty(state) && StringUtils.isEmpty(testcaseId);
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

    public String getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(String testcaseId) {
        this.testcaseId = testcaseId;
    }
}
