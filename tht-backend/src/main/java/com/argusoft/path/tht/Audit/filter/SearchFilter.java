package com.argusoft.path.tht.Audit.filter;

import io.swagger.annotations.ApiParam;

import java.util.List;

public class SearchFilter {

    @ApiParam(
            value = "Name for Audit result"
    )
    private String name;
    @ApiParam(
            value = "Ids for Audit result"
    )
    private List<String> ids;

    @ApiParam(
            value = "revType for Audit result"
    )
    private Byte revType;

    @ApiParam(
            value = "versionNumber for Audit result"
    )
    private Long versionNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public Byte getRevType() {
        return revType;
    }

    public void setRevType(Byte revType) {
        this.revType = revType;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }
    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}