package com.argusoft.path.tht.reportmanagement.service;

import com.argusoft.path.tht.reportmanagement.models.entity.GradeEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;

import java.util.List;
import java.util.Optional;


public interface GradeService {

    List<GradeEntity> getAllGrades(ContextInfo contextInfo);

    Optional<GradeEntity> getGradeBasedOnPercentageRange(Integer percentage, ContextInfo contextInfo);

    GradeEntity updateGrade(GradeEntity gradeEntity, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException;

    GradeEntity getGradeById(String id, ContextInfo contextInfo) throws DoesNotExistException;


}
