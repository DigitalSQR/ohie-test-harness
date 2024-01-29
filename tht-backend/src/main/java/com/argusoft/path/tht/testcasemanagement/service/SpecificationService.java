/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * This interface provides contract for Specification API.
 *
 * @author Dhruv
 */
public interface SpecificationService {

    /**
     * Creates a new Specification.In the Specification Id, Description, and Meta information may
     * not be set in the supplied SpecificationInfo.
     *
     * @param specificationEntity Specification
     * @param contextInfo         information containing the principalId and locale
     *                            information about the caller of service operation
     * @return SpecificationInfo the Specification just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    SpecificationInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     */
    public SpecificationEntity createSpecification(SpecificationEntity specificationEntity,
                                                   ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException;

    /**
     * Updates an existing Specification.
     *
     * @param specificationEntity the new data for the Specification
     * @param contextInfo         information containing the principalId and locale
     *                            information about the caller of service operation
     * @return SpecificationInfo the details of Specification just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    SpecificationInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public SpecificationEntity updateSpecification(SpecificationEntity specificationEntity,
                                                   ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException,
            InvalidParameterException;

    /**
     * Retrieves a list of Specifications corresponding to the given Specification Name.The
     * returned list may be in any order with unique set.
     *
     * @param specificationSearchFilter
     * @param pageable                  Contains Index number of the Page, Max size of the single
     *                                  page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo               information containing the principalId and locale
     *                                  information about the caller of service operation
     * @return a list of Specification name start with given SpecificationName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<SpecificationEntity> searchSpecifications(SpecificationCriteriaSearchFilter specificationSearchFilter,
                                                          Pageable pageable,
                                                          ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;


    /**
     * Retrieves a list of Specifications corresponding to the given Specification Name.The
     * returned list may be in any order with unique set.
     *
     * @param specificationSearchFilter
     * @param contextInfo               information containing the principalId and locale
     *                                  information about the caller of service operation
     * @return a list of Specification name start with given SpecificationName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public List<SpecificationEntity> searchSpecifications(SpecificationCriteriaSearchFilter specificationSearchFilter,
                                                          ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Validates a Specification.Depending on the value of validationType, this
     * validation could be limited to tests on just the current object and its
     * directly contained sub-objects or expanded to perform all tests related
     * to this object
     *
     * @param validationTypeKey   the identifier of the extent of validation
     * @param specificationEntity the Specification information to be tested
     * @param contextInfo         information containing the principalId and locale
     *                            information about the caller of service operation
     * @return Results Specification performing the validation
     * @throws InvalidParameterException SpecificationInfo or contextInfo is not valid
     * @throws OperationFailedException  unable to complete request
     */
    public List<ValidationResultInfo> validateSpecification(String validationTypeKey,
                                                            SpecificationEntity specificationEntity,
                                                            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException;

    /**
     * Retrieves a Specification corresponding to the given Specification Id.
     *
     * @param specificationId SpecificationId of Specification to be retrieved
     * @param contextInfo     information containing the principalId and locale
     *                        information about the caller of service operation
     * @return a list of Specification
     * @throws DoesNotExistException     a SpecificationId in SpecificationIds not found
     * @throws InvalidParameterException invalid contextInfo
     */
    public SpecificationEntity getSpecificationById(String specificationId,
                                                    ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException;

    /**
     * Retrieves a list of Specifications.The returned list may be in any order with
     * unique set.
     *
     * @param pageable    Contains Index number of the Page, Max size of the single
     *                    page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return a list of Specification
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<SpecificationEntity> getSpecifications(Pageable pageable,
                                                       ContextInfo contextInfo)
            throws InvalidParameterException;


    public SpecificationEntity changeState(String specificationId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException;

}
