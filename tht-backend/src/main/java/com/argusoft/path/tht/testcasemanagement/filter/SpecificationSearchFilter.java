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
 * SearchFilter for Componenet.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class SpecificationSearchFilter {

    @ApiParam(
            value = "name of the specification"
    )
    private String name;

    @ApiParam(
            value = "nameSearchType of the specification"
    )
    private SearchType nameSearchType;

    @ApiParam(
            value = "state of the specification"
    )
    private String state;

    @ApiParam(
            value = "stateSearchType of the specification"
    )
    private SearchType stateSearchType;

    @ApiParam(
            value = "componentId of the specification"
    )
    private String componentId;

    public SpecificationSearchFilter() {
    }

    public SpecificationSearchFilter(String name,
                                     SearchType nameSearchType,
                                     String state,
                                     SearchType stateSearchType,
                                     String componentId) {
        this.name = name;
        this.nameSearchType = nameSearchType;
        this.state = state;
        this.stateSearchType = stateSearchType;
        this.componentId = componentId;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(name) && StringUtils.isEmpty(state) && StringUtils.isEmpty(componentId);
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

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }
}