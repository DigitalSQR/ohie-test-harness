package com.argusoft.path.tht.reportmanagement.service.impl;

import com.argusoft.path.tht.reportmanagement.models.entity.GradeEntity;
import com.argusoft.path.tht.reportmanagement.repository.GradeRepository;
import com.argusoft.path.tht.reportmanagement.service.GradeService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class GradeServiceImpl implements GradeService {

    public static final Logger LOGGER = LoggerFactory.getLogger(GradeServiceImpl.class);

    @Autowired
    private GradeRepository gradeRepository;

    @Override
    public List<GradeEntity> getAllGrades(ContextInfo contextInfo) {
        List<GradeEntity> grades = gradeRepository.findAll();
        grades = grades.stream().sorted(Comparator.comparing(GradeEntity::getGrade)).toList();
        return grades;
    }

    @Override
    public Optional<GradeEntity> getGradeBasedOnPercentageRange(Integer percentage, ContextInfo contextInfo) {
        Page<GradeEntity> gradeBasedOnActualPercentage = gradeRepository.getGradeBasedOnActualPercentage(percentage, Constant.SINGLE_VALUE_PAGE);

        return gradeBasedOnActualPercentage.getContent().stream()
                .findFirst();
    }

    @Override
    public GradeEntity updateGrade(GradeEntity gradeEntity, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException {
        GradeEntity gradeById = this.getGradeById(gradeEntity.getId(), contextInfo);
        gradeById.setGrade(gradeEntity.getGrade());
        if (gradeEntity.getPercentage() < 0 && gradeEntity.getPercentage() > 100) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setElement("Percentage");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("Percentage is not in range of 0 and 100");
            LOGGER.error("{}{}", ValidateConstant.DATA_VALIDATION_EXCEPTION, GradeServiceImpl.class.getSimpleName());
            throw new DataValidationErrorException("Invalid percentage data while updating grade entity", Collections.singletonList(validationResultInfo));
        }
        return null;
    }

    @Override
    public GradeEntity getGradeById(String id, ContextInfo contextInfo) throws DoesNotExistException {
        Optional<GradeEntity> gradeById = gradeRepository.findById(id);
        return gradeById.orElseThrow(() -> new DoesNotExistException("Grade does not found with id : " + id));

    }
}
