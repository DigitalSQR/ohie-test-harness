package com.argusoft.path.tht.reportmanagement.service.impl;

import com.argusoft.path.tht.reportmanagement.models.entity.GradeEntity;
import com.argusoft.path.tht.reportmanagement.repository.GradeRepository;
import com.argusoft.path.tht.reportmanagement.service.GradeService;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;

import io.astefanutti.metrics.aspectj.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Metrics(registry = "GradeServiceImpl")
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeRepository gradeRepository;


    @Override
    public List<GradeEntity> getAllGrades(ContextInfo contextInfo) {
        List<GradeEntity> grades = gradeRepository.findAll();
        grades = grades.stream().sorted(Comparator.comparing(GradeEntity::getGrade)).collect(Collectors.toList());
        return grades;
    }

    @Override
    public GradeEntity getGradeBasedOnPercentageRange(Integer percentage, ContextInfo contextInfo) {
        return gradeRepository.getGradeBasedOnActualPercentage(percentage);
    }

    @Override
    public GradeEntity updateGrade(GradeEntity gradeEntity, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException {
        GradeEntity gradeById = this.getGradeById(gradeEntity.getId(),contextInfo);
        gradeById.setGrade(gradeEntity.getGrade());
        if(gradeEntity.getPercentage() < 0 && gradeEntity.getPercentage() > 100){
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setElement("Percentage");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("Percentage is not in range of 0 and 100");
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
