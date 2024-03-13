package com.argusoft.path.tht.reportmanagement.service;

import com.argusoft.path.tht.reportmanagement.models.entity.GradeEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;

import java.util.List;
import java.util.Optional;

/**
 * This interface provides contract for Grade API.
 *
 * @author Dhruv
 */
public interface GradeService {

    /**
     * Retrieves all grades
     *
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return list of all grades
     */
    List<GradeEntity> getAllGrades(ContextInfo contextInfo);

    /**
     * Get grade based on percentage range
     *
     * @param percentage percentage based on successful and fail testcases
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return a grade
     */
    Optional<GradeEntity> getGradeBasedOnPercentageRange(Integer percentage, ContextInfo contextInfo);

    /**
     * Update existing grade
     *
     * @param gradeEntity grade
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return updated grade
     * @throws DoesNotExistException a gradeId in gradeIds not found
     * @throws DataValidationErrorException supplied data is invalid
     */
    GradeEntity updateGrade(GradeEntity gradeEntity, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException;

    /**
     * Retrieve grade by existing id
     *
     * @param id grade id
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return a grade
     * @throws DoesNotExistException a gradeId in gradeIds not found
     */
    GradeEntity getGradeById(String id, ContextInfo contextInfo) throws DoesNotExistException;

}
