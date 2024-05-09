package com.argusoft.path.tht.notificationmanagement.filter;

import com.argusoft.path.tht.notificationmanagement.models.entity.NotificationEntity;
import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Criteria Search filter for notification
 *
 * @author Ali
 */
public class NotificationCriteriaSearchFilter extends AbstractCriteriaSearchFilter<NotificationEntity> {

    private String id;

    @ApiParam(
            value = "state of the notification"
    )
    private List<String> state;

    private Root<NotificationEntity> notificationEntityRoot;

    private Join<NotificationEntity, UserEntity> notificationEntityUserEntityJoin;

    public NotificationCriteriaSearchFilter(String id) {
        this.id = id;
    }

    public NotificationCriteriaSearchFilter() {
    }

    @Override
    protected List<Predicate> buildPredicates(Root<NotificationEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        setNotificationEntityRoot(root);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(getNotificationEntityRoot().get("id"), getPrimaryId()));
        }

        if (!CollectionUtils.isEmpty(getState())) {
            predicates.add(criteriaBuilder.in(getNotificationEntityRoot().get("state")).value(getState()));
        }

        return predicates;
    }

    public List<String> getState() {
        return state;
    }

    public void setState(List<String> state) {
        this.state = state;
    }

    public String getPrimaryId() {
        return id;
    }

    private Root<NotificationEntity> getNotificationEntityRoot() {
        return notificationEntityRoot;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<NotificationEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(this.getNotificationEntityUserEntityJoin().get("id"), contextInfo.getUsername()));
        return predicates;
    }

    private void setNotificationEntityRoot(Root<NotificationEntity> notificationEntityRoot) {
        this.notificationEntityRoot = notificationEntityRoot;
        this.notificationEntityUserEntityJoin = null;
    }

    private Join<NotificationEntity, UserEntity> getNotificationEntityUserEntityJoin() {
        if (notificationEntityUserEntityJoin == null) {
            notificationEntityUserEntityJoin = getNotificationEntityRoot().join("receiver");
        }
        return notificationEntityUserEntityJoin;
    }

}
