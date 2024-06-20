package com.argusoft.path.tht.usermanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.CommonUtil;
import com.argusoft.path.tht.usermanagement.filter.UserActivitySearchCriteriaFilter;
import com.argusoft.path.tht.usermanagement.models.entity.UserActivityEntity;
import com.argusoft.path.tht.usermanagement.repository.UserActivityRepository;
import com.argusoft.path.tht.usermanagement.service.UserActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserActivityServiceImpl implements UserActivityService {
    public static final Logger LOGGER = LoggerFactory.getLogger(UserServiceServiceImpl.class);

    UserActivityRepository userActivityRepository;

    @Autowired
    public void setUserActivityRepository(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    @Override
    public UserActivityEntity createUserActivity(UserActivityEntity userActivityEntity, ContextInfo contextInfo) {

        userActivityEntity = userActivityRepository.saveAndFlush(userActivityEntity);

        return userActivityEntity;
    }

    @Override
    public Page<UserActivityEntity> getUserActivity(UserActivitySearchCriteriaFilter userActivitySearchFilter, Pageable pageable, ContextInfo contextInfo) throws InvalidParameterException {
        Specification<UserActivityEntity> userActivityEntitySpecification = userActivitySearchFilter.buildSpecification(pageable, contextInfo);

        return this.userActivityRepository.findAll(userActivityEntitySpecification, CommonUtil.getPageable(pageable));

    }


}
