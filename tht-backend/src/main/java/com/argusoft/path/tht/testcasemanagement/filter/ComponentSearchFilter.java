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
 * SearchFilter for Component.
 *
 * @author Dhruv
 */
public class ComponentSearchFilter {

    @ApiParam(
            value = "name of the component"
    )
    private String name;

    @ApiParam(
            value = "nameSearchType of the component"
    )
    private SearchType nameSearchType;

    @ApiParam(
            value = "state of the component"
    )
    private String state;

    public ComponentSearchFilter() {
    }

    public ComponentSearchFilter(String name,
                                 SearchType nameSearchType,
                                 String state) {
        this.name = name;
        this.nameSearchType = nameSearchType;
        this.state = state;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(name);
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
