/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.utils;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This FHIRUtil provides methods for fhir.
 *
 * @author Dhruv
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

    public static Patient createPatient(
            String familyName,
            String givenName,
            String gender,
            String birthDate,
            String identifierSystem,
            String identifierValue,
            boolean active,
            String linkedIdentifierValue,
            String phone,
            String email,
            IGenericClient fhirClient) {
        // Creating a new Patient resource
        Patient patient = new Patient();

        // Setting patient name
        HumanName name = patient.addName();
        name.setFamily(familyName);
        name.addGiven(givenName);

        // Setting patient gender
        if (gender != null) {
            patient.setGender(Enumerations.AdministrativeGender.valueOf(gender.toUpperCase()));
        }

        // Setting patient birth date
        if (birthDate != null) {
            patient.setBirthDateElement(new DateType(birthDate));
        }

        // Setting patient identifier
        Identifier identifier = patient.addIdentifier();
        identifier.setSystem(identifierSystem);
        identifier.setValue(identifierValue);

        // Setting patient active status
        patient.setActive(active);

        // Handling linking scenario
        if (!linkedIdentifierValue.isEmpty()) {
            Resource linkTarget = fhirClient
                    .search()
                    .forResource(Patient.class)
                    .where(Patient.IDENTIFIER.exactly().systemAndCode(identifierSystem, linkedIdentifierValue))
                    .returnBundle(Bundle.class)
                    .execute()
                    .getEntryFirstRep()
                    .getResource();
            patient.addLink().setOther(new Reference(linkTarget));
        }

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

    public static HealthcareService createHealthcareService(String identifierValue, String name, String comment,
                                                            String phone, String email, List<String> specialties, Location location) {
        HealthcareService healthcareService = new HealthcareService();

        // Set the identifier
        Identifier identifier = new Identifier().setSystem("urn:oid:1.2.3.4.5").setValue(identifierValue);
        healthcareService.addIdentifier(identifier);

        //set demographics
        healthcareService.setName(name);
        healthcareService.setComment(comment);

        //set contact details
        ContactPoint phoneContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(phone).setUse(ContactPoint.ContactPointUse.MOBILE);
        ContactPoint emailContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(email).setUse(ContactPoint.ContactPointUse.HOME);
        healthcareService.addTelecom(phoneContact).addTelecom(emailContact);

        // Set specialties
        for (String specialty : specialties) {
            CodeableConcept codeableConcept = new CodeableConcept();
            Coding coding = codeableConcept.addCoding();
            coding.setCode(specialty);
            healthcareService.addSpecialty(codeableConcept);
        }

        //set location
        healthcareService.getLocation().add(new Reference(location));
        return healthcareService;
    }
}
