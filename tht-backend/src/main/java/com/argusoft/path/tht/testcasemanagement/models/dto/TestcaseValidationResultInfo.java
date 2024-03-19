package com.argusoft.path.tht.testcasemanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import io.swagger.annotations.ApiModelProperty;


/**
 * Information about the results of a data validation.
 *
 * @author Dhruv
 */
public class TestcaseValidationResultInfo extends ValidationResultInfo {

    @ApiModelProperty(notes = "The refObjUri of the message",
            example = "refObjUri",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String refObjUri;

    @ApiModelProperty(notes = "isAutomated for the testcase",
            example = "isAutomated",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Boolean isAutomated;

    public TestcaseValidationResultInfo() {
        super();
    }

    public TestcaseValidationResultInfo(
            ErrorLevel level,
            String refObjUri,
            String element,
            String message,
            Boolean isAutomated) {
        super(element, level, message);
        this.isAutomated = isAutomated;
        this.refObjUri = refObjUri;
    }

    public String getRefObjUri() {
        return refObjUri;
    }

    public void setRefObjUri(String refObjUri) {
        this.refObjUri = refObjUri;
    }

    public Boolean getAutomated() {
        return isAutomated;
    }

    public void setAutomated(Boolean automated) {
        isAutomated = automated;
    }
}
