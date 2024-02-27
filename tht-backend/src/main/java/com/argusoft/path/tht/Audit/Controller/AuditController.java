package com.argusoft.path.tht.Audit.Controller;

import com.argusoft.path.tht.Audit.Service.AuditService;
import com.argusoft.path.tht.Audit.constant.AuditServiceConstant;
import com.argusoft.path.tht.Audit.filter.SearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
@Api(value = "REST API for audit services", tags = {"Audit API"})
public class AuditController {

    @Autowired
    AuditService auditService;
    @GetMapping("")
    public List<Object> searchAudit(
            @RequestParam(name = "tableName", required = true) String tableName,
            @RequestParam(name = "id", required = true) List<String> ids,
            @RequestParam(name = "type", required = false) Byte type,
            @RequestParam(name = "version", required = false) Long version,
            @RequestAttribute(name = "contextInfo", required = true) ContextInfo contextInfo)
            throws
            DataValidationErrorException, DoesNotExistException, InvalidParameterException {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setName(tableName);
        searchFilter.setIds(ids);
        searchFilter.setRevType(type);
        searchFilter.setVersionNumber(version);
        List<Object> objects = auditService.searchAudit(searchFilter, contextInfo);
        AuditServiceConstant.EntityType entityType = AuditServiceConstant.EntityType.valueOf(tableName);
        return entityType.createMapperObject(entityType).modelToDto(objects);
    }

}
