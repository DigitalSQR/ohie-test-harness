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
import java.util.*;

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


    public static ConceptMap createConceptMap(String name, String url, String status, String sourceUri, String targetUri, String sourceCode, String targetCode, String sourceDisplay, String targetDisplay){
        ConceptMap conceptMap = new ConceptMap();

        // Set ConceptMap metadata
        conceptMap.setName(name);
        conceptMap.setUrl(url);
        conceptMap.setStatus(Enumerations.PublicationStatus.valueOf(status));

        // Set source and target code systems
        conceptMap.setSource(new UriType(sourceUri));
        conceptMap.setTarget(new UriType(targetUri));

        // Define a mapping between source and target codes
        ConceptMap.ConceptMapGroupComponent group = new ConceptMap.ConceptMapGroupComponent();
        group.setSource(sourceUri);
        group.setTarget(targetUri);

        ConceptMap.SourceElementComponent sourceElement = new ConceptMap.SourceElementComponent();
        sourceElement.setCode(sourceCode);
        sourceElement.setDisplay(sourceDisplay);
        ConceptMap.TargetElementComponent target = sourceElement.addTarget();
        target.setCode(targetCode);
                target.setEquivalence(Enumerations.ConceptMapEquivalence.EQUAL);
                target.setDisplay(targetDisplay);


        group.addElement(sourceElement);
        conceptMap.setGroup(Collections.singletonList(group));

        return conceptMap;
    }

    public static Practitioner createPractitioner(
            String familyName,
            String givenName,
            String prefix,
            String gender,
            String birthDate,
            String identifierSystem,
            String identifierValue,
            boolean active,
            String phone,
            String email
    ){
        // Creating a new Practitioner resource
        Practitioner practitioner = new Practitioner();

        // Set practitioner identifier
        Identifier identifier = practitioner.addIdentifier();
        identifier.setSystem(identifierSystem)
                .setValue(identifierValue);

        // Set practitioner name
        HumanName name = practitioner.addName();
        name.addGiven(givenName).setFamily(familyName);
        name.addPrefix(prefix);

        // Set practitioner gender
        if (gender != null) {
            practitioner.setGender(Enumerations.AdministrativeGender.valueOf(gender.toUpperCase()));
        }

        // Set practitioner birth date
        if (birthDate != null) {
            practitioner.setBirthDateElement(new DateType(birthDate));
        }

        // Set practitioner active status
        practitioner.setActive(active);

        // Set contact information
        ContactPoint phoneContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(phone).setUse(ContactPoint.ContactPointUse.MOBILE);
        ContactPoint emailContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(email).setUse(ContactPoint.ContactPointUse.HOME);
        practitioner.addTelecom(phoneContact).addTelecom(emailContact);


        return practitioner;
    }

    public static PractitionerRole createPractitionerRole(
            String id,
            Practitioner practitionerRef,
            String codingSystem,
            String role,
            boolean active,
            Location locationRef,
            HealthcareService healthcareServiceRef,
//            Set<String> daysOfWeek,
            String availableStartTime,
            String availableEndTime
    ){
        // Creating new PractitionerRole resource
        PractitionerRole practitionerRole = new PractitionerRole();

        // Set id
        practitionerRole.setId(id);

        // Set practitioner reference
        practitionerRole.setPractitioner(new Reference(practitionerRef));

        // Set practitioner role code
        List<CodeableConcept> codeableConceptList = new ArrayList<>();
        CodeableConcept codeableConcept = new CodeableConcept()
                .addCoding(new Coding().setSystem(codingSystem).setCode(role));

        codeableConceptList.add(codeableConcept);

        practitionerRole.setCode(codeableConceptList);

        // Set practitioner active status
        practitionerRole.setActive(active);

        //Set Location reference
        practitionerRole.addLocation(new Reference(locationRef));

        //Set HealthcareService reference
        practitionerRole.addHealthcareService(new Reference(healthcareServiceRef));


        practitionerRole.addAvailableTime()
                .setAvailableStartTime(String.valueOf(new TimeType(availableStartTime)))
                .setAvailableEndTime(String.valueOf(new TimeType(availableEndTime)));

        return practitionerRole;
    }

    public static HealthcareService createHealthcareService(
            String id,
            String categorySystem,
            String categoryCode,
            String categoryDisplay,
            String typeSystem,
            String typeCode,
            String typeDisplay,
            String serviceName,
            boolean active,
            Location location

    ){

        // Create a HealthcareService resource
        HealthcareService healthcareService = new HealthcareService();

        //set id
        healthcareService.setId(id);
        // Set the service category
        healthcareService.addCategory(new CodeableConcept().addCoding(
                new Coding().setSystem(categorySystem).setCode(categoryCode).setDisplay(categoryDisplay)
        ));

        // Set the service type
        healthcareService.addType(new CodeableConcept().addCoding(
                new Coding().setSystem(typeSystem).setCode(typeCode).setDisplay(typeDisplay)
        ));

        // Set the location reference
        healthcareService.getLocation().add(new Reference(location));

        // Set the service name
        healthcareService.setName(serviceName);

        // Set service status
        healthcareService.setActive(active);

        return healthcareService;
    }

    public static CodeSystem createCodeSystem(String url,String version,String name,String title,String status,String publisher,String content,String code,String display,String definition) {

        // create codeSystem object
        CodeSystem codeSystem = new CodeSystem() ;

        // add data in codeSystem
        codeSystem.setUrl(url);
        codeSystem.setName(name);
        codeSystem.setVersion(version);
        codeSystem.setStatus(Enumerations.PublicationStatus.valueOf(status));
        codeSystem.setTitle(title);
        codeSystem.setPublisher(publisher);
        codeSystem.setContent(CodeSystem.CodeSystemContentMode.valueOf(content));

        // Add a concept with a code
        CodeSystem.ConceptDefinitionComponent concept=codeSystem.addConcept();
        concept.setCode(code);
        concept.setDisplay(display);
        concept.setDefinition(definition);

        return codeSystem;
    }

    public static ValueSet createValueSet(String url,String name,String title,String status,String publisherName){

        ValueSet valueSet=new ValueSet();
        valueSet.setUrl(url);
        valueSet.setName(name);
        valueSet.setTitle(title);
        valueSet.setStatus(Enumerations.PublicationStatus.valueOf(status));
        valueSet.setPublisher(publisherName);

        return valueSet;
    }

    public static void addConceptValueSet(ValueSet valueSet,String codeSystemUrl,String code,String display){
        // creating compose in ValueSet
        ValueSet.ValueSetComposeComponent compose = new ValueSet.ValueSetComposeComponent();

        // include a system and concept
        ValueSet.ConceptSetComponent conceptSet = compose.addInclude();
        conceptSet.setSystem(codeSystemUrl);
        ValueSet.ConceptReferenceComponent concept= conceptSet.addConcept();
        concept.setCode(code);
        concept.setDisplay(display);

        // Add the composed component in the valueSet
        valueSet.setCompose(compose);
    }

}

    public static Location createLocation(
            String id,
            String name,
            String street,
            String city,
            String state,
            String postalCode,
            String country,
            String phoneNo,
            double latitude,
            double longitude
    ){
        // Create a Location reference
        Location location=new Location();

        // Set Location name
        location.setName(name);

        // set id
        location.setId(id);

        //Set address
        location.setAddress(
                new Address()
                        .addLine(street)
                        .setCity(city)
                        .setPostalCode(postalCode)
                        .setState(state)
                        .setCountry(country));

        // Set telecom
        location.addTelecom(new ContactPoint()
                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                .setValue(phoneNo)
        );

        // Set position
        location.setPosition(new Location.LocationPositionComponent()
                .setLatitude(latitude)
                .setLongitude(longitude)
        );

        return location;
    }

    public static ValueSet createValueSet(String url,String name,String title,String status,String publisherName){

        ValueSet valueSet=new ValueSet();
        valueSet.setUrl(url);
        valueSet.setName(name);
        valueSet.setTitle(title);
        valueSet.setStatus(Enumerations.PublicationStatus.valueOf(status));
        valueSet.setPublisher(publisherName);

        return valueSet;
    }

    public static void addConceptValueSet(ValueSet valueSet,String codeSystemUrl,String code,String display){
        // creating compose in ValueSet
        ValueSet.ValueSetComposeComponent compose = new ValueSet.ValueSetComposeComponent();

        // include a system and concept
        ValueSet.ConceptSetComponent conceptSet = compose.addInclude();
        conceptSet.setSystem(codeSystemUrl);
        ValueSet.ConceptReferenceComponent concept= conceptSet.addConcept();
        concept.setCode(code);
        concept.setDisplay(display);

        // Add the composed component in the valueSet
        valueSet.setCompose(compose);
    }

}


