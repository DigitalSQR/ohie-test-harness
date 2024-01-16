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
import java.util.Collections;
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


    public static Patient createPatient(String givenName, String familyName, String gender, String birthDate,
                                        String identifierValue, String phone, String email, String addressLine, String city, String state, String postalCode) {
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

        Address address = new Address();
        address.addLine(addressLine);
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(postalCode);

        patient.addAddress(address);

        return patient;
    }

    public static Practitioner createPractitioner(String name, String gender, String birthDate, String qualificationCode, String qualificationDisplay, String phone, String email, String addressLine, String city, String state, String postalCode){
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

    public static PractitionerRole createPractitionerRole(String role, String code, String practitionerId, String organizationId){
        PractitionerRole practitionerRole = new PractitionerRole();
        practitionerRole.getOrganization().setReference(organizationId);
        practitionerRole.getPractitioner().setReference(practitionerId);
        practitionerRole.addCode().addCoding().setCode(code).setSystem("http://example.org/codes").setDisplay(role);
        return practitionerRole;
    }

    public static Condition createCondition(String patientId, String encounterId, String disease, String code){
        Condition condition = new Condition();
        condition.getSubject().setReference(patientId);
        condition.getEncounter().setReference(encounterId);
        condition.getCode().addCoding().setCode(code).setSystem("http://example.org/codes").setDisplay(disease);
        return condition;
    }

    public static Encounter createEncounter(String patientId, String practitionerId, String code){
        Encounter encounter = new Encounter();

        encounter.setStatus(Encounter.EncounterStatus.INPROGRESS);  // Set the status of the encounter
        encounter.getClass_().setSystem("http://hl7.org/fhir/v3/ActCode").setCode(code);  // Set the class of the encounter
        encounter.getSubject().setReference(patientId);  // Reference to the patient
        encounter.getPeriod().setStart(new Date());
        encounter.addParticipant().getIndividual().setReference(practitionerId);
        return encounter;
    }

    public static Medication createMedication(String code, String name, String productType){
        Medication medication = new Medication();
        medication.getCode().addCoding().setCode(code).setSystem("http://example.org/codes").setDisplay(name);
        medication.getForm().setText(productType);
        return medication;
    }

    public static MedicationRequest createMedicationRequest(String medicationId, String patientId){
        MedicationRequest medicationRequest = new MedicationRequest();
        medicationRequest.getMedicationReference().setId(medicationId);
        medicationRequest.getSubject().setReference(patientId);
        return medicationRequest;
    }

    public static MedicationAdministration createMedicationAdministration(String patientId, String practitionerId, String medicationId, String encounterId){
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

    public static Procedure createProcedure(String patientId, String code, String procedureName){
        Procedure procedure = new Procedure();
        procedure.getSubject().setReference(patientId);
        procedure.getCode().addCoding().setCode(code).setSystem("http://example.org/codes").setDisplay(procedureName);

        // Set the status of the procedure (e.g., completed)
        procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);

        return procedure;
    }

    public static Observation createObservation(String patientId, String code, String observationType, Double quantity, String unit){
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

    public static AllergyIntolerance createAllergyIntolerance(String patientId, String code, String allergyFrom){
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

    public static Immunization createImmunization(String patientId, String code, String vaccineName){
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
    public static DiagnosticReport createDiagnosticReport(String patientId, String code, String description ){
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


    public static Organization createOrganization(String name, String organizationType, String addressLine, String city, String state, String country, String postalCode,
                                                  String phone, String email, String identifierValue, String ownedBy, String administrativeLevel, Boolean operationalStatus ){
        Organization organization = new Organization();

        organization.setName(name);

        //Set organization type
        CodeableConcept organizationTypeConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem("http://hl7.org/fhir/ValueSet/organization-type");
        coding.setCode(organizationType);
        organizationTypeConcept.addCoding(coding);
        organization.setType(Collections.singletonList(organizationTypeConcept));

        //Set Address
        Address address = new Address();
        address.addLine(addressLine);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setPostalCode(postalCode);

        organization.addAddress(address);
        organization.setActive(operationalStatus);

        // Set identifier
        Identifier identifier = new Identifier().setSystem("urn:oid:1.2.3.4.5").setValue(identifierValue);
        organization.addIdentifier(identifier);

        // Set contact information
        ContactPoint phoneContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(phone).setUse(ContactPoint.ContactPointUse.MOBILE);
        ContactPoint emailContact = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(email).setUse(ContactPoint.ContactPointUse.HOME);
        organization.addTelecom(phoneContact).addTelecom(emailContact);

        return organization;
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
