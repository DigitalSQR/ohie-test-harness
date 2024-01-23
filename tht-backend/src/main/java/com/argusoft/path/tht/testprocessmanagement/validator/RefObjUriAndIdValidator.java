package com.argusoft.path.tht.testprocessmanagement.validator;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.constant.*;
import com.argusoft.path.tht.testcasemanagement.service.*;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class RefObjUriAndIdValidator {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private SpecificationService specificationService;

    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private TestcaseOptionService testcaseOptionService;

    @Autowired
    private TestcaseResultService testcaseResultService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private TestRequestService testRequestService;


    List<String> refObjUriList = List.of(
            ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
            DocumentServiceConstants.DOCUMENT_REF_OBJ_URI,
            SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
            TestcaseServiceConstants.TESTCASE_REF_OBJ_URI,
            TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
            TestcaseOptionServiceConstants.TESTCASE_OPTION_REF_OBJ_URI,
            TestcaseResultServiceConstants.TESTCASE_RESULT_REF_OBJ_URI
    );

    public void refObjUriAndIdValidation(String refObjUri, String refId,
                                         ContextInfo contextInfo)throws InvalidParameterException, DoesNotExistException {
        if(!refObjUriList.contains(refObjUri)){
            throw new DoesNotExistException("The RefObjUri supplied for the start process does not exists");
        }

        try{
            if(refObjUri.equals(ComponentServiceConstants.COMPONENT_REF_OBJ_URI)){
                componentService.getComponentById(refId, contextInfo);
            } else if (refObjUri.equals(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI)) {
                specificationService.getSpecificationById(refId, contextInfo);
            } else if (refObjUri.equals(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI)) {
                testcaseService.getTestcaseById(refId, contextInfo);
            } else if (refObjUri.equals(TestcaseOptionServiceConstants.TESTCASE_OPTION_REF_OBJ_URI)) {
                testcaseOptionService.getTestcaseOptionById(refId, contextInfo);
            } else if (refObjUri.equals(TestcaseResultServiceConstants.TESTCASE_RESULT_REF_OBJ_URI)) {
                testcaseResultService.getTestcaseResultById(refId, contextInfo);
            } else if (refObjUri.equals(DocumentServiceConstants.DOCUMENT_REF_OBJ_URI)) {
                documentService.getDocument(refId, contextInfo);
            }else {
                testRequestService.getTestRequestById(refId, contextInfo);
            }

        }catch (DoesNotExistException ex){
            int startIndex = refObjUri.lastIndexOf(".")+1;
            int endIndex = refObjUri.indexOf("Info");
            String entity = refObjUri.substring(startIndex, endIndex);
            throw new DoesNotExistException("The refId supplied for "+entity+" does not exists");
        }
    }

}
