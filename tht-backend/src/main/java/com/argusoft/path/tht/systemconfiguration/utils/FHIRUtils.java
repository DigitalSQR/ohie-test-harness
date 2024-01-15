/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.utils;

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

    public static Organization createOrganization(String name,
                                                  String identifierValue, String phone, String email, String city, String state, String country) {
        Organization organization = new Organization();

        // Set organization demographics
        organization.setName(name);
        organization.addAddress().setCity(city);
        organization.addAddress().setState(state);
        organization.addAddress().setCountry(country);

        // Set identifier
        Identifier identifier = new Identifier().setSystem("urn:oid:1.2.3.4.5").setValue(identifierValue);
        organization.addIdentifier(identifier);

        // Set contact information
        ContactPoint phoneContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(phone).setUse(ContactPoint.ContactPointUse.MOBILE);
        ContactPoint emailContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(email).setUse(ContactPoint.ContactPointUse.HOME);
        organization.addTelecom(phoneContact).addTelecom(emailContact);

        return organization;
    }

    public static Location createLocation(String identifierValue, String name, String description, String phone,
                                          String email, String city, String postalCode, String country, Organization organization) {
        Location location = new Location();

        //set location demographics
        location.setName(name);
        location.setDescription(description);

        Address address = new Address();
        address.setCity(city);
        address.setPostalCode(postalCode);
        address.setCountry(country);
        location.setAddress(address);

        // Set identifier
        Identifier identifier = new Identifier().setSystem("urn:oid:1.2.3.4.5").setValue(identifierValue);
        location.addIdentifier(identifier);

        // Set contact information
        ContactPoint phoneContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(phone).setUse(ContactPoint.ContactPointUse.MOBILE);
        ContactPoint emailContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(email).setUse(ContactPoint.ContactPointUse.HOME);
        location.addTelecom(phoneContact).addTelecom(emailContact);

        //set organization
        location.setManagingOrganization(new Reference(organization));

        return location;
    }
}
