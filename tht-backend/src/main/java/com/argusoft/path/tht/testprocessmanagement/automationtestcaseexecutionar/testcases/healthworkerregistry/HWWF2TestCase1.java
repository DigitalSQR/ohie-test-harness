package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.healthworkerregistry;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;

@Component
public class HWWF2TestCase1 implements TestCase {
    public static final Logger LOGGER = LoggerFactory.getLogger(HWWF2TestCase1.class);
    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap, ContextInfo contextInfo) throws OperationFailedException {
        try{
            LOGGER.info("Start testing HWWF2TestCase1");

            LOGGER.info("Creating practitioner");

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_HEALTH_WORKER_REGISTRY_ID);
            if (client == null) {
                return new ValidationResultInfo("testHWWF2TestCase1", ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            //creating a practitioner
            Practitioner practitioner= FHIRUtils.createPractitioner("Voigt","Pieter","Dr","male","1995-11-06","urn:oid:2.16.528.1.1007.3.1","890455352",true,"0205669382","p.voigt@bmc.nl");

            MethodOutcome outcome = client.create()
                    .resource(practitioner)
                    .execute();

            // Check if the practitioner was created successfully
            if (!outcome.getCreated()) {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to create practitioner");
            }

            //verify practitioner by ID
            String practitionerId = outcome.getResource().getIdElement().getIdPart();
            Practitioner createdPractitioner = client.read()
                    .resource(Practitioner.class)
                    .withId(practitionerId)
                    .execute();

            if (!practitioner.getBirthDate().equals(createdPractitioner.getBirthDate())) {
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to get practitioner by ID");
            }



            //query based on parameter
            Bundle bundle = client.search()
                    .forResource(Practitioner.class)
                    .where(Practitioner.IDENTIFIER.exactly().systemAndCode("urn:oid:2.16.528.1.1007.3.1","890455352"))
                    .returnBundle(Bundle.class)
                    .execute();

            if (!bundle.hasEntry()) {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to fetch practitioner based on parameter");
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                Practitioner temppractitioner = (Practitioner) entry.getResource();
                if(!temppractitioner.getBirthDate().equals(dateFormat.parse("1995-11-06"))){
                    LOGGER.error("Testcase Failed");
                    return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to fetch practitioner based on parameter");
                }
            }


            LOGGER.info("Creating Location resource");

            //creating a Location
            Location location = FHIRUtils.createLocation(null,"HCL hospital", "123 Main St", "Cityville", "Caroel", "12345", "USA", "555-1234", 40.7484, -73.9869);

            outcome = client.create()
                    .resource(location)
                    .execute();

            // Check if the location was created successfully
            if (!outcome.getCreated()) {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to create location");
            }

            //verify practitioner by ID
            String locationId=outcome.getResource().getIdElement().getIdPart();
            Location createdLocation = client.read()
                    .resource(Location.class)
                    .withId(locationId)
                    .execute();


            if (!location.getPosition().getLongitude().equals(createdLocation.getPosition().getLongitude()) || !location.getPosition().getLatitude().equals(createdLocation.getPosition().getLatitude())) {
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to get location by ID");

            }


            // query based on some parameter
            bundle = client.search()
                    .forResource(Location.class)
                    .where(Location.RES_ID.exactly().identifier(locationId))
                    .returnBundle(Bundle.class)
                    .execute();

            if (!bundle.hasEntry()) {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to fetch Location based on parameter");
            }


            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                Location tempLocation = (Location) entry.getResource();
                if(!tempLocation.getName().equals("HCL hospital")){
                    LOGGER.error("Testcase Failed");
                    return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to fetch Location based on parameter");

                }
            }


            LOGGER.info("Creating HealthcareService resource");

            //creating a HealthcareService
            HealthcareService healthcareService = FHIRUtils.createHealthcareService(null,"http://example.com/service-category","clinic","Clinic","http://example.com/service-type","primary-care","Primary Care","Primary Care Clinic",true,createdLocation);

            outcome = client.create()
                    .resource(healthcareService)
                    .execute();

            // Check if the healthcareService was created successfully
            if (!outcome.getCreated()) {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to create healthcareService");
            }

            //verify practitioner by ID
            String healthcareServiceid=outcome.getResource().getIdElement().getIdPart();
            HealthcareService createdHealthcareService = client.read()
                    .resource(HealthcareService.class)
                    .withId(healthcareServiceid)
                    .execute();


            if (!healthcareService.getName().equals(createdHealthcareService.getName())){
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to get healthcareService by ID");
            }


            // query based on some parameter
            bundle = client.search()
                    .forResource(HealthcareService.class)
                    .where(HealthcareService.LOCATION.hasId(locationId))
                    .returnBundle(Bundle.class)
                    .execute();

            if (!bundle.hasEntry()) {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to fetch HealthcareService based on parameter");
            }

            boolean flag = false;
            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                HealthcareService tempHealthcareService = (HealthcareService) entry.getResource();
                if(tempHealthcareService.getName().equals("Primary Care Clinic") && tempHealthcareService.getIdElement().getIdPart().equals(healthcareServiceid)){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to fetch HealthcareService based on parameter");
            }


            LOGGER.info("Creating PractitionerRole resource");

            //creating a PractitionerRole
            PractitionerRole practitionerRole = FHIRUtils.createPractitionerRole(null,createdPractitioner,"http://example.com/role-codes","physician",true,createdLocation,createdHealthcareService,"09:00:00","16:30:00");

            outcome = client.create()
                    .resource(practitionerRole)
                    .execute();

            // Check if the practitionerRole was created successfully
            if (!outcome.getCreated()) {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to create practitionerRole");
            }

            //verify practitionerRole by ID
            String practitionerRoleId=outcome.getResource().getIdElement().getIdPart();
            PractitionerRole createdPractitionerRole = client.read()
                    .resource(PractitionerRole.class)
                    .withId(outcome.getResource().getIdElement().getIdPart())
                    .execute();

            if (!createdPractitionerRole.getPractitioner().getReference().equals("Practitioner/"+practitionerId)) {
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to get practitionerRole by ID");
            }

            // query based on some parameter
            bundle = client.search()
                    .forResource(PractitionerRole.class)
                    .where(PractitionerRole.ROLE.exactly().code("physician"))
                    .and(PractitionerRole.LOCATION.hasId(locationId))
                    .and(PractitionerRole.PRACTITIONER.hasId(practitionerId))
                    .returnBundle(Bundle.class)
                    .execute();

            if (!bundle.hasEntry()) {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to fetch PractitionerRole based on parameter");
            }

            flag = false;
            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                PractitionerRole temppractitionerRole = (PractitionerRole) entry.getResource();
                if(temppractitionerRole.getIdElement().getIdPart().equals(practitionerRoleId)){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.ERROR, "Failed to fetch practitionerRole based on parameter");
            }

            LOGGER.info("Testcase successfully passed!");
            return new ValidationResultInfo("testHWWF2Case1", ErrorLevel.OK, "Passed");


        }
        catch(Exception ex) {
            LOGGER.error("Exception while HWWF2TestCase1 ", ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}