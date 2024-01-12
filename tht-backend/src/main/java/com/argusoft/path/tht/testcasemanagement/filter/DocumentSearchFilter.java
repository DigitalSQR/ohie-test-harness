package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;

public class DocumentSearchFilter {

    @ApiParam(
            value = "name of the component"
    )
    private String name;


    @ApiParam(
            value = "nameSearchType of the component"
    )
    private SearchType nameSearchType;


    @ApiParam(
            value = "refObjUri of the component"
    )
    private String refObjUri;

    @ApiParam(
            value = "refId of the component"
    )
    private String refId;

    @ApiParam(
            value = "state of the component"
    )
    private String state;


    @ApiParam(
            value = "fileType of the component"
    )
    private String fileType;

    public DocumentSearchFilter(String name, SearchType nameSearchType, String refObjUri, String refId, String state, String fileType) {
        this.name = name;
        this.nameSearchType = nameSearchType;
        this.refObjUri = refObjUri;
        this.refId = refId;
        this.state = state;
        this.fileType = fileType;
    }

    public DocumentSearchFilter() {
    }


    public boolean isEmpty() {
        return StringUtils.isEmpty(name) && StringUtils.isEmpty(state) && StringUtils.isEmpty(refObjUri) && StringUtils.isEmpty(refId) && StringUtils.isEmpty(fileType);
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
