package com.argusoft.path.tht.reportmanagement.service.impl;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapper;
import com.argusoft.path.tht.reportmanagement.repository.TestcaseResultAttributesRepository;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultAttributesService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.reportmanagement.validator.TestcaseResultAttributesValidator;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class TestcaseResultAttributesServiceImpl implements TestcaseResultAttributesService {
    @Autowired
private TestcaseResultAttributesRepository testcaseResultAttributesRepository;

    @Autowired
    private TestcaseResultAttributesValidator testcaseResultAttributesValidator;

    @Autowired
    TestcaseResultMapper testcaseResultMapper;

    @Autowired
    TestcaseResultService testcaseResultService;

    @Autowired
    SimpMessagingTemplate msgTemplate;

    @Override
    public Optional<TestcaseResultAttributesEntity> getTestcaseResultAttributes(TestcaseResultEntity testcaseResultEntity, String key, ContextInfo contextInfo)
            throws InvalidParameterException
    {
        if(key.isEmpty())
        {
            throw new InvalidParameterException("Key is empty");
        }
        return testcaseResultAttributesRepository.findByTestcaseResultEntityAndKey(testcaseResultEntity,key);
    }

    @Override
    public TestcaseResultAttributesEntity createAndChangeTestcaseResultAttributes(TestcaseResultEntity testcaseResultEntity,String Key, String Value,
                                                                                  ContextInfo contextInfo)
            throws
            InvalidParameterException, DoesNotExistException {
        if(Key.isEmpty() || Value.isEmpty())
        {
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
        notifyButton(testcaseResultEntity.getId(), contextInfo);
        return testcaseResultAttributesEntity1;
    }

    @Override
    public void deleteTestcaseResultAttributesEntities(TestcaseResultEntity testcaseResultEntity, ContextInfo contextInfo)
            throws DoesNotExistException
    {
        try
        {
            testcaseResultAttributesRepository.deleteByTestcaseResultEntity(testcaseResultEntity);
            testcaseResultAttributesRepository.flush();
            notifyButton(testcaseResultEntity.getId(), contextInfo);
        }
        catch (Exception e)
        {
         throw new DoesNotExistException(e.getMessage());
        }
    }

    private void notifyButton(
            String testResultId,
            ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException {
        String destination = "/testcase-result/attribute/" + testResultId;
        TestcaseResultEntity testcaseResultEntity = testcaseResultService.getTestcaseResultById(testResultId, contextInfo);
        msgTemplate.convertAndSend(destination, testcaseResultMapper.modelToDto(testcaseResultEntity));
    }

}
