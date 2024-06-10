package com.argusoft.path.tht.testprocessmanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestValueEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Criteria Search filter for testcaseVariable
 *
 * @author Aastha
 */

public class TestRequestValueCriteriaSearchFilter extends AbstractCriteriaSearchFilter<TestRequestValueEntity> {

    private String id;

    @ApiParam(
            value = "key of the test request value"
    )
    private String key;

    @ApiParam(
            value = "test request id of the test request value"
    )
    private String testRequestId;

    @ApiParam(
            value = "test case default value id of the test request value"
    )
    private String testcaseVariableId;


    private Root<TestRequestValueEntity> testRequestValueEntityRoot;

    public TestRequestValueCriteriaSearchFilter(String id) {
        this.id = id;
    }

    public TestRequestValueCriteriaSearchFilter() {
    }

    @Override
    protected List<Predicate> buildPredicates(Root<TestRequestValueEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        setTestRequestValueEntityRoot(root);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(getTestRequestValueEntityRoot().get("id"), getPrimaryId()));
        }

        if (getKey() != null) {
            predicates.add(criteriaBuilder.equal(getTestRequestValueEntityRoot().get("key"), getKey()));
        }

        if (getTestRequestId() != null) {
            predicates.add(criteriaBuilder.equal(getTestRequestValueEntityRoot().get("testRequestId"), getTestRequestId()));
        }

        if (getTestcaseVariableId() != null) {
            predicates.add(criteriaBuilder.equal(getTestRequestValueEntityRoot().get("testcaseVariableId"), getTestcaseVariableId()));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildLikePredicates(Root<TestRequestValueEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) throws ParseException {
        return null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private Root<TestRequestValueEntity> getTestRequestValueEntityRoot() {
        return testRequestValueEntityRoot;
    }

    private void setTestRequestValueEntityRoot(Root<TestRequestValueEntity> testRequestValueEntityRoot) {
        this.testRequestValueEntityRoot = testRequestValueEntityRoot;
    }

    public String getPrimaryId() {
        return id;
    }

    public String getTestRequestId() {
        return testRequestId;
    }

    public void setTestRequestId(String testRequestId) {
        this.testRequestId = testRequestId;
    }

    public String getTestcaseVariableId() {
        return testcaseVariableId;
    }

    public void setTestcaseVariableId(String testcaseVariableId) {
        this.testcaseVariableId = testcaseVariableId;
    }
}
