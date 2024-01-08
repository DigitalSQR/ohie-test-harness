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
 * SearchFilter for Specification.
 *
 * @author Dhruv
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
            value = "componentId of the specification"
    )
    private String componentId;

    @ApiParam(
            value = "isManual of the specification"
    )
    private Boolean isManual;

    public SpecificationSearchFilter() {
    }

    public SpecificationSearchFilter(String name,
                                     SearchType nameSearchType,
                                     String state,
                                     String componentId,
                                     Boolean isManual) {
        this.name = name;
        this.nameSearchType = nameSearchType;
        this.state = state;
        this.componentId = componentId;
        this.isManual = isManual;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(name) && StringUtils.isEmpty(state) && StringUtils.isEmpty(componentId) && isManual == null;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
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
    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }
}
