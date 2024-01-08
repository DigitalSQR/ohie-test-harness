/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.filter;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;

/**
 * SearchFilter for TestRequest.
 *
 * @author Dhruv
 */
public class TestRequestSearchFilter {

    @ApiParam(
            value = "name of the testRequest"
    )
    private String name;

    @ApiParam(
            value = "nameSearchType of the testRequest"
    )
    private SearchType nameSearchType;

    @ApiParam(
            value = "state of the testRequest"
    )
    private String state;

    public TestRequestSearchFilter() {
    }

    public TestRequestSearchFilter(String name,
                                   SearchType nameSearchType,
                                   String state,
                                   SearchType stateSearchType) {
        this.name = name;
        this.nameSearchType = nameSearchType;
        this.state = state;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(name) && StringUtils.isEmpty(state);
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
}
