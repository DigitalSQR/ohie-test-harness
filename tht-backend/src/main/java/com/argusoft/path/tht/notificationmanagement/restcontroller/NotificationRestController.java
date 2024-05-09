package com.argusoft.path.tht.notificationmanagement.restcontroller;

import com.argusoft.path.tht.notificationmanagement.filter.NotificationCriteriaSearchFilter;
import com.argusoft.path.tht.notificationmanagement.models.dto.NotificationInfo;
import com.argusoft.path.tht.notificationmanagement.models.entity.NotificationEntity;
import com.argusoft.path.tht.notificationmanagement.models.mapper.NotificationMapper;
import com.argusoft.path.tht.notificationmanagement.service.NotificationService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This NotificationRestController maps end points with standard service.
 *
 * @author Ali
 */
@RestController
@RequestMapping("/notification")
@Api(value = "REST API for Notification services", tags = {"Notification API"})
public class NotificationRestController {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationRestController.class);

    private NotificationService notificationService;
    private NotificationMapper notificationMapper;

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Autowired
    public void setNotificationMapper(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @ApiOperation(value = "View Notification with supplied id", response = NotificationInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Notification"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{notificationId}")
    public NotificationInfo getNotificationById(@PathVariable("notificationId") String notificationId,
                                                @RequestAttribute("contextInfo") ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException {
        NotificationEntity notification = notificationService.getNotificationById(notificationId, contextInfo);
        return notificationMapper.modelToDto(notification);
    }

    @ApiOperation(value = "View available Notifications with given filters", response = NotificationInfo.class, responseContainer = "Page")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Notifications"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("")
    public Page<NotificationInfo> searchNotification(NotificationCriteriaSearchFilter notificationCriteriaSearchFilter,
                                                     Pageable pageable,
                                                     @RequestAttribute("contextInfo") ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {

        Page<NotificationEntity> notificationBySearchFilter = notificationService.searchNotifications(notificationCriteriaSearchFilter, pageable, contextInfo);
        return notificationMapper.pageEntityToDto(notificationBySearchFilter);
    }

    @ApiOperation(value = "To change status of Notification", response = NotificationInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated Notification"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PatchMapping("/state/{notificationId}/{changeState}")
    @Transactional(rollbackFor = Exception.class)
    public NotificationInfo updateNotificationState(@PathVariable("notificationId") String notificationId,
                                                    @PathVariable("changeState") String changeState,
                                                    @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        NotificationEntity notificationEntity = notificationService.changeState(notificationId, changeState, contextInfo);
        return notificationMapper.modelToDto(notificationEntity);
    }

    @ApiOperation(value = "To change status of all the Notification", response = NotificationInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated Notification"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PatchMapping("/state/bulk/{oldState}/{newState}")
    @Transactional(rollbackFor = Exception.class)
    public List<NotificationInfo> bulkUpdateNotificationState(@PathVariable("oldState") String oldState,
                                                          @PathVariable("newState") String newState,
                                                          @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        List<NotificationEntity> notificationEntities = notificationService.bulkChangeState(oldState, newState, contextInfo);
        return notificationMapper.modelToDto(notificationEntities);
    }

}
