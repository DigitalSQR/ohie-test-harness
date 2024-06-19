package com.argusoft.path.tht.testprocessmanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Criteria search filter for test request
 *
 * @author Hardik
 */

public class TestRequestCriteriaSearchFilter extends AbstractCriteriaSearchFilter<TestRequestEntity> {

    Root<TestRequestEntity> testRequestEntityRoot;
    Join<TestRequestEntity, UserEntity> testRequestEntityUserEntityJoin;
    private String id;
    @ApiParam(
            value = "name of the testRequest"
    )
    private String name;

    @ApiParam(
            value = "company name of the testRequest"
    )
    private String companyName;

    @ApiParam(
            value = "email of the testRequest"
    )
    private String email;

    @ApiParam(
            value = "request date of the testRequest"
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date requestDate;
    @ApiParam(
            value = "state of the testRequest"
    )
    private List<String> states;
    @ApiParam(
            value = "assesseeId of the testRequest"
    )
    private String assesseeId;

    @ApiParam(
            value = "assesseeName of the testRequest"
    )
    private String assesseeName;

    public TestRequestCriteriaSearchFilter(String id) {
        this.id = id;
    }

    public TestRequestCriteriaSearchFilter() {
    }

    @Override
    protected void modifyCriteriaQuery(CriteriaBuilder criteriaBuilder, Root<TestRequestEntity> root, CriteriaQuery<?> query, Pageable pageable) {
        Sort.Order order = pageable.getSort().getOrderFor("default");
        if (order == null) {
            return;
        }

        Expression<Object> stateWiseDefaultOrder = criteriaBuilder.selectCase()
                .when(criteriaBuilder.equal(root.get("state"), TestRequestServiceConstants.TEST_REQUEST_STATUS_PENDING), 0)
                .when(criteriaBuilder.equal(root.get("state"), TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED), 1)
                .when(criteriaBuilder.equal(root.get("state"), TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS), 2)
                .when(criteriaBuilder.equal(root.get("state"), TestRequestServiceConstants.TEST_REQUEST_STATUS_REJECTED), 3)
                .when(criteriaBuilder.equal(root.get("state"), TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED), 4)
                .otherwise(5);

        if (order.isAscending()) {
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
            return predicates;
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getTestRequestEntityRoot().get("name")), getNameBasedOnSearchType(getName())));
        }

        if (!CollectionUtils.isEmpty(getState())) {
            predicates.add(criteriaBuilder.in(getTestRequestEntityRoot().get("state")).value(getState()));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildLikePredicates(Root<TestRequestEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo){
        this.setTestRequestEntityRoot(root);

        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getAssesseeName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getTestRequestEntityUserEntityJoin().get("name")), "%" + assesseeName.toLowerCase() + "%"));
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getTestRequestEntityRoot().get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (!CollectionUtils.isEmpty(getState())) {
            predicates.add(criteriaBuilder.in(getTestRequestEntityRoot().get("state")).value(getState()));
        }

        if (StringUtils.hasLength(getEmail())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getTestRequestEntityUserEntityJoin().get("email")), "%" + email.toLowerCase() + "%"));
        }

        if (StringUtils.hasLength(getCompanyName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getTestRequestEntityUserEntityJoin().get("companyName")), "%" + companyName.toLowerCase() + "%"));
        }

        if (getRequestDate() != null) {
            // Truncate the time part of the requestDate
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getRequestDate());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date truncatedRequestDate = new java.sql.Date(calendar.getTime().getTime());

            // Add the predicate to compare the date part only
            predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.function("DATE", Date.class, root.get("createdAt")),
                    truncatedRequestDate
            ));
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

    public String getAssesseeName() {
        return assesseeName;
    }

    public void setAssesseeName(String assesseeName) {
        this.assesseeName = assesseeName;
    }

    public String getPrimaryId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
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
