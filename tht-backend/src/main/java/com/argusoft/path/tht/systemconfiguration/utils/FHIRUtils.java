/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.utils;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final Logger LOGGER = LoggerFactory.getLogger(FHIRUtils.class);

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

    public static Location createAmbulance(String name, String description, String phone, Location.LocationStatus status) {

        Location ambulance = new Location();
        ambulance.setName(name);
        ambulance.setDescription(description);
        ambulance.setMode(Location.LocationMode.KIND);
        ambulance.setStatus(status);

        // Set this location as Ambulance
        ambulance.addType().addCoding().setCode("AMB").setSystem("http://terminology.hl7.org/CodeSystem/v3-RoleCode").setDisplay("Ambulance");

        // Set contact information
        ambulance.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(phone).setUse(ContactPoint.ContactPointUse.MOBILE);

        return ambulance;
    }

    public static Organization createOrganization(String name, String country, String city, String phone) {

        Organization organization = new Organization();
        organization.setName(name);

        // Set organization's address
        organization.addAddress().setCountry(country).setCity(city);

        // Set organization's contact information
        organization.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(phone).setUse(ContactPoint.ContactPointUse.MOBILE);

        return organization;
    }

    public static Date parseDate(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error("caught ParseException in FHIRUtils ", e);
            throw new RuntimeException("Error parsing date", e);
        }
    }

    public static Practitioner createPractitioner(String name, String gender, String birthDate, String identifierValue, String phone){
        Practitioner practitioner = new Practitioner();

        // set Practitioner demographics
        practitioner.addName().addGiven(name);
        practitioner.setGender(gender.equals("M") ? Enumerations.AdministrativeGender.MALE : Enumerations.AdministrativeGender.FEMALE);
        practitioner.setBirthDate(parseDate(birthDate));

        // Set contact information
        ContactPoint phoneContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(phone).setUse(ContactPoint.ContactPointUse.MOBILE);
        practitioner.addTelecom(phoneContact);

        // set identifier
        Identifier identifier = new Identifier().setSystem("urn:oid:1.2.3.4.5.6").setValue(identifierValue);
        practitioner.addIdentifier(identifier);

        return practitioner;
    }

    public static PractitionerRole createPractitionerRole(String identifierValue, String displaySpecialty, String contact, Practitioner practitioner, Location location, HealthcareService careService) {
        PractitionerRole practitionerRole = new PractitionerRole();

        // set Practitioner demographics
        practitionerRole.setPractitioner(new Reference(practitioner));
        practitionerRole.getLocation().add(new Reference(location));
//        practitionerRole.getHealthcareService().add(new Reference(careService));
        practitionerRole.addHealthcareService(new Reference(careService));
        practitionerRole.getSpecialtyFirstRep().addCoding()
                .setSystem("http://hl7.org/fhir/sid/us-npi")
                .setCode("207QS0010X")
                .setDisplay(displaySpecialty);


        // Set contact information
        ContactPoint phoneContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(contact).setUse(ContactPoint.ContactPointUse.MOBILE);
        practitionerRole.addTelecom(phoneContact);

        // set identifier
        Identifier identifier = new Identifier().setSystem("urn:oid:1.2.3.4.5").setValue(identifierValue);
        practitionerRole.addIdentifier(identifier);

        return practitionerRole;
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
