package com.argusoft.path.tht.usermanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.models.entity.UserActivityEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserActivitySearchCriteriaFilter extends AbstractCriteriaSearchFilter<UserActivityEntity> {

    private String id;

    @ApiParam(
            value = "Id of the user trying to access the API"
    )
    private String userId;

    @ApiParam(
            value = "Url of the API that is being hit"
    )
    private String apiUrl;

    @ApiParam(
            value = "Scope of the API being hit"
    )
    private List<String> scope;

    @ApiParam(
            value = "start date for the range filter"
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date startDate;

    @ApiParam(
            value = "end date for the range filter"
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date endDate;


    private Root<UserActivityEntity> userActivityEntityRoot;

    public UserActivitySearchCriteriaFilter(String id) {
        this.id = id;
    }

    public UserActivitySearchCriteriaFilter() {

    }

    @Override
    protected List<Predicate> buildPredicates(Root<UserActivityEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        setUserActivityEntityRoot(root);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(getUserActivityEntityRoot().get("id"), getPrimaryId()));
        }

        if (StringUtils.hasLength(getUserId())) {
            predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(getUserActivityEntityRoot().get("userId")), getApiUrl().toLowerCase()));
        }

        if (!CollectionUtils.isEmpty(getScope())) {
            predicates.add(criteriaBuilder.in(getUserActivityEntityRoot().get("scope")).value(getScope()));
        }

        if (StringUtils.hasLength(getApiUrl())) {
            predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(getUserActivityEntityRoot().get("apiUrl")), getApiUrl().toLowerCase()));
        }

        if (getStartDate() != null && getEndDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getEndDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date endDateInclusive = calendar.getTime();
            Predicate startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("accessTime").as(Date.class), getStartDate());
            Predicate endDatePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("accessTime").as(Date.class), endDateInclusive);
            predicates.add(criteriaBuilder.and(startDatePredicate, endDatePredicate));
        }


        return predicates;
    }

    @Override
    protected List<Predicate> buildLikePredicates(Root<UserActivityEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        List<Predicate> predicates = new ArrayList<>();

        if (!CollectionUtils.isEmpty(getScope())) {
            predicates.add(criteriaBuilder.in(getUserActivityEntityRoot().get("scope")).value(getScope()));
        }

        if (!StringUtils.hasLength(getUserId())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getUserActivityEntityRoot().get("userId")), "%" + userId.toLowerCase() + "%"));
        }

        if (!StringUtils.hasLength(getApiUrl())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getUserActivityEntityRoot().get("apiUrl")), "%" + apiUrl.toLowerCase() + "%"));
        }

        if (getStartDate() != null && getEndDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getEndDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date endDateInclusive = calendar.getTime();
            Predicate startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("accessTime").as(Date.class), getStartDate());
            Predicate endDatePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("accessTime").as(Date.class), endDateInclusive);
            predicates.add(criteriaBuilder.and(startDatePredicate, endDatePredicate));
        }

        return predicates;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public List<String> getScope() {
        return scope;
    }

    public void setScope(List<String> scope) {
        this.scope = scope;
    }

    public String getPrimaryId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Root<UserActivityEntity> getUserActivityEntityRoot() {
        return userActivityEntityRoot;
    }

    public void setUserActivityEntityRoot(Root<UserActivityEntity> userActivityEntityRoot) {
        this.userActivityEntityRoot = userActivityEntityRoot;
    }
}
