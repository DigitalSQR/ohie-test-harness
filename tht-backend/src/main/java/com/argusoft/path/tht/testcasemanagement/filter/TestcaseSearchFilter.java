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
 * SearchFilter for Testcase.
 *
 * @author Dhruv
 */
public class TestcaseSearchFilter {

    @ApiParam(
            value = "name of the testcase"
    )
    private String name;

    @ApiParam(
            value = "nameSearchType of the testcase"
    )
    private SearchType nameSearchType;

    @ApiParam(
            value = "state of the testcase"
    )
    private String state;

    @ApiParam(
            value = "stateSearchType of the testcase"
    )
    private SearchType stateSearchType;

    @ApiParam(
            value = "specificationId of the testcase"
    )
    private String specificationId;

    @ApiParam(
            value = "isManual of the testcase"
    )
    private Boolean isManual;

    public TestcaseSearchFilter() {
    }

    public TestcaseSearchFilter(String name,
                                SearchType nameSearchType,
                                String state,
                                SearchType stateSearchType,
                                String specificationId,
                                Boolean isManual) {
        this.name = name;
        this.nameSearchType = nameSearchType;
        this.state = state;
        this.stateSearchType = stateSearchType;
        this.specificationId = specificationId;
        this.isManual = isManual;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(name) && StringUtils.isEmpty(state) && StringUtils.isEmpty(specificationId) && isManual != null;
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

    public String getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(String specificationId) {
        this.specificationId = specificationId;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }
}
