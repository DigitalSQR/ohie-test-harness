package com.argusoft.path.tht.reportmanagement.service;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;

import java.util.Optional;

public interface TestcaseResultAttributesService {


    public TestcaseResultAttributesEntity createAndChangeTestcaseResultAttributes(TestcaseResultEntity testcaseResultEntity,String Key, String Value,
                                                                         ContextInfo contextInfo)
            throws
            InvalidParameterException, DoesNotExistException;

    public Optional<TestcaseResultAttributesEntity> getTestcaseResultAttributes(TestcaseResultEntity testcaseResultEntity, String key, ContextInfo contextInfo)
        throws DoesNotExistException,
    InvalidParameterException;

    public void deleteTestcaseResultAttributesEntities(TestcaseResultEntity testcaseResultEntity, ContextInfo contextInfo)
        throws DoesNotExistException;

}
