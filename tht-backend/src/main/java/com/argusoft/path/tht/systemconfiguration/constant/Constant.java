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
 * @author Dhruv
 */
public final class Constant {

    public static final String DEFAULT_SYSTEM_USER_ID = "SYSTEM_USER";
    public static final String ANONYMOUS_USER = "anonymousUser";
    public static final String ANONYMOUS_USER_NAME = "ANONYMOUS_USER";

    // Constants for validating
    public static final String CREATE_VALIDATION = "create.validation";
    public static final String UPDATE_VALIDATION = "update.validation";
    public static final String DELETE_VALIDATION = "delete.validation";
    public static final String TRUE_STRING = "true";
    public static final String FALSE_STRING = "false";
    public static final Pageable SINGLE_VALUE_PAGE = PageRequest.of(0, 1);
    public static final Pageable FULL_PAGE = PageRequest.of(0, Integer.MAX_VALUE);
    public static final Pageable FULL_PAGE_SORT_BY_RANK = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("rank"));
    public static final Pageable TWO_VALUE_PAGE = PageRequest.of(0, 2);
    public static final String NOT_FOUND = " not found";
    public static final String ALLOWED_CHARS_IN_NAMES = "[[A-Z][a-z][0-9][/][\\s][_][@][#][(][)][.][,]['][-][*][`][/][&]]*";
    public static final DateTimeFormatter LOCAL_DATE_DD_MM_YYYY
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final String DATE_FORMATE_STRING = "yyyy-MM-dd";
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
                DEFAULT_SYSTEM_USER_ID,
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
