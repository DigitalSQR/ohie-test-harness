/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.constant;

import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * This Constant class contains all the common constant variables
 *
 * @author dhruv
 * @since 2023-09-13
 */
public final class Constant {

    public static final String ANONYMOUS_REQUEST = "ANONYMOUS REQUEST";
    public static final String DEFAULT_SYSTEM_USER_NAME = "SYSTEM_USER";
    public static final String ANONYMOUS_USER = "anonymousUser";
    public static final String ANONYMOUS_USER_NAME = "ANONYMOUS_USER";
    public static final String EMPTY = "";
    // Constants for validating for
    public static final String CREATE_VALIDATION = "create.validation";
    public static final String UPDATE_VALIDATION = "update.validation";
    public static final String DELETE_VALIDATION = "delete.validation";
    public static final String TRUE_STRING = "true";
    public static final String FALSE_STRING = "false";
    public static final Integer MAX_IDS_CAN_RETRIVED = 50;
    public static final Pageable LAST_CRETED_TYPE_RECORD_PAGE = PageRequest.of(0, 1, Sort.by("code").descending());
    public static final Pageable FULL_PAGE = PageRequest.of(0, Integer.MAX_VALUE);
    public static final Pageable SINGLE_VALUE_PAGE = PageRequest.of(0, 1);
    public static final Pageable TWO_VALUE_PAGE = PageRequest.of(0, 2);
    public static final String CANT_PARSE_PRINCIPAL_ID = "Can't parse PrincipalId from Context";
    public static final String VERSION_MISMATCH = "Version misMatch";
    public static final String NOT_FOUND = " not found";
    public static final String OPERATION_FAILED = "Operation failed";
    public static final String VALIDATION_ERROR = "Error(s) occurred validating";
    public static final String ID_FOR_UPADATE_NOT_EXIST = "The id supplied to the update does not exists";
    public static final String INVALID_VALIDATION_TYPE_KEY = "Invalid validationTypeKey";
    public static final String NO_META_ON_CREATE = "no meta data should be supplied on a create";
    public static final String STARTED_UPDATING = " started updating, you might want to refresh your page.";
    public static final String MUST_PROVIDED = " must be provided";
    public static final String ID_MISSING = "id is missing";
    public static final String TYPE = "type";
    public static final String STATE = "state";
    public static final String ALLOWED_CHARS_IN_NAMES = "[[A-Z][a-z][0-9][/][\\s][_][@][#][(][)][.][,]['][-][*][`][/][&]]*";
    public static final DateTimeFormatter LOCAL_DATE_DD_MM_YYYY
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final String DATE_FORMATE_STRING = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMATE_STRING = "yyyy-MM-dd hh:mm a";
    public static final String TIME_FORMAT_24_HOURS_STRING = "HH:mm";
    public static final String TIME_FORMAT_12_HOURS_STRING = "hh:mm a";
    public static final DateTimeFormatter LOCAL_DATE_YYYY_MM_DD
            = DateTimeFormatter.ofPattern(DATE_FORMATE_STRING);
    public static final DateTimeFormatter LOCAL_DATE_FORMATE
            = DateTimeFormatter.ofPattern(DATE_FORMATE_STRING);
    public static final DateTimeFormatter LOCAL_DATE_YYYYMMDD
            = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static ContextInfo SUPER_USER_CONTEXT;

    static {
        SUPER_USER_CONTEXT = new ContextInfo(
                "ivasiwala@argusoft.com",
                DEFAULT_SYSTEM_USER_NAME,
                "password",
                true,
                true,
                true,
                true,
                new ArrayList<>()
        );
    }

    private Constant() {
    }
}
