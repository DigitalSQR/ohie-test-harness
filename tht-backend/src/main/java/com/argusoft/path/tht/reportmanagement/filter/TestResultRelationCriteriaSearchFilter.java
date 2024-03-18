package com.argusoft.path.tht.reportmanagement.filter;

import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import io.swagger.annotations.ApiParam;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Search filter for TestResultRelation
 *
 * @author Hardik
 */

public class TestResultRelationCriteriaSearchFilter extends AbstractCriteriaSearchFilter<TestResultRelationEntity> {

    @ApiParam(
            value = "id of the TestResultRelation"
    )
    private List<String> id;

    @ApiParam(
            value = "refObjUri of the TestResultRelation"
    )
    private String refObjUri;

    @ApiParam(
            value = "refId of the TestResultRelation"
    )
    private Set<String> refId;

    @ApiParam(
            value = "isSelected of the TestResultRelation"
    )
    private Boolean isSelected;

    @ApiParam(
            value = "versionOfRefEntity of the TestResultRelation"
    )
    private Long versionOfRefEntity;

    @ApiParam(
            value = "testcaseResultId of the TestResultRelation"
    )
    private String testcaseResultId;

    private Root<TestResultRelationEntity> testResultRelationEntityRoot;

    private Join<TestResultRelationEntity, TestcaseResultEntity> testResultRelationEntityTestcaseResultEntityJoin;

    @Override
    protected List<Predicate> buildPredicates(Root<TestResultRelationEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        this.setTestResultRelationEntityRoot(root);

        List<Predicate> predicates = new ArrayList<>();

        if (!CollectionUtils.isEmpty(getId())) {
            predicates.add(criteriaBuilder.in(this.getTestResultRelationEntityRoot().get("id")).value(getId()));
        }

        if (StringUtils.hasLength(getRefObjUri())) {
            predicates.add(criteriaBuilder.equal(this.getTestResultRelationEntityRoot().get("refObjUri"), getRefObjUri()));
        }

        if (!CollectionUtils.isEmpty(getRefId())) {
            predicates.add(criteriaBuilder.in(this.getTestResultRelationEntityRoot().get("refId")).value(getRefId()));
        }

        if (getVersionOfRefEntity() != null) {
            predicates.add(criteriaBuilder.equal(this.getTestResultRelationEntityRoot().get("versionOfRefEntity"), getVersionOfRefEntity()));
        }

        if (StringUtils.hasLength(getTestcaseResultId())) {
            predicates.add(criteriaBuilder.equal(this.getTestResultRelationEntityTestcaseResultEntityJoin().get("id"), getTestcaseResultId()));
        }

        return predicates;
    }

    public Root<TestResultRelationEntity> getTestResultRelationEntityRoot() {
        return testResultRelationEntityRoot;
    }

    private void setTestResultRelationEntityRoot(Root<TestResultRelationEntity> testResultRelationEntityRoot) {
        this.testResultRelationEntityRoot = testResultRelationEntityRoot;
        testResultRelationEntityTestcaseResultEntityJoin = null;
    }

    public Join<TestResultRelationEntity, TestcaseResultEntity> getTestResultRelationEntityTestcaseResultEntityJoin() {
        if (this.testResultRelationEntityTestcaseResultEntityJoin == null) {
            this.testResultRelationEntityTestcaseResultEntityJoin = getTestResultRelationEntityRoot().join("testcaseResultEntity");
        }
        return testResultRelationEntityTestcaseResultEntityJoin;
    }

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }

    public String getRefObjUri() {
        return refObjUri;
    }

    public void setRefObjUri(String refObjUri) {
        this.refObjUri = refObjUri;
    }

    public Set<String> getRefId() {
        return refId;
    }

    public void setRefId(Set<String> refId) {
        this.refId = refId;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Long getVersionOfRefEntity() {
        return versionOfRefEntity;
    }

    public void setVersionOfRefEntity(Long versionOfRefEntity) {
        this.versionOfRefEntity = versionOfRefEntity;
    }

    public String getTestcaseResultId() {
        return testcaseResultId;
    }

    public void setTestcaseResultId(String testcaseResultId) {
        this.testcaseResultId = testcaseResultId;
    }
}
