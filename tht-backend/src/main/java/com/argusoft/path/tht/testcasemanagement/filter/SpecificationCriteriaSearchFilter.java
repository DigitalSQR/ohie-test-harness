package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class SpecificationCriteriaSearchFilter extends AbstractCriteriaSearchFilter<SpecificationEntity> {

    private String id;

    @ApiParam(
            value = "name of the specification"
    )
    private String name;

    @ApiParam(
            value = "state of the specification"
    )
    private List<String> state;

    @ApiParam(
            value = "componentId of the specification"
    )
    private String componentId;

    @ApiParam(
            value = "isManual of the testcase"
    )
    private Boolean isManual;


    @ApiParam(
            value = "min rank of the specification"
    )
    private Integer minRank;

    @ApiParam(
            value = "max rank of the specification"
    )
    private Integer maxRank;


    private Root<SpecificationEntity> specificationEntityRoot;
    private Join<SpecificationEntity, ComponentEntity> specificationEntityComponentEntityJoin;

    private Join<SpecificationEntity, TestcaseEntity> specificationEntityTestcaseEntityJoin;

    public SpecificationCriteriaSearchFilter() {
    }

    public SpecificationCriteriaSearchFilter(String id) {
        this.id = id;
    }

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }

    @Override
    protected void modifyCriteriaQuery(CriteriaBuilder criteriaBuilder, Root<SpecificationEntity> root, CriteriaQuery<?> query) {
        if (getManual() != null) {
            query.distinct(true);
        }
    }

    @Override
    protected List<Predicate> buildPredicates(Root<SpecificationEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        this.setSpecificationEntityRoot(root);

        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(this.getSpecificationEntityRoot().get("id"), getPrimaryId()));
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(this.specificationEntityRoot.get("name")), getNameBasedOnSearchType(getName())));

        }

        if (!CollectionUtils.isEmpty(getState())) {
            predicates.add(criteriaBuilder.in(this.getSpecificationEntityRoot().get("state")).value(state));
        }

        if (getComponentId() != null) {
            predicates.add(criteriaBuilder.equal(this.getSpecificationEntityComponentEntityJoin().get("id"), getComponentId()));
        }

        if (getManual() != null) {
            predicates.add(criteriaBuilder.equal(this.getSpecificationEntityTestcaseEntityJoin().get("isManual"), getManual()));
        }

        if (getMinRank() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(this.getSpecificationEntityRoot().get("rank"), getMinRank()));
        }

        if (getMaxRank() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(this.getSpecificationEntityRoot().get("rank"), getMaxRank()));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<SpecificationEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getState() {
        return state;
    }

    public void setState(List<String> state) {
        this.state = state;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getPrimaryId() {
        return id;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public Integer getMinRank() {
        return minRank;
    }

    public void setMinRank(Integer minRank) {
        this.minRank = minRank;
    }

    public Integer getMaxRank() {
        return maxRank;
    }

    public void setMaxRank(Integer maxRank) {
        this.maxRank = maxRank;
    }


    private Root<SpecificationEntity> getSpecificationEntityRoot() {
        return specificationEntityRoot;
    }

    private void setSpecificationEntityRoot(Root<SpecificationEntity> specificationEntityRoot) {
        this.specificationEntityRoot = specificationEntityRoot;
        this.specificationEntityComponentEntityJoin = null;
        this.specificationEntityTestcaseEntityJoin = null;
    }

    private Join<SpecificationEntity, ComponentEntity> getSpecificationEntityComponentEntityJoin() {
        if (this.specificationEntityComponentEntityJoin == null) {
            this.specificationEntityComponentEntityJoin = getSpecificationEntityRoot().join("component");
        }
        return specificationEntityComponentEntityJoin;
    }

    private Join<SpecificationEntity, TestcaseEntity> getSpecificationEntityTestcaseEntityJoin() {
        if (this.specificationEntityTestcaseEntityJoin == null) {
            this.specificationEntityTestcaseEntityJoin = getSpecificationEntityRoot().join("testcases");
        }
        return specificationEntityTestcaseEntityJoin;
    }
}
