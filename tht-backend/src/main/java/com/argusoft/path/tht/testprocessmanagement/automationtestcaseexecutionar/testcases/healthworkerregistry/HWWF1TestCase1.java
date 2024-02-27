package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.healthworkerregistry;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class HWWF1TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(HWWF1TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            LOGGER.info("Start testing HWWF1TestCase1");

            LOGGER.info("Creating HealthWorker");

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_HEALTH_WORKER_REGISTRY_ID);
            if (client == null) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            //create practitioner
            Practitioner practitioner = FHIRUtils.createPractitioner("Smith","M","1962-12-12","00010","888-888-8888");

            MethodOutcome practitionerOutcome = client.create()
                    .resource(practitioner)
                    .execute();

            // Check if the practitioner was created successfully
            if (!practitionerOutcome.getCreated()) {
                LOGGER.error("Testcase Failed for creating practitioner");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create practitioner");
            }

            String practitionerId = practitionerOutcome.getResource().getIdElement().getIdPart();
            practitioner = client.read()
                    .resource(Practitioner.class)
                    .withId(practitionerId)
                    .execute();

            //check for practitioner
            if (!practitioner.hasName()) {
                LOGGER.error("Testcase Failed to read practitioner");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to read practitioner");
            }

            LOGGER.info("Creating Location in HWR");
            //create location
            Location location = FHIRUtils.createLocation("0234", "South Wing, second floor", "Second floor of the Old South Wing, formerly in use by Psychiatry",
                    "2328", "second wing admissions", "Den Burg", "9105 PZ", "NLD",null);

            MethodOutcome locationOutcome = client.create()
                    .resource(location)
                    .execute();

            //check for the location created successfully
            if (!locationOutcome.getCreated()) {
                LOGGER.error("Testcase Failed for creating location in HWR");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create location");
            }

            String locationId = locationOutcome.getResource().getIdElement().getIdPart();
            location = client.read()
                    .resource(Location.class)
                    .withId(locationId)
                    .execute();

            //check for location
            if (!location.hasName()) {
                LOGGER.error("Testcase Failed to read location in HWR");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to read location");
            }

            //For Health Care Service
            LOGGER.info("Creating HealthCare Service in HWR");

            // Specialty as a list of strings
            List<String> specialties = new ArrayList<>();
            specialties.add("Cardiology");
            specialties.add("Dermatology");

            //create Health Care Service
            HealthcareService healthcareService = FHIRUtils.createHealthcareService("1111", "Consulting psychologists and/or psychology services",
                    "Providing Specialist psychology services to the greater Den Burg area, many years of experience dealing with PTSD ", "555-555-5555",
                    "directaddress@example.com", specialties, location);

            MethodOutcome careServiceOutcome = client.create()
                    .resource(healthcareService)
                    .execute();

            // Check if the healthcare service was created successfully
            if (!careServiceOutcome.getCreated()) {
                LOGGER.error("Testcase Failed to create HealthCare Service");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create HealthCare Service");
            }

            String careServiceId = careServiceOutcome.getResource().getIdElement().getIdPart();
            healthcareService = client.read()
                    .resource(HealthcareService.class)
                    .withId(careServiceId)
                    .execute();

            //check if we can queryy healthcare service or not
            if (!healthcareService.hasName()) {
                LOGGER.error("Testcase failed to read HealthCare Service");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to read HealthCare Service");
            }

            //For Practitioner Role
            LOGGER.info("Creating Practitioner Role in HWR");

            //create practitioner role
            PractitionerRole practitionerRole = FHIRUtils.createPractitionerRole("00011", "General medical practice", "888-888-8888", practitioner, location, healthcareService);

            MethodOutcome practitionerRoleOutcome = client.create()
                    .resource(practitionerRole)
                    .execute();

            // Check if the practitioner role was created successfully
            if (!practitionerRoleOutcome.getCreated()) {
                LOGGER.error("Testcase Failed to create Practitioner Role");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create practitioner role");
            }

            // Search for Practitioner by name
            Bundle practitionerBundle = client.search()
                    .forResource(Practitioner.class)
                    .where(Practitioner.NAME.matches().value("Smith"))
                    .returnBundle(Bundle.class)
                    .execute();

            // Check if practitioner was found by name
            if (practitionerBundle.getEntry().isEmpty()) {
                LOGGER.error("Testcase failed to search practitioner by name");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to search practitioner by name");
            }


            // Search for Location by name
            Bundle locationBundle = client.search()
                    .forResource(Location.class)
                    .where(Location.NAME.matches().value("South Wing, second floor"))
                    .returnBundle(Bundle.class)
                    .execute();

            // Check if location was found by name
            if (locationBundle.getEntry().isEmpty()) {
                LOGGER.error("Testcase failed to search location by name");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to search location by name");
            }

            // Search for Healthcare Service by name
            Bundle careServiceBundle = client.search()
                    .forResource(HealthcareService.class)
                    .where(HealthcareService.NAME.matches().value("Consulting psychologists and/or psychology services"))
                    .returnBundle(Bundle.class)
                    .execute();

            // Check if care services was found by name
            if (careServiceBundle.getEntry().isEmpty()) {
                LOGGER.error("Testcase failed to search careService by name");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to search careService by name");
            }

            //search practitioner by passing attributes reference
            Bundle resultBundle = client.search()
                    .forResource(PractitionerRole.class)
                    .where(PractitionerRole.PRACTITIONER.hasId(practitioner.getId()))
                    .and(PractitionerRole.LOCATION.hasId(location.getId()))
                    .returnBundle(Bundle.class)
                    .execute();

            //check if present or not present
            if (resultBundle.getEntry().isEmpty()) {
                LOGGER.error("Testcase failed to search PractitionerRole by Attributes practitioner and location");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to search PractitionerRole by Attributes practitioner and location");
            }

            //check for healthcare service present or not
            if (practitionerRole.getHealthcareService().stream().noneMatch(careService -> careService.getResource().getIdElement().getIdPart().equals(careServiceId))) {
                LOGGER.error("Testcase failed to find healthcare service in practitioner role ");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to find healthcare service in practitioner role");
            }

            LOGGER.info("Testcase Passed");
            return new ValidationResultInfo(ErrorLevel.OK, "Passed");

        } catch (Exception ex) {
            LOGGER.error(ValidateConstant.EXCEPTION + HWWF1TestCase1.class.getSimpleName(), ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}

