package com.argusoft.path.tht.reportmanagement.evaluator;

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

    public static final Logger LOGGER = LoggerFactory.getLogger(GradeEvaluator.class);


    @Autowired
    private GradeService gradeService;

    @Autowired
    private TestcaseResultService testcaseResultService;

    public String evaluate(String testResultId, ContextInfo contextInfo) throws OperationFailedException {

        String grade = null;

        TestcaseResultEntity testcaseResultInput = null;
        // if testResultId does not exist
        try {
            testcaseResultInput = testcaseResultService.getTestcaseResultById(testResultId, contextInfo);
        } catch (DoesNotExistException | InvalidParameterException e) {
            LOGGER.error("DoesNotExistException or InvalidParameterException while evaluating grade",e);
        }


        if(TESTCASE_RESULT_REF_OBJ_URI.equals(testcaseResultInput.getRefObjUri())){
            return null;
        }

        if(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI.equals(testcaseResultInput.getRefObjUri())
        || ComponentServiceConstants.COMPONENT_REF_OBJ_URI.equals(testcaseResultInput.getRefObjUri())
                || TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI.equals(testcaseResultInput.getRefObjUri())){

            TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();
            testcaseResultCriteriaSearchFilter.setParentTestcaseResultId(testcaseResultInput.getId());
            List<TestcaseResultEntity> testcaseResultEntities = new ArrayList<>();
            try {
                testcaseResultEntities = testcaseResultService.searchTestcaseResults(testcaseResultCriteriaSearchFilter, contextInfo);
            } catch (InvalidParameterException e) {
                LOGGER.error("InvalidParameterException while evaluating grade",e);
            }

            int totalElements = testcaseResultEntities.size();
            int successElements = (int) testcaseResultEntities.stream().filter(testcaseResultEntity -> Boolean.TRUE.equals(testcaseResultEntity.getSuccess()) && testcaseResultEntity.getRequired()).count();
            int percentage;
            if(totalElements == 0){
                percentage = 100;
            }else {
                percentage = getPercentage(successElements, totalElements);
            }
            Optional<GradeEntity> gradeBasedOnPercentageRange = gradeService.getGradeBasedOnPercentageRange(percentage, contextInfo);
            grade = gradeBasedOnPercentageRange.map(GradeEntity::getGrade).orElse(null);

        }
        return grade;

    }


    private Integer getPercentage(int successElements, int totalElements){
        return Math.round((float) (successElements * 100) / totalElements);
    }

}
