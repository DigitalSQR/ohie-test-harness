package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.healthworkerregistry;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
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
public class HWWF4TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(HWWF4TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            LOGGER.info("Start testing HWWF4TestCase1");

            LOGGER.info("Creating HealthWorker");

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_HEALTH_WORKER_REGISTRY_ID);
            if (client == null) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            //create practitioner
            Practitioner practitioner = FHIRUtils.createPractitioner("Smith", "M", "1962-12-12", "00010", "888-888-8888");

            MethodOutcome practitionerOutcome = client.create()
                    .resource(practitioner)
                    .execute();

            // Check if the practitioner was created successfully
            if (Boolean.FALSE.equals(practitionerOutcome.getCreated())) {
                LOGGER.error("Testcase Failed for creating practitioner");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create practitioner");
            }

            String practitionerId = practitionerOutcome.getResource().getIdElement().getIdPart();
            practitioner = client.read()
                    .resource(Practitioner.class)
                    .withId(practitionerId)
                    .execute();

            //set new name for update
            practitioner.getName().get(0).getGiven().get(0).setValue("ALICE");
            practitionerOutcome = client.update()
                    .resource(practitioner)
                    .withId(practitionerId)
                    .execute();

            if (!practitionerId.equals(practitionerOutcome.getResource().getIdElement().getIdPart())) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Instead of Update, Server has created new Practitioner");
            }

            practitioner = client.read()
                    .resource(Practitioner.class)
                    .withId(practitionerId)
                    .execute();

            //check for practitioner updated or not
            if (!practitioner.getName().get(0).getGiven().get(0).getValue().equals("ALICE")) {
                return new ValidationResultInfo(ErrorLevel.OK, "Failed to update Practitioner");
            }

            LOGGER.info("Creating Location in HWR");
            //create location
            Location location = FHIRUtils.createLocation("0234", "South Wing, second floor", "Second floor of the Old South Wing, formerly in use by Psychiatry",
                    "2328", "second wing admissions", "Den Burg", "9105 PZ", "NLD", null);

            MethodOutcome locationOutcome = client.create()
                    .resource(location)
                    .execute();

            //check for the location created successfully
            if (Boolean.FALSE.equals(locationOutcome.getCreated())) {
                LOGGER.error("Testcase Failed for creating location in HWR");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create location");
            }

            String locationId = locationOutcome.getResource().getIdElement().getIdPart();
            location = client.read()
                    .resource(Location.class)
                    .withId(locationId)
                    .execute();

            location.setName("North Wing , first floor");
            locationOutcome = client.update()
                    .resource(location)
                    .execute();

            if (!locationId.equals(locationOutcome.getResource().getIdElement().getIdPart())) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Instead of Update, Server has created new Location");
            }

            location = client.read()
                    .resource(Location.class)
                    .withId(locationId)
                    .execute();

            //check for location updated or not
            if (!location.getName().equals("North Wing , first floor")) {
                return new ValidationResultInfo(ErrorLevel.OK, "Failed to update Location");
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
            if (Boolean.FALSE.equals(careServiceOutcome.getCreated())) {
                LOGGER.error("Testcase Failed to create HealthCare Service");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create HealthCare Service");
            }

            String careServiceId = careServiceOutcome.getResource().getIdElement().getIdPart();
            healthcareService = client.read()
                    .resource(HealthcareService.class)
                    .withId(careServiceId)
                    .execute();

            healthcareService.setName("Example Care Services");
            careServiceOutcome = client.update()
                    .resource(healthcareService)
                    .execute();

            if (!careServiceId.equals(careServiceOutcome.getResource().getIdElement().getIdPart())) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Instead of Update, Server has created new HealthCare Service ");
            }

            healthcareService = client.read()
                    .resource(HealthcareService.class)
                    .withId(careServiceId)
                    .execute();

            //check for HealthCAre Service updated or not
            if (!healthcareService.getName().equals("Example Care Services")) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to update HealthCare Service");
            }

            //For Practitioner Role
            LOGGER.info("Creating Practitioner Role in HWR");

            //create practitioner role
            PractitionerRole practitionerRole = FHIRUtils.createPractitionerRole("00011", "General medical practice", "888-888-8888", practitioner, location, healthcareService);

            MethodOutcome practitionerRoleOutcome = client.create()
                    .resource(practitionerRole)
                    .execute();

            // Check if the practitioner role was created successfully
            if (Boolean.FALSE.equals(practitionerRoleOutcome.getCreated())) {
                LOGGER.error("Testcase Failed to create Practitioner Role");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create practitioner role");
            }

            String practitionerRoleId = practitionerRoleOutcome.getResource().getIdElement().getIdPart();
            practitionerRole = client.read()
                    .resource(PractitionerRole.class)
                    .withId(practitionerRoleId)
                    .execute();

            ContactPoint contactPoint = practitionerRole.getTelecom().get(0);
            contactPoint.setValue("222-222-2222");

            practitionerRoleOutcome = client.update()
                    .resource(practitionerRole)
                    .execute();

            if (!practitionerRoleId.equals(practitionerRoleOutcome.getResource().getIdElement().getIdPart())) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Instead of Update, Server has created new Practitioner Role ");
            }

            practitionerRole = client.read()
                    .resource(PractitionerRole.class)
                    .withId(practitionerRoleId)
                    .execute();

            //check for practitioner Role updated or not
            if (practitionerRole.getTelecom().stream()
                    .noneMatch(prole -> "222-222-2222".equals(prole.getValue()))) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to update HealthCare Service");
            }

            LOGGER.info("Testcase Passed");
            return new ValidationResultInfo(ErrorLevel.OK, "Passed");

        } catch (Exception ex) {
            LOGGER.error(ValidateConstant.EXCEPTION + HWWF4TestCase1.class.getSimpleName(), ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
