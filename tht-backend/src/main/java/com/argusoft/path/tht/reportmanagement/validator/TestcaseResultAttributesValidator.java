package com.argusoft.path.tht.reportmanagement.validator;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.repository.TestcaseResultAttributesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TestcaseResultAttributesValidator {

    @Autowired
    TestcaseResultAttributesRepository testcaseResultAttributesRepository;

    public TestcaseResultAttributesEntity checkKeyPresence(TestcaseResultAttributesEntity testcaseResultAttributesEntity) {
        Optional<TestcaseResultAttributesEntity> testcaseResultAttributes = testcaseResultAttributesRepository.findByTestcaseResultEntityAndKey(testcaseResultAttributesEntity.getTestcaseResultEntity(), testcaseResultAttributesEntity.getKey());
        if (testcaseResultAttributes.isPresent()) {
            if (!testcaseResultAttributes.get().getKey().equals("is_Interrupted")) {
                testcaseResultAttributes.get().setValue(testcaseResultAttributesEntity.getValue());
            }
            return testcaseResultAttributesRepository.saveAndFlush(testcaseResultAttributes.get());
        }
        return testcaseResultAttributesRepository.saveAndFlush(testcaseResultAttributesEntity);
    }
}
