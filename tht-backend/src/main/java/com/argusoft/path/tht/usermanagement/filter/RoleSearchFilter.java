/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.usermanagement.filter;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import org.springframework.util.StringUtils;

/**
 * SearchFilter for Role.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class RoleSearchFilter {

    //    @ApiParam(
//            value = "name of the role"
//    )
    private String name;

    //    @ApiParam(
//            value = "nameSearchType of the role"
//    )
    private SearchType nameSearchType;

    public RoleSearchFilter() {
    }

    public RoleSearchFilter(String name,
                            SearchType nameSearchType) {
        this.name = name;
        this.nameSearchType = nameSearchType;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(name);
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
