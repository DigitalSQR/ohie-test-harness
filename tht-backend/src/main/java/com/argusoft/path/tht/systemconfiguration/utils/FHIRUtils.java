package com.argusoft.path.tht.systemconfiguration.utils;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
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

    private FHIRUtils() {
    }

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

    public static Patient createPatient(
            String familyName,
            String givenName,
            String gender,
            String birthDate,
            String identifierSystem,
            String phone,
            String email) {
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

        // Setting patient active status
        patient.setActive(true);

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
            LOGGER.error(ValidateConstant.PARSE_EXCEPTION + FHIRUtils.class.getSimpleName(), e);
            throw new RuntimeException("Error parsing date", e);
        }
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

    public static Practitioner createPractitioner(String name, String gender, String birthDate, String identifierValue, String phone) {
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
// practitionerRole.getHealthcareService().add(new Reference(careService));
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

    public static ConceptMap createConceptMap(String name, String url, String status, String sourceUri, String targetUri, String sourceCode, String targetCode, String sourceDisplay, String targetDisplay) {
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
    ) {
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

    public static ValueSet createValueSet(String url, String name, String title, String status) {
        ValueSet valueSet = new ValueSet();
        valueSet.setUrl(url);
        valueSet.setName(name);
        valueSet.setTitle(title);
        valueSet.setStatus(Enumerations.PublicationStatus.valueOf(status));
        return valueSet;
    }

    public static Encounter createEncounter(String patientId, List<String> practitionerIds, String encounterTypeCode, String encounterDate) {
        Encounter encounter = new Encounter();

        // Set the patient reference
        encounter.setSubject(new Reference("Patient/" + patientId));

        for (String practitionerId : practitionerIds) {
            encounter.addParticipant().setIndividual(new Reference("Practitioner/" + practitionerId));
        }
        // Set the practitioner reference

        // Set the encounter class (e.g., outpatient)
        encounter.setClass_(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode").setCode(encounterTypeCode));

        // Set the encounter period
        encounter.setPeriod(new Period().setStart(parseDate(encounterDate)));

        return encounter;
    }

    public static Encounter createEncounter(String patientId, String practitionerId, String encounterTypeCode, String encounterDate) {
        Encounter encounter = new Encounter();

        // Set the patient reference
        encounter.setSubject(new Reference("Patient/" + patientId));

        // Set the practitioner reference
        encounter.addParticipant().setIndividual(new Reference("Practitioner/" + practitionerId));

        // Set the encounter class (e.g., outpatient)
        encounter.setClass_(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode").setCode(encounterTypeCode));

        // Set the encounter period
        encounter.setPeriod(new Period().setStart(parseDate(encounterDate)));

        return encounter;
    }

    public static DocumentReference createDocumentReference(String patientId, String practitionerId, String attachmentURL, String attachmentTitle) {
        String base64CdaContent = "PGNsaW5pY2lkYXRvcz4KICAgIDx0aXRsZT5TYW1wbGUgQ0RBIERvY3VtZW50PC90aXRsZT4KICAgIDxwYXRpZW50PjxuYW1lPkpvaG4gRG9lPC9uYW1lPjxkYXRlYmFzZT4xOTgwMDEwMTwvZGF0ZWJhc2U+CiAgICA8L3BhdGllbnQ+CjwvQ2xpY2lubmFtZURvY3VtZW50PjwvQ2xpY2lubmFtZWRhdG9ucz4=";
        DocumentReference documentReference = new DocumentReference();
        documentReference.setStatus(Enumerations.DocumentReferenceStatus.CURRENT);
        documentReference.setId("document123");
        // Set the subject (patient)
        Reference subjectReference = new Reference("Patient/" + patientId);
        documentReference.setSubject(subjectReference);

        // Set the content (attachment details)
        DocumentReference.DocumentReferenceContentComponent content = new DocumentReference.DocumentReferenceContentComponent();
        Attachment attachment = new Attachment();

// Set content type (e.g., "application/xml" for CDA documents)
        attachment.setContentType("application/xml");

// Base64-decode the CDA document content and set it
        byte[] decodedContent = Base64.getDecoder().decode(base64CdaContent);
        attachment.setData(decodedContent);

// Optionally set URL or title if applicable
        attachment.setUrl(attachmentURL);
        attachment.setTitle(attachmentTitle);

// Set the attachment in the content
        content.setAttachment(attachment);

// Add content to the DocumentReference
        documentReference.addContent(content);
        // Set the type (LOINC code for Clinical Note)
        CodeableConcept typeCodeableConcept = new CodeableConcept();
        Coding typeCoding = new Coding();
        typeCoding.setSystem("http://loinc.org");
        typeCoding.setCode("60591-5");
        typeCoding.setDisplay("Clinical Note");
        typeCodeableConcept.addCoding(typeCoding);
        typeCodeableConcept.setText("Clinical Note");
        documentReference.setType(typeCodeableConcept);

        // Set the author (practitioner reference)
        Reference authorReference = new Reference("Practitioner/" + practitionerId);
        documentReference.addAuthor(authorReference);

        return documentReference;

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
    ) {
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
    ) {

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

    public static CodeSystem createCodeSystem(String url, String version, String name, String title, String status, String publisher, String content, String code, String display, String definition) {

        // create codeSystem object
        CodeSystem codeSystem = new CodeSystem();

        // add data in codeSystem
        codeSystem.setUrl(url);
        codeSystem.setName(name);
        codeSystem.setVersion(version);
        codeSystem.setStatus(Enumerations.PublicationStatus.valueOf(status));
        codeSystem.setTitle(title);
        codeSystem.setPublisher(publisher);
        codeSystem.setContent(CodeSystem.CodeSystemContentMode.valueOf(content));

        // Add a concept with a code
        CodeSystem.ConceptDefinitionComponent concept = codeSystem.addConcept();
        concept.setCode(code);
        concept.setDisplay(display);
        concept.setDefinition(definition);

        return codeSystem;
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
    ) {
        // Create a Location reference
        Location location = new Location();

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

    public static ValueSet createValueSet(String url, String name, String title, String status, String publisherName) {

        ValueSet valueSet = new ValueSet();
        valueSet.setUrl(url);
        valueSet.setName(name);
        valueSet.setTitle(title);
        valueSet.setStatus(Enumerations.PublicationStatus.valueOf(status));
        valueSet.setPublisher(publisherName);

        return valueSet;
    }

    public static void addConceptValueSet(ValueSet valueSet, String codeSystemUrl, String code, String display) {
        // creating compose in ValueSet
        ValueSet.ValueSetComposeComponent compose = new ValueSet.ValueSetComposeComponent();

        // include a system and concept
        ValueSet.ConceptSetComponent conceptSet = compose.addInclude();
        conceptSet.setSystem(codeSystemUrl);
        ValueSet.ConceptReferenceComponent concept = conceptSet.addConcept();
        concept.setCode(code);
        concept.setDisplay(display);

        // Add the composed component in the valueSet
        valueSet.setCompose(compose);
    }

    public static DiagnosticReport createDiagnosticReport(String patientId, String practitionerId, String observationId) {
        Reference patientReference = new Reference("Patient/" + patientId);

        // Create Practitioner reference
        Reference practitionerReference = new Reference("Practitioner/" + practitionerId);

        Reference observationReference = new Reference("Observation/" + observationId);
        // Create DiagnosticReport
        DiagnosticReport diagnosticReport = new DiagnosticReport();
        diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        diagnosticReport.setSubject(patientReference);
        diagnosticReport.setPerformer(List.of(practitionerReference));
        diagnosticReport.addResult(observationReference);

        return diagnosticReport;
    }

    public static Observation createObservation(String patientId, String practitionerId, String code, double value) {
        // Create Patient reference
        Reference patientReference = new Reference("Patient/" + patientId);
        Reference practitionerReference = new Reference("Practitioner/" + practitionerId);

        // Create Observation
        Observation observation = new Observation();
        observation.setStatus(Observation.ObservationStatus.FINAL);
        observation.addPerformer(practitionerReference);

        // Set code and value for the observation
        observation.getCode().addCoding()
                .setSystem("http://loinc.org")
                .setCode(code)
                .setDisplay("Observation Display Name");

        observation.setValue(new Quantity().setValue(value).setUnit("Unit"));

        // Set additional details
        observation.setEffective(new DateTimeType());
        observation.setIssuedElement(new InstantType());

        // Set reference range (normal range for the observation)
        observation.getReferenceRangeFirstRep().setLow(new SimpleQuantity().setValue(10.0).setUnit("Unit"));
        observation.getReferenceRangeFirstRep().setHigh(new SimpleQuantity().setValue(20.0).setUnit("Unit"));

        // Set the method used to produce the observation
        observation.getMethod().addCoding().setSystem("http://snomed.info/sct").setCode("123456");

        // Set interpretation of the observation result
        observation.getMethod().addCoding().setSystem("http://snomed.info/sct").setCode("789012");
        CodeableConcept interpretation = new CodeableConcept();
        interpretation.addCoding().setSystem("http://snomed.info/sct").setCode("789012");
        observation.setInterpretation(List.of(interpretation));

        return observation;
    }

    public static Practitioner createPractitioner(String name, String gender, String birthDate, String qualificationCode, String qualificationDisplay, String phone, String email, String addressLine, String city, String state, String postalCode) {
        Practitioner practitioner = new Practitioner();
        practitioner.addName().addGiven(name);
        practitioner.setGender(gender.equals("M") ? Enumerations.AdministrativeGender.MALE : Enumerations.AdministrativeGender.FEMALE);
        practitioner.setBirthDate(parseDate(birthDate));
        practitioner.addQualification().getCode().addCoding().setCode(qualificationCode).setSystem("http://example.org/codes").setDisplay(qualificationDisplay);

        ContactPoint phoneContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(phone).setUse(ContactPoint.ContactPointUse.MOBILE);
        ContactPoint emailContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(email).setUse(ContactPoint.ContactPointUse.HOME);
        practitioner.addTelecom(phoneContact).addTelecom(emailContact);

        Address address = new Address();
        address.addLine(addressLine);
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(postalCode);

        practitioner.addAddress(address);
        return practitioner;
    }

    public static PractitionerRole createPractitionerRole(String role, String code, String practitionerId, String organizationId) {
        PractitionerRole practitionerRole = new PractitionerRole();
        practitionerRole.getOrganization().setReference(organizationId);
        practitionerRole.getPractitioner().setReference(practitionerId);
        practitionerRole.addCode().addCoding().setCode(code).setSystem("http://example.org/codes").setDisplay(role);
        return practitionerRole;
    }

    public static Condition createCondition(String patientId, String encounterId, String disease, String code) {
        Condition condition = new Condition();
        condition.getSubject().setReference(patientId);
        condition.getEncounter().setReference(encounterId);
        condition.getCode().addCoding().setCode(code).setSystem("http://example.org/codes").setDisplay(disease);
        return condition;
    }

    public static Encounter createEncounter(String patientId, String practitionerId, String code) {
        Encounter encounter = new Encounter();

        encounter.setStatus(Encounter.EncounterStatus.INPROGRESS);  // Set the status of the encounter
        encounter.getClass_().setSystem("http://hl7.org/fhir/v3/ActCode").setCode(code);  // Set the class of the encounter
        encounter.getSubject().setReference(patientId);  // Reference to the patient
        encounter.getPeriod().setStart(new Date());
        encounter.addParticipant().getIndividual().setReference(practitionerId);
        return encounter;
    }

    public static Medication createMedication(String code, String name, String productType) {
        Medication medication = new Medication();
        medication.getCode().addCoding().setCode(code).setSystem("http://example.org/codes").setDisplay(name);
        medication.getForm().setText(productType);
        return medication;
    }

    public static MedicationRequest createMedicationRequest(String medicationId, String patientId) {
        MedicationRequest medicationRequest = new MedicationRequest();
        medicationRequest.getMedicationReference().setId(medicationId);
        medicationRequest.getSubject().setReference(patientId);
        return medicationRequest;
    }

    public static MedicationAdministration createMedicationAdministration(String patientId, String practitionerId, String medicationId, String encounterId) {
        MedicationAdministration medicationAdministration = new MedicationAdministration();

        // Set the patient reference
        medicationAdministration.getSubject().setReference(patientId);

        // Set the practitioner reference
        medicationAdministration.addPerformer().getActor().setReference(practitionerId);

        // Set the medication reference
        medicationAdministration.getMedicationReference().setId(medicationId);

        // Set the encounter reference
        medicationAdministration.getContext().setReference(encounterId);

        return medicationAdministration;
    }

    public static Procedure createProcedure(String patientId, String code, String procedureName) {
        Procedure procedure = new Procedure();
        procedure.getSubject().setReference(patientId);
        procedure.getCode().addCoding().setCode(code).setSystem("http://example.org/codes").setDisplay(procedureName);

        // Set the status of the procedure (e.g., completed)
        procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);

        return procedure;
    }

    public static Observation createObservation(String patientId, String code, String observationType, Double quantity, String unit) {
        Observation observation = new Observation();

        observation.getSubject().setReference(patientId);
        observation.getCode().addCoding().setCode(code).setSystem("http://loinc.org").setDisplay(observationType);

        Quantity valueQuantity = new Quantity();
        valueQuantity.setValue(quantity).setUnit(unit).setSystem("http://unitsofmeasure.org");
        observation.setValue(valueQuantity);
        // Set the status of the observation (e.g., final)
        observation.setStatus(Observation.ObservationStatus.FINAL);

        return observation;
    }

    public static AllergyIntolerance createAllergyIntolerance(String patientId, String code, String allergyFrom) {
        AllergyIntolerance allergyIntolerance = new AllergyIntolerance();
        allergyIntolerance.getPatient().setReference(patientId);  // Reference to the patient

        CodeableConcept substance = new CodeableConcept();
        substance.addCoding().setCode(code).setSystem("http://snomed.info/sct").setDisplay(allergyFrom);
        allergyIntolerance.addReaction().setSubstance(substance);

        // Set the type of allergy or intolerance (e.g., allergy)
        allergyIntolerance.setType(AllergyIntolerance.AllergyIntoleranceType.ALLERGY);

        CodeableConcept verificationAndClinicalStatus = new CodeableConcept();
        verificationAndClinicalStatus.addCoding().setCode("confirmed").setSystem("http://hl7.org/fhir/v3/ActStatus").setDisplay("Confirmed");

        // Set the clinical status of the allergy (e.g., confirmed)
        allergyIntolerance.setClinicalStatus(verificationAndClinicalStatus);

        // Set the verification status of the allergy (e.g., confirmed)
        allergyIntolerance.setVerificationStatus(verificationAndClinicalStatus);

        return allergyIntolerance;
    }

    public static Immunization createImmunization(String patientId, String code, String vaccineName) {
        Immunization immunization = new Immunization();
        immunization.getPatient().setReference(patientId);

        // Set the vaccine code
        CodeableConcept vaccineCode = new CodeableConcept();
        vaccineCode.addCoding().setCode(code).setSystem("http://www.nlm.nih.gov/research/umls/rxnorm").setDisplay(vaccineName);
        immunization.setVaccineCode(vaccineCode);

        // Set the date of immunization
        immunization.addReaction().setDate(new Date());

        // Set the status of the immunization
        immunization.setStatus(Immunization.ImmunizationStatus.COMPLETED);

        return immunization;
    }

    public static DiagnosticReport createDiagnosticReportWithCode(String patientId, String code, String description) {
        DiagnosticReport diagnosticReport = new DiagnosticReport();

        diagnosticReport.getSubject().setReference(patientId);  // Reference to the patient

        // Set the code for the diagnostic report
        CodeableConcept diagnosis = new CodeableConcept();
        diagnosis.addCoding().setCode(code).setSystem("http://loinc.org").setDisplay(description);
        diagnosticReport.setCode(diagnosis);

        // Set the status of the diagnostic report
        diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);

        return diagnosticReport;
    }

    public static Composition createComposition(String title, String patientId, String organizationId, String OrganizationName) {

        Composition composition = new Composition();

        // Set the title of the composition
        composition.setTitle(title);

        // Set the status of the composition (e.g., FINAL)
        composition.setStatus(Composition.CompositionStatus.FINAL);

        // Set the date of the composition (e.g., current date/time)
        composition.setDate(new Date());

        // Set the subject of the composition (reference to the patient)
        composition.setSubject(new Reference("Patient/" + patientId));

        Reference custodian = new Reference("Organization/" + organizationId);
        custodian.setDisplay(OrganizationName);
        composition.setCustodian(custodian);

        // Section - History of present illness
        Composition.SectionComponent section1 = new Composition.SectionComponent();
        section1.setTitle("History of present illness");
        CodeableConcept code1 = new CodeableConcept();
        code1.addCoding(new Coding("http://loinc.org", "11348-0", "History of past illness Narrative"));
        section1.setCode(code1);
        composition.addSection(section1);

        // Section - History of family member diseases
        Composition.SectionComponent section2 = new Composition.SectionComponent();
        section2.setTitle("History of family member diseases");
        CodeableConcept code2 = new CodeableConcept();
        code2.addCoding(new Coding("http://loinc.org", "10157-6", "History of family member diseases Narrative"));
        section2.setCode(code2);
        Narrative text2 = new Narrative();
        text2.setStatus(Narrative.NarrativeStatus.GENERATED);
        text2.setDivAsString("<div xmlns=\"http://www.w3.org/1999/xhtml\">\n\t\t\t\t<p>History of family member diseases - not available</p>\n\t\t\t</div>");
        section2.setText(text2);
        CodeableConcept emptyReason = new CodeableConcept();
        emptyReason.addCoding(new Coding("http://terminology.hl7.org/CodeSystem/list-empty-reason", "withheld", "Information Withheld"));
        section2.setEmptyReason(emptyReason);
        composition.addSection(section2);

        return composition;

    }

    public static Composition createAdmissionNote(String patientId, String organizationId, String practitionerId, String OrganizationName) {
        Composition composition = new Composition();

        // Set type
        CodeableConcept type = new CodeableConcept().addCoding(new Coding().setSystem("http://loinc.org").setCode("11488-4").setDisplay("Admission note"));
        composition.setType(type);

        // Set status
        composition.setStatus(Composition.CompositionStatus.FINAL);

        // Set date
        composition.setDate(new Date());

        // Set title
        composition.setTitle("Admission Note");

        // Set subject (replace with actual patient reference)
        composition.setSubject(new Reference("Patient/" + patientId));

        // Set author (replace with actual author reference)
        composition.addAuthor(new Reference("Practitioner/" + practitionerId));

        Reference custodian = new Reference("Organization/" + organizationId);
        custodian.setDisplay(OrganizationName);
        composition.setCustodian(custodian);

        // Add sections (example: presenting complaints, medical history, initial assessments, treatment plans)
        Composition.SectionComponent section = new Composition.SectionComponent();
        section.setTitle("Presenting Complaints");
        section.setCode(new CodeableConcept().setText("Presenting Complaints"));
        Narrative text = new Narrative();
        text.setStatus(Narrative.NarrativeStatus.GENERATED);
        text.setDivAsString("Presented to the emergency department with severe abdominal pain");
        section.setText(text);
        composition.addSection(section);
        return composition;
    }

    // Method to create Operative Note Composition
    public static Composition createOperativeNote(String patientId, String organizationId, String practitionerId, String OrganizationName) {
        Composition composition = new Composition();

        // Set type
        CodeableConcept type = new CodeableConcept().addCoding(new Coding().setSystem("http://loinc.org").setCode("28573-5").setDisplay("Operative note"));
        composition.setType(type);

        // Set status
        composition.setStatus(Composition.CompositionStatus.FINAL);

        // Set date
        composition.setDate(new Date());

        // Set title
        composition.setTitle("Operative Note");

        // Set subject (replace with actual patient reference)
        composition.setSubject(new Reference("Patient/" + patientId));

        // Set author (replace with actual author reference)
        composition.addAuthor(new Reference("Practitioner/" + practitionerId));

        Reference custodian = new Reference("Organization/" + organizationId);
        custodian.setDisplay(OrganizationName);
        composition.setCustodian(custodian);

        Composition.SectionComponent section = new Composition.SectionComponent();
        section.setTitle("Preoperative Assessments");
        section.setCode(new CodeableConcept().setText("Preoperative Assessments"));
        Narrative text = new Narrative();
        text.setStatus(Narrative.NarrativeStatus.GENERATED);
        text.setDivAsString("The patient underwent preoperative evaluation");
        section.setText(text);
        composition.addSection(section);
        return composition;
    }

    // Method to create Progress Notes Composition
    public static Composition createProgressNotes(String patientId, String organizationId, String practitionerId, String OrganizationName) {
        Composition composition = new Composition();

        // Set type
        CodeableConcept type = new CodeableConcept().addCoding(new Coding().setSystem("http://loinc.org").setCode("11506-3").setDisplay("Progress note"));
        composition.setType(type);

        // Set status
        composition.setStatus(Composition.CompositionStatus.FINAL);

        // Set date
        composition.setDate(new Date());

        // Set title
        composition.setTitle("Progress Note");

        // Set subject (replace with actual patient reference)
        composition.setSubject(new Reference("Patient/" + patientId));

        // Set author (replace with actual author reference)
        composition.addAuthor(new Reference("Practitioner/" + practitionerId));

        Reference custodian = new Reference("Organization/" + organizationId);
        custodian.setDisplay(OrganizationName);
        composition.setCustodian(custodian);

        Composition.SectionComponent section = new Composition.SectionComponent();
        section.setTitle("Daily Progress Notes");
        section.setCode(new CodeableConcept().setText("Daily Progress Notes"));
        Narrative text = new Narrative();
        text.setStatus(Narrative.NarrativeStatus.GENERATED);
        text.setDivAsString("On day 2 of hospitalization, the patient's condition remained stable");
        section.setText(text);
        composition.addSection(section);
        return composition;
    }

    // Method to create Consultation Reports Composition
    public static Composition createConsultationReports(String patientId, String organizationId, String practitionerId, String OrganizationName) {
        Composition composition = new Composition();

        // Set type
        CodeableConcept type = new CodeableConcept().addCoding(new Coding().setSystem("http://loinc.org").setCode("57133-1").setDisplay("Consultation note"));
        composition.setType(type);

        // Set status
        composition.setStatus(Composition.CompositionStatus.FINAL);

        // Set date
        composition.setDate(new Date());

        // Set title
        composition.setTitle("Consultation Report");

        // Set subject (replace with actual patient reference)
        composition.setSubject(new Reference("Patient/" + patientId));

        // Set author (replace with actual author reference)
        composition.addAuthor(new Reference("Practitioner/" + practitionerId));

        Reference custodian = new Reference("Organization/" + organizationId);
        custodian.setDisplay(OrganizationName);
        composition.setCustodian(custodian);

        Composition.SectionComponent section = new Composition.SectionComponent();
        section.setTitle("Findings");
        section.setCode(new CodeableConcept().setText("Findings"));
        Narrative text = new Narrative();
        text.setStatus(Narrative.NarrativeStatus.GENERATED);
        text.setDivAsString("The patient was referred for cardiology consultation");
        section.setText(text);
        composition.addSection(section);
        return composition;
    }

    public static DocumentReference createDocumentReference(String documentReferenceId, String patientId, String practitionerId, String attachmentURL, String attachmentTitle) {
        String base64CdaContent = "PGNsaW5pY2lkYXRvcz4KICAgIDx0aXRsZT5TYW1wbGUgQ0RBIERvY3VtZW50PC90aXRsZT4KICAgIDxwYXRpZW50PjxuYW1lPkpvaG4gRG9lPC9uYW1lPjxkYXRlYmFzZT4xOTgwMDEwMTwvZGF0ZWJhc2U+CiAgICA8L3BhdGllbnQ+CjwvQ2xpY2lubmFtZURvY3VtZW50PjwvQ2xpY2lubmFtZWRhdG9ucz4=";
        DocumentReference documentReference = new DocumentReference();
        documentReference.setId(documentReferenceId);
        documentReference.setStatus(Enumerations.DocumentReferenceStatus.CURRENT);

        // Set the subject (patient)
        Reference subjectReference = new Reference("Patient/" + patientId);
        documentReference.setSubject(subjectReference);

        // Set the content (attachment details)
        DocumentReference.DocumentReferenceContentComponent content = new DocumentReference.DocumentReferenceContentComponent();
        Attachment attachment = new Attachment();

// Set content type (e.g., "application/xml" for CDA documents)
        attachment.setContentType("application/xml");

// Base64-decode the CDA document content and set it
        byte[] decodedContent = Base64.getDecoder().decode(base64CdaContent);
        attachment.setData(decodedContent);

// Optionally set URL or title if applicable
        attachment.setUrl(attachmentURL);
        attachment.setTitle(attachmentTitle);

// Set the attachment in the content
        content.setAttachment(attachment);

// Add content to the DocumentReference
        documentReference.addContent(content);
        // Set the type (LOINC code for Clinical Note)
        CodeableConcept typeCodeableConcept = new CodeableConcept();
        Coding typeCoding = new Coding();
        typeCoding.setSystem("http://loinc.org");
        typeCoding.setCode("60591-5");
        typeCoding.setDisplay("Clinical Note");
        typeCodeableConcept.addCoding(typeCoding);
        typeCodeableConcept.setText("Clinical Note");
        documentReference.setType(typeCodeableConcept);

        // Set the author (practitioner reference)
        Reference authorReference = new Reference("Practitioner/" + practitionerId);
        documentReference.addAuthor(authorReference);

        return documentReference;

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

    // Method to create Discharge Summary Composition
    public static Composition createDischargeSummary(String patientId, String organizationId, String practitionerId, String OrganizationName) {
        Composition composition = new Composition();

        // Set type
        CodeableConcept type = new CodeableConcept().addCoding(new Coding().setSystem("http://loinc.org").setCode("18842-5").setDisplay("Discharge summary"));
        composition.setType(type);

        // Set status
        composition.setStatus(Composition.CompositionStatus.FINAL);

        // Set date
        composition.setDate(new Date());

        // Set title
        composition.setTitle("Discharge Summary");

        // Set subject (replace with actual patient reference)
        composition.setSubject(new Reference("Patient/" + patientId));

        // Set author (replace with actual author reference)
        composition.addAuthor(new Reference("Practitioner/" + practitionerId));

        Reference custodian = new Reference("Organization/" + organizationId);
        custodian.setDisplay(OrganizationName);
        composition.setCustodian(custodian);

        Composition.SectionComponent section = new Composition.SectionComponent();
        section.setTitle("Reason for Admission");
        section.setCode(new CodeableConcept().setText("Reason for Admission"));
        Narrative text = new Narrative();
        text.setStatus(Narrative.NarrativeStatus.GENERATED);
        text.setDivAsString("The patient was admitted for exacerbation of congestive heart failure");
        section.setText(text);
        composition.addSection(section);

        return composition;
    }
}
