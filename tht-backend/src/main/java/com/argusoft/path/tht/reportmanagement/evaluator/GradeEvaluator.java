package com.argusoft.path.tht.reportmanagement.evaluator;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.GradeEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.GradeService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants.TESTCASE_RESULT_REF_OBJ_URI;

@Component
public class GradeEvaluator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GradeEvaluator.class);

    @Autowired
    private GradeService gradeService;

    @Autowired
    private TestcaseResultService testcaseResultService;

    public String evaluate(List<TestcaseResultEntity> testcaseResultEntities, ContextInfo contextInfo) throws OperationFailedException{

        /* List<TestcaseResultEntity> specificationTestcaseResultEntities;
         TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = getTestcaseResultCriteriaSearchFilter(testcaseResultEntity);

         specificationTestcaseResultEntities = testcaseResultService.searchTestcaseResults(testcaseResultCriteriaSearchFilter, contextInfo);

*/
            /*int totalElements = specificationTestcaseResultEntities.size();
            int successElements = 0;

            for (TestcaseResultEntity specificationTestcaseResultEntity : specificationTestcaseResultEntities) {
                TestcaseResultEntity testcaseResultStatus = testcaseResultService.getTestcaseResultStatus(specificationTestcaseResultEntity.getId(),
                        null, null, null, true, null, null, contextInfo);
                successElements = testcaseResultStatus.getSuccess() ? successElements + 1 : successElements;
            }
*/
        int totalElements = testcaseResultEntities.size();
        int successElements = testcaseResultEntities.stream().filter(testcaseResultEntity -> Boolean.TRUE.equals(testcaseResultEntity.getSuccess())).toList().size();
        if (totalElements != 0) {
            int percentage = getPercentage(successElements, totalElements);
            return getGradeBasedOnPercentage(percentage, contextInfo);
        }

        return null;
    }

    private static TestcaseResultCriteriaSearchFilter getTestcaseResultCriteriaSearchFilter(TestcaseResultEntity testcaseResultEntity) {
        TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter  = new TestcaseResultCriteriaSearchFilter();

        if(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI.equals(testcaseResultEntity.getRefObjUri())) {
            testcaseResultCriteriaSearchFilter.setTestRequestId(testcaseResultEntity.getTestRequest().getId());
            testcaseResultCriteriaSearchFilter.setRefObjUri(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI);
        }
        else if(ComponentServiceConstants.COMPONENT_REF_OBJ_URI.equals(testcaseResultEntity.getRefObjUri())){
            testcaseResultCriteriaSearchFilter.setParentTestcaseResultId(testcaseResultEntity.getId());
        }
        return testcaseResultCriteriaSearchFilter;
    }

    private int getPercentage(int successElements, int totalElements) {
        return Math.round((float) (successElements * 100) / totalElements);
    }

    private String getGradeBasedOnPercentage(int percentage, ContextInfo contextInfo) {
        Optional<GradeEntity> gradeBasedOnPercentageRange = gradeService.getGradeBasedOnPercentageRange(percentage, contextInfo);
        return gradeBasedOnPercentageRange.map(GradeEntity::getGrade).orElse(null);
    }
}

