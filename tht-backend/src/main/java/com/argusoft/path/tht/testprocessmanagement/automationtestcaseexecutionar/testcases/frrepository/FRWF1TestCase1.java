package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.frrepository;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.Location;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;


@Component
public class FRWF1TestCase1 implements TestCase {

    public static final Logger LOGGER =  LoggerFactory.getLogger(FRWF1TestCase1.class);
    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            LOGGER.info("Start testing FRWF1TestCase1");
            LOGGER.info("Creating facility");

            //create new facility

            // Specialty as a list of strings
            List<String> specialties = new ArrayList<>();
            specialties.add("Cardiology");
            specialties.add("Dermatology");

            //get location to pass it as reference
            String locationId = "68";
            Location location = client.read().resource(Location.class).withId(locationId).execute();

            HealthcareService healthcareService = FHIRUtils.createHealthcareService("1111", "Consulting psychologists and/or psychology services",
                    "Providing Specialist psychology services to the greater Den Burg area, many years of experience dealing with PTSD ", "555-555-5555",
                    "directaddress@example.com", specialties, location);

            MethodOutcome outcome = client.create()
                    .resource(healthcareService)
                    .execute();

            // Check if the healthcare service was created successfully
            if (!outcome.getCreated()) {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testFRWF1Case1", ErrorLevel.ERROR, "Failed to create Facility");
            }

            String careServiceId = outcome.getResource().getIdElement().getIdPart();

            healthcareService = client.read()
                    .resource(HealthcareService.class)
                    .withId(careServiceId)
                    .execute();

            //check if we can queryy healthcare service or not
            if (healthcareService.hasName()) {
                LOGGER.info("Testcase successfully passed!");
                return new ValidationResultInfo("testFRWF1Case1", ErrorLevel.OK, "Passed");
            } else {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testFRWF1Case1", ErrorLevel.ERROR, "Failed to query services");

            }

        } catch (Exception ex) {
            LOGGER.error("Exception while FRWF1TestCase1 ",ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
