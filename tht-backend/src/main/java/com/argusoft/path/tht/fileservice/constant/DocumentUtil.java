package com.argusoft.path.tht.fileservice.constant;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;

import java.util.*;

public class DocumentUtil {

    public static boolean isDocumentTypeValidForEntityRefObjectUri(String documentType, String refObjUri) {
        return getDocumentEntityTypeEnumForDocumentType(refObjUri, documentType).isPresent();
    }

    public static Optional<EntityDocumentTypeEnum> getDocumentEntityTypeEnumForDocumentType(String refObjUri, String documentType) {
        List<EntityDocumentTypeEnum> entityDocumentTypeEnumsForRefObjectUri = getEntityDocumentTypeEnumsForRefObjectUri(refObjUri);
        return entityDocumentTypeEnumsForRefObjectUri.stream().filter(entityDocumentTypeEnum -> entityDocumentTypeEnum.getKey().equals(documentType)).findFirst();
    }

    public static Set<FileType> getAllowedFileTypesForDocumentType(String refObjUriOfDocumentEntity, String documentType) {
        return getDocumentEntityTypeEnumForDocumentType(refObjUriOfDocumentEntity, documentType)
                .map(EntityDocumentTypeEnum::getAllowedFileTypes).orElse(new HashSet<>());
    }

    public static String getAllowedActiveTypeForDocumentType(String refObjUri, String documentType) {
        return getDocumentEntityTypeEnumForDocumentType(refObjUri, documentType)
                .map(EntityDocumentTypeEnum::getAllowedActiveType).orElse(null);
    }

    public static List<EntityDocumentTypeEnum> getEntityDocumentTypeEnumsForRefObjectUri(String refObjectUri) {

        if (UserServiceConstants.USER_REF_OBJ_URI.equals(refObjectUri)) {
            return List.of(UserEntityDocumentTypes.values());
        } else if (TestcaseServiceConstants.TESTCASE_REF_OBJ_URI.equals(refObjectUri)) {
            return List.of(TestcaseEntityDocumentTypes.values());
        } else if (TestcaseResultServiceConstants.TESTCASE_RESULT_REF_OBJ_URI.equals(refObjectUri)) {
            return List.of(TestcaseResultEntityDocumentTypes.values());
        }

        return new ArrayList<>();
    }

}
