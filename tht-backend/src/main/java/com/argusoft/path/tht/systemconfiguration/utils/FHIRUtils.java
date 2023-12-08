/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.utils;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.KeyCategory;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import org.hl7.fhir.r4.model.*;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This FHIRUtil provides methods for fhir.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public final class FHIRUtils {

    public static <T> List<T> processBundle(Class<T> type, Bundle bundle) {
        List<T> resources = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            if (entry.getResource() != null && type.isInstance(entry.getResource())) {
                resources.add(type.cast(entry.getResource()));
            }
        }
        return resources;
    }

    public static Patient createPatient(String givenName, String familyName, String gender, String birthDate,
                                        String identifierValue, String phone, String email) {
        Patient patient = new Patient();

        // Set patient demographics
        patient.addName().addGiven(givenName).setFamily(familyName);
        patient.setGender(gender.equals("M") ? Enumerations.AdministrativeGender.MALE : Enumerations.AdministrativeGender.FEMALE);
        patient.setBirthDate(parseDate(birthDate));

        // Set identifier
        Identifier identifier = new Identifier().setSystem("urn:oid:1.2.3.4.5").setValue(identifierValue);
        patient.addIdentifier(identifier);

        // Set contact information
        ContactPoint phoneContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(phone).setUse(ContactPoint.ContactPointUse.MOBILE);
        ContactPoint emailContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(email).setUse(ContactPoint.ContactPointUse.HOME);
        patient.addTelecom(phoneContact).addTelecom(emailContact);

        return patient;
    }

    public static Date parseDate(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date", e);
        }
    }
}