package com.argusoft.path.tht.reportmanagement.service.impl;

import com.argusoft.path.tht.reportmanagement.event.TestcaseResultAttributeEvent;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.repository.TestcaseResultAttributesRepository;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultAttributesService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.reportmanagement.validator.TestcaseResultAttributesValidator;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class TestcaseResultAttributesServiceImpl implements TestcaseResultAttributesService {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseResultAttributesServiceImpl.class);
    @Autowired
private TestcaseResultAttributesRepository testcaseResultAttributesRepository;

    @Autowired
    private TestcaseResultAttributesValidator testcaseResultAttributesValidator;

    @Autowired
    TestcaseResultService testcaseResultService;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Optional<TestcaseResultAttributesEntity> getTestcaseResultAttributes(TestcaseResultEntity testcaseResultEntity, String key, ContextInfo contextInfo)
            throws InvalidParameterException
    {
        if(key.isEmpty())
        {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + TestcaseResultAttributesServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("Key is empty");
        }
        return testcaseResultAttributesRepository.findByTestcaseResultEntityAndKey(testcaseResultEntity,key);
    }

    @Override
    public TestcaseResultAttributesEntity createAndChangeTestcaseResultAttributes(TestcaseResultEntity testcaseResultEntity,String Key, String Value,
                                                                                  ContextInfo contextInfo)
            throws
            InvalidParameterException {
        if(Key.isEmpty() || Value.isEmpty())
        {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + TestcaseResultAttributesServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("Key or Value is empty");
        }
        Optional<TestcaseResultAttributesEntity> testcaseResultAttributesEntity = testcaseResultAttributesRepository.findByTestcaseResultEntityAndKey(testcaseResultEntity,Key.toLowerCase());

        TestcaseResultAttributesEntity testcaseResultAttributesEntity1;

        if(testcaseResultAttributesEntity.isEmpty())
        {
            TestcaseResultAttributesEntity newTestcaseResultAttributesEntity = new TestcaseResultAttributesEntity();
            newTestcaseResultAttributesEntity.setTestcaseResultEntity(testcaseResultEntity);
            newTestcaseResultAttributesEntity.setKey(Key.toLowerCase());
            newTestcaseResultAttributesEntity.setValue(Value.toLowerCase());
            testcaseResultAttributesEntity1 = testcaseResultAttributesRepository.saveAndFlush(newTestcaseResultAttributesEntity);
        } else {
            testcaseResultAttributesEntity.get().setValue(Value.toLowerCase());
            testcaseResultAttributesEntity1 = testcaseResultAttributesRepository.saveAndFlush(testcaseResultAttributesEntity.get());
        }
        applicationEventPublisher.publishEvent(new TestcaseResultAttributeEvent(testcaseResultEntity.getId(),contextInfo));
        return testcaseResultAttributesEntity1;
    }

    @Override
    public void deleteTestcaseResultAttributesEntities(TestcaseResultEntity testcaseResultEntity, ContextInfo contextInfo)
            throws DoesNotExistException
    {
        try
        {
            String testcaseResultId = testcaseResultEntity.getId();
            testcaseResultAttributesRepository.deleteByTestcaseResultEntity(testcaseResultEntity);
            testcaseResultAttributesRepository.flush();
            applicationEventPublisher.publishEvent(new TestcaseResultAttributeEvent(testcaseResultId,contextInfo));
        }
        catch (Exception e)
        {
            LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestcaseResultAttributesServiceImpl.class.getSimpleName(), e);
         throw new DoesNotExistException(e.getMessage());
        }
    }
}
