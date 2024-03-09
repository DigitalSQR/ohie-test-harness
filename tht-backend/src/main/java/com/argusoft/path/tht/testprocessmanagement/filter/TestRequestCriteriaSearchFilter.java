package com.argusoft.path.tht.testprocessmanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class TestRequestCriteriaSearchFilter extends AbstractCriteriaSearchFilter<TestRequestEntity> {

    Root<TestRequestEntity> testRequestEntityRoot;
    Join<TestRequestEntity, UserEntity> testRequestEntityUserEntityJoin;
    private String id;
    @ApiParam(
            value = "name of the testRequest"
    )
    private String name;
    @ApiParam(
            value = "state of the testRequest"
    )
    private List<String> states;
    @ApiParam(
            value = "assesseeId of the testRequest"
    )
    private String assesseeId;

    public TestRequestCriteriaSearchFilter(String id) {
        this.id = id;
    }

    public TestRequestCriteriaSearchFilter() {
    }

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }

    @Override
    protected void modifyCriteriaQuery(CriteriaBuilder criteriaBuilder, Root<TestRequestEntity> root, CriteriaQuery<?> query, Pageable pageable) {
        Sort.Order order = pageable.getSort().getOrderFor("default");
        if(order == null) { return; }

        Expression<Object> stateWiseDefaultOrder = criteriaBuilder.selectCase()
                .when(criteriaBuilder.equal(root.get("state"), TestRequestServiceConstants.TEST_REQUEST_STATUS_PENDING), 0)
                .when(criteriaBuilder.equal(root.get("state"), TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED), 1)
                .when(criteriaBuilder.equal(root.get("state"), TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS), 2)
                .when(criteriaBuilder.equal(root.get("state"), TestRequestServiceConstants.TEST_REQUEST_STATUS_REJECTED), 3)
                .when(criteriaBuilder.equal(root.get("state"), TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED), 4)
                .otherwise(5);

        if(order.isAscending()) {
            query.orderBy(criteriaBuilder.asc(stateWiseDefaultOrder));
        } else {
            query.orderBy(criteriaBuilder.desc(stateWiseDefaultOrder));
        }
    }

    @Override
    protected List<Predicate> buildPredicates(Root<TestRequestEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        this.setTestRequestEntityRoot(root);

        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(getTestRequestEntityRoot().get("id"), getPrimaryId()));
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getTestRequestEntityRoot().get("name")),getNameBasedOnSearchType(getName()) ));
        }

        if (!CollectionUtils.isEmpty(getState())) {
            predicates.add(criteriaBuilder.in(getTestRequestEntityRoot().get("state")).value(getState()));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<TestRequestEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {

        List<Predicate> predicates = new ArrayList<>();

        if (contextInfo.isAssessee()) {
            predicates.add(criteriaBuilder.equal(this.getTestRequestEntityUserEntityJoin().get("id"), contextInfo.getUsername()));
        } else {
            if (getAssesseeId() != null) {
                predicates.add(criteriaBuilder.equal(this.getTestRequestEntityUserEntityJoin().get("id"), getAssesseeId()));
            }
        }

        return predicates;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getState() {
        return states;
    }

    public void setState(List<String> states) {
        this.states = states;
    }

    public String getAssesseeId() {
        return assesseeId;
    }

    public void setAssesseeId(String assesseeId) {
        this.assesseeId = assesseeId;
    }

    public String getPrimaryId() {
        return id;
    }

    private Root<TestRequestEntity> getTestRequestEntityRoot() {
        return testRequestEntityRoot;
    }

    private void setTestRequestEntityRoot(Root<TestRequestEntity> testRequestEntityRoot) {
        this.testRequestEntityRoot = testRequestEntityRoot;
        this.testRequestEntityUserEntityJoin = null;
    }

    public Join<TestRequestEntity, UserEntity> getTestRequestEntityUserEntityJoin() {
        if (testRequestEntityUserEntityJoin == null) {
            testRequestEntityUserEntityJoin = getTestRequestEntityRoot().join("assessee");
        }
        return testRequestEntityUserEntityJoin;
    }
}
