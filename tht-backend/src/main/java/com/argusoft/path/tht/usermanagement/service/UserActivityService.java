package com.argusoft.path.tht.usermanagement.service;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.filter.UserActivitySearchCriteriaFilter;
import com.argusoft.path.tht.usermanagement.models.entity.UserActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public interface UserActivityService {

    /**
     * Creates a new User Activity
     * @param userActivityEntity  the userActivity data
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return userActivityInfo for the user Activity just created
     */
    @Async
    @Transactional
    public UserActivityEntity createUserActivity(UserActivityEntity userActivityEntity, ContextInfo contextInfo);

    /**
     * Retrieves a list of User Activity corresponding. The
     * returned list may be in any order.
     *
     * @param userActivitySearchCriteriaFilter
     * @param pageable         Contains Index number of the Page, Max size of the single
     *                         page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo      information containing the principalId and locale
     *                         information about the caller of service operation
     * @return a list of UserActivity
     */
    public Page<UserActivityEntity> getUserActivity(UserActivitySearchCriteriaFilter userActivitySearchCriteriaFilter, Pageable pageable, ContextInfo contextInfo)throws InvalidParameterException;


}
