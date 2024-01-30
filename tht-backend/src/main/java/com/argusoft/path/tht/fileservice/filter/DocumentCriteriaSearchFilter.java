package com.argusoft.path.tht.fileservice.filter;

import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentCriteriaSearchFilter extends AbstractCriteriaSearchFilter<DocumentEntity> {

    private String id;

    @ApiParam(
            value = "name of the document"
    )
    private String name;

    @ApiParam(
            value = "refObjUri of the document"
    )
    private String refObjUri;

    @ApiParam(
            value = "refId of the document"
    )
    private String refId;

    @ApiParam(
            value = "ownerId of the document"
    )
    private String ownerId;


    @ApiParam(
            value = "fileId of the document"
    )
    private String fileId;

    @ApiParam(
            value = "states of the document"
    )
    private List<String> state;


    public DocumentCriteriaSearchFilter() {
    }

    public DocumentCriteriaSearchFilter(String id) {
        this.id = id;
    }

    @Override
    public void validateSearchFilter() throws InvalidParameterException {
        if (ValidationUtils.arePairsNullConsistent(refObjUri, refId)) {
            throw new InvalidParameterException("refObjUri and refId must either be null or non-null");
        }
    }

    protected List<Predicate> buildPredicates(Root<DocumentEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(root.get("id"), getPrimaryId()));
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (StringUtils.hasLength(getRefObjUri())) {
            predicates.add(criteriaBuilder.equal(root.get("refObjUri"), refObjUri));
        }

        if (StringUtils.hasLength(getRefId())) {
            predicates.add(criteriaBuilder.equal(root.get("refId"), refId));
        }

        if (StringUtils.hasLength(getFileId())) {
            predicates.add(criteriaBuilder.equal(root.get("fileId"), fileId));
        }

        if (!CollectionUtils.isEmpty(state)) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(state));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<DocumentEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        List<Predicate> predicates = new ArrayList<>();



        if (contextInfo.isAssessee()) {
            Join<DocumentEntity, UserEntity> joinTable = root.join("owner");
            predicates.add(criteriaBuilder.equal(joinTable.get("id"), contextInfo.getUsername()));
        } else {
            if (getOwnerId() != null) {
                Join<DocumentEntity, UserEntity> joinTable = root.join("owner");
                predicates.add(criteriaBuilder.equal(joinTable.get("id"), getOwnerId()));
            }
        }

        return predicates;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRefObjUri() {
        return refObjUri;
    }

    public void setRefObjUri(String refObjUri) {
        this.refObjUri = refObjUri;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public List<String> getState() {
        return state;
    }

    public void setState(List<String> states) {
        this.state = states;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getPrimaryId() {
        return id;
    }

    public void setPrimaryId(String id) {
        this.id = id;
    }
}
