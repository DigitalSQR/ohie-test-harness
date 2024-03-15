package com.argusoft.path.tht.reportmanagement.evaluator;

import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.GradeEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.GradeService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Grade Evaluator
 *
 * @author Dhruv
 */

@Component
public class GradeEvaluator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GradeEvaluator.class);


    private GradeService gradeService;
    private TestcaseResultService testcaseResultService;

    private static TestcaseResultCriteriaSearchFilter getTestcaseResultCriteriaSearchFilter(TestcaseResultEntity testcaseResultEntity) {
        TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();

        if (TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI.equals(testcaseResultEntity.getRefObjUri())) {
            testcaseResultCriteriaSearchFilter.setTestRequestId(testcaseResultEntity.getTestRequest().getId());
            testcaseResultCriteriaSearchFilter.setRefObjUri(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI);
        } else if (ComponentServiceConstants.COMPONENT_REF_OBJ_URI.equals(testcaseResultEntity.getRefObjUri())) {
            testcaseResultCriteriaSearchFilter.setParentTestcaseResultId(testcaseResultEntity.getId());
        }
        return testcaseResultCriteriaSearchFilter;
    }

    @Autowired
    public void setGradeService(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @Autowired
    public void setTestcaseResultService(TestcaseResultService testcaseResultService) {
        this.testcaseResultService = testcaseResultService;
    }

    public String evaluate(List<TestcaseResultEntity> testcaseResultEntities, ContextInfo contextInfo){
        int totalElements = testcaseResultEntities.size();
        int successElements = testcaseResultEntities.stream().filter(testcaseResultEntity -> Boolean.TRUE.equals(testcaseResultEntity.getSuccess())).toList().size();
        if (totalElements != 0) {
            int percentage = getPercentage(successElements, totalElements);
            return getGradeBasedOnPercentage(percentage, contextInfo);
        }

        return null;
    }

    private int getPercentage(int successElements, int totalElements) {
        return Math.round((float) (successElements * 100) / totalElements);
    }

    private String getGradeBasedOnPercentage(int percentage, ContextInfo contextInfo) {
        Optional<GradeEntity> gradeBasedOnPercentageRange = gradeService.getGradeBasedOnPercentageRange(percentage, contextInfo);
        return gradeBasedOnPercentageRange.map(GradeEntity::getGrade).orElse(null);
    }
}
