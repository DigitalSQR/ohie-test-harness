/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * This interface provides contract for Component API.
 *
 * @author Dhruv
 */
public interface ComponentService {

    /**
     * Creates a new Component.In the Component Id, Description, and Meta information may
     * not be set in the supplied ComponentInfo.
     *
     * @param componentEntity Component
     * @param contextInfo     information containing the principalId and locale
     *                        information about the caller of service operation
     * @return ComponentInfo the Component just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    ComponentInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     */
    public ComponentEntity createComponent(ComponentEntity componentEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException;

    /**
     * Updates an existing Component.
     *
     * @param componentEntity the new data for the Component
     * @param contextInfo     information containing the principalId and locale
     *                        information about the caller of service operation
     * @return ComponentInfo the details of Component just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    ComponentInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public ComponentEntity updateComponent(ComponentEntity componentEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException,
            InvalidParameterException;

    /**
     * Retrieves a list of Components corresponding to the given Component Name.The
     * returned list may be in any order with unique set.
     *
     * @param componentCriteriaSearchFilter
     * @param pageable                      Contains Index number of the Page, Max size of the single
     *                                      page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo                   information containing the principalId and locale
     *                                      information about the caller of service operation
     * @return a list of Component name start with given ComponentName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<ComponentEntity> searchComponents(ComponentCriteriaSearchFilter componentCriteriaSearchFilter,
                                                  Pageable pageable,
                                                  ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;


    /**
     * Retrieves a list of Components corresponding to the given Component Name.The
     * returned list may be in any order with unique set.
     *
     * @param componentCriteriaSearchFilter
     * @param contextInfo                   information containing the principalId and locale
     *                                      information about the caller of service operation
     * @return a list of Component name start with given ComponentName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public List<ComponentEntity> searchComponents(ComponentCriteriaSearchFilter componentCriteriaSearchFilter,
                                                  ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Validates a Component.Depending on the value of validationType, this
     * validation could be limited to tests on just the current object and its
     * directly contained sub-objects or expanded to perform all tests related
     * to this object
     *
     * @param validationTypeKey the identifier of the extent of validation
     * @param componentEntity   the Component information to be tested
     * @param contextInfo       information containing the principalId and locale
     *                          information about the caller of service operation
     * @return Results Component performing the validation
     * @throws InvalidParameterException ComponentInfo or contextInfo is not valid
     * @throws OperationFailedException  unable to complete request
     */
    public List<ValidationResultInfo> validateComponent(String validationTypeKey,
                                                        ComponentEntity componentEntity,
                                                        ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException;

    /**
     * Retrieves a Component corresponding to the given Component Id.
     *
     * @param componentId ComponentId of Component to be retrieved
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return a list of Component
     * @throws DoesNotExistException     a ComponentId in ComponentIds not found
     * @throws InvalidParameterException invalid contextInfo
     */
    public ComponentEntity getComponentById(String componentId,
                                            ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException;

    /**
     * Retrieves a list of Components.The returned list may be in any order with
     * unique set.
     *
     * @param pageable    Contains Index number of the Page, Max size of the single
     *                    page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return a list of Component
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<ComponentEntity> getComponents(Pageable pageable,
                                               ContextInfo contextInfo)
            throws InvalidParameterException;

    public ComponentEntity changeState(String componentId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException;


}
