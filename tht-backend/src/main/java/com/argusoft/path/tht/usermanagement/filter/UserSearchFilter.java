/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.usermanagement.filter;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import io.swagger.annotations.ApiParam;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * SearchFilter for User.
 *
 * @author Dhruv
 */
public class UserSearchFilter {

    @ApiParam(
            value = "name of the user"
    )
    private String name;

    @ApiParam(
            value = "name of the user"
    )
    private List<String> state;

    @ApiParam(
            value = "nameSearchType of the user"
    )
    private SearchType nameSearchType;

    @ApiParam(
            value = "email of the user"
    )
    private String email;

    public UserSearchFilter() {
    }

    public UserSearchFilter(String name,
                            SearchType nameSearchType,
                            List<String> state,
                            String email) {
        this.name = name;
        this.nameSearchType = nameSearchType;
        this.state = state;
        this.email = email;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(name)
                && StringUtils.isEmpty(email)
                && CollectionUtils.isEmpty(state);
    }

    public List<String> getState() {
        return state;
    }

    public void setState(List<String> state) {
        this.state = state;
    }

    public String getEmail() {
        if (StringUtils.isEmpty(email)) {
            email = null;
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        if (StringUtils.isEmpty(name)) {
            name = "";
        }
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
}
