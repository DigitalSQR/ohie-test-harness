package com.argusoft.path.tht.usermanagement.restcontroller;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.filter.UserActivitySearchCriteriaFilter;
import com.argusoft.path.tht.usermanagement.models.dto.UserActivityInfo;
import com.argusoft.path.tht.usermanagement.models.entity.UserActivityEntity;
import com.argusoft.path.tht.usermanagement.models.mapper.UserActivityMapper;
import com.argusoft.path.tht.usermanagement.service.UserActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-activity")
@Api(value = "REST API for User Activity services", tags = {"User Activity API"})
public class UserActivityRestController {
    public static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);

    private UserActivityService userActivityService;
    private UserActivityMapper userActivityMapper;

    @Autowired
    public void setUserActivityService(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @Autowired
    public void setUserActivityMapper(UserActivityMapper userActivityMapper) {
        this.userActivityMapper = userActivityMapper;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View a page of available filtered users", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved page"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were attempting to reach could not be found.")
    })
    @GetMapping("")
    @PreAuthorize(value = "hasAuthority('role.superadmin')")
    public Page<UserActivityInfo> getUserActivity(
            UserActivitySearchCriteriaFilter userActivitySearchCriteriaFilter, Pageable pageable, @RequestAttribute("contextInfo") ContextInfo contextInfo) throws InvalidParameterException {

        Page<UserActivityEntity> userActivityEntities = this.userActivityService.getUserActivity(userActivitySearchCriteriaFilter, pageable, contextInfo);

        return userActivityMapper.pageEntityToDto(userActivityEntities);
    }

}
