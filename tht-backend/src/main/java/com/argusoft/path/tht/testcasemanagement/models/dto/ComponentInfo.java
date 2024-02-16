package com.argusoft.path.tht.testcasemanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Set;

/**
 * This info is for Component DTO that contains all the Component model's data.
 *
 * @author Dhruv
 */
public class ComponentInfo extends IdStateNameMetaInfo implements Serializable {

    @ApiModelProperty(notes = "The unique rank of the component",
            allowEmptyValue = false,
            example = "1",
            dataType = "Integer",
            required = true)
    private Integer rank;

    @ApiModelProperty(notes = "The specificationIds for the component",
            allowEmptyValue = false,
            example = "['1','2']",
            dataType = "Set<String>",
            required = false)
    private Set<String> specificationIds;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Set<String> getSpecificationIds() {
        return specificationIds;
    }

    public void setSpecificationIds(Set<String> specificationIds) {
        this.specificationIds = specificationIds;
    }
}
