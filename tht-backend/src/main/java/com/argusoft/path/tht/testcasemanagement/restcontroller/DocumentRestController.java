package com.argusoft.path.tht.testcasemanagement.restcontroller;

import com.argusoft.path.tht.fileservice.InvalidFileTypeException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.dto.DocumentInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.DocumentEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.DocumentMapper;
import com.argusoft.path.tht.testcasemanagement.service.DocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DocumentRestController
 *
 * @author hardik
 */
@RestController
@RequestMapping("/document")
@Api(value = "REST API for Document services", tags = {"Document API"})
public class DocumentRestController {


    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentMapper documentMapper;


    @ApiOperation(value = "Create Document", response = DocumentInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created Document"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")

    })
    @PostMapping
    @Transactional
    public DocumentInfo createDocument(@ModelAttribute DocumentInfo documentInfo,
                                       @ModelAttribute("file") MultipartFile file,
                                       @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws InvalidFileTypeException,
            DataValidationErrorException,
            OperationFailedException {
        DocumentEntity documentEntity = documentMapper.dtoToModel(documentInfo);
        DocumentEntity document = documentService.createDocument(documentEntity, file, getFileTypePdfPngJpeg(),contextInfo);
        documentInfo = documentMapper.modelToDto(document);
        return documentInfo;
    }

    @ApiOperation(value = "View available Document with supplied id", response = DocumentInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Document"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{documentId}")
    public DocumentInfo getDocument(@PathVariable("documentId") String documentId,
                                    @RequestAttribute("contextInfo") ContextInfo contextInfo) throws DoesNotExistException {
        DocumentEntity document = documentService.getDocument(documentId, contextInfo);
        return documentMapper.modelToDto(document);
    }


    @ApiOperation(value = "View a list of documents by refObjectUri and refId", response = DocumentInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved documents by refObjectUri and refId"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping
    public List<DocumentInfo> getDocumentsByRefObjectUriAndRefObjectId(@RequestParam("refObjectUri") String refObjectUri,
                                                                       @RequestParam("refId") String refId,
                                                                       @RequestAttribute("contextInfo") ContextInfo contextInfo){
        List<DocumentEntity> documentsByRefObjectUriAndRefObjectId = documentService.getDocumentsByRefObjectUriAndRefObjectId(refObjectUri, refId, contextInfo);
        return documentMapper.modelToDto(documentsByRefObjectUriAndRefObjectId);
    }

    @ApiOperation(value = "To change status of Document", response = DocumentInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated document"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PutMapping("/change/status/{documentId}/{changeState}")
    @Transactional
    public DocumentInfo updateDocumentState(@PathVariable("documentId") String documentId,
                                              @PathVariable("changeState") String changeState,
                                              @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException {
        DocumentEntity documentEntity = documentService.changeState(documentId, changeState, contextInfo);
        return documentMapper.modelToDto(documentEntity);
    }

    @ApiOperation(value = "To change order of Document", response = DocumentInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated document"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PutMapping("/change/order/{documentId}/{orderId}")
    @Transactional
    public DocumentInfo changeDocumentOrder(@PathVariable("documentId") String documentId,
                                            @PathVariable("orderId") Integer orderId,
                                            @RequestAttribute("contextInfo") ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException {
        DocumentEntity updatedDocumentEntity = documentService.changeOrder(documentId, orderId, contextInfo);
        return documentMapper.modelToDto(updatedDocumentEntity);
    }


    @ApiOperation(value = "To download the document", response = DocumentInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully Downloaded Document"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping("/file/{documentId}")
    public ResponseEntity<Resource> getFileByDocument(@PathVariable("documentId") String documentId,
                                                      @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException {
        ByteArrayResource byteArrayResourceByDocumentId = documentService.getByteArrayResourceByDocumentId(documentId, contextInfo);
        DocumentEntity document = documentService.getDocument(documentId, contextInfo);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + document.getName());

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(byteArrayResourceByDocumentId.contentLength())
                .body(byteArrayResourceByDocumentId);
    }


    private List<String> getFileTypePdfPngJpeg(){
        List<String> validationAgainstTypes = new ArrayList<>();
        validationAgainstTypes.add("application/pdf");
        validationAgainstTypes.add("image/png");
        validationAgainstTypes.add("image/jpeg");

        return validationAgainstTypes;
    }


}
