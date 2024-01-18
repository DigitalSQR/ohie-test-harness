package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.facilityregistry;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

@Component
public class FRF9TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(FRF9TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            String testCaseName = this.getClass().getSimpleName();
            LOGGER.info("Start testing " + testCaseName);

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_FACILITY_REGISTRY_ID);
            if(client == null) {
                return new ValidationResultInfo(testCaseName, ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            String city = "Stockholm City" + new SecureRandom().nextInt(999999);
            String orgName = "Burgers University Medical Center" + new SecureRandom().nextInt(999999);
            String ambulanceName = "Ambulance" + new SecureRandom().nextInt(999999);

            // Create a new organization
            Organization organization = FHIRUtils.createOrganization(orgName, "Sweden", city, "+41-123-23");
            MethodOutcome outcome = client.create().resource(organization).execute();
            // Check if the organization was created successfully
            if (!outcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating organization");
                return new ValidationResultInfo(testCaseName, ErrorLevel.ERROR, "Failed to create organization");
            }

            // Create another organization with same city
            organization = FHIRUtils.createOrganization("Richard Hospital", "Sweden", city, "+41-543-73");
            outcome = client.create().resource(organization).execute();
            // Check if the organization was created successfully
            if (!outcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating organization");
                return new ValidationResultInfo(testCaseName, ErrorLevel.ERROR, "Failed to create organization");
            }

            LOGGER.info("Verify searching organization by city");
            // 1. Verification of search functionality of organization by city
            Bundle bundle = client
                    .search()
                    .forResource(Organization.class)
                    .where(new StringClientParam("address-city").matchesExactly().value(city))
                    .returnBundle(Bundle.class)
                    .execute();
            List<Organization> organizations = FHIRUtils.processBundle(Organization.class, bundle);
            if (organizations.size() != 2) {
                LOGGER.error(testCaseName + "Testcase Failed when searching organization by city");
                return new ValidationResultInfo(testCaseName, ErrorLevel.ERROR, "Failed to search organization by city");
            }

            LOGGER.info("Verify searching organization by name");
            // 2. Verification of search functionality of organization by name
            bundle = client
                    .search()
                    .forResource(Organization.class)
                    .where(new StringClientParam("name").matchesExactly().value(orgName))
                    .returnBundle(Bundle.class)
                    .execute();
            organizations = FHIRUtils.processBundle(Organization.class, bundle);
            if (organizations.size() != 1) {
                LOGGER.error(testCaseName + "Testcase Failed when searching organization by name");
                return new ValidationResultInfo(testCaseName, ErrorLevel.ERROR, "Failed to search organization by name");
            }

            // Create a new Ambulance Location resource
            Location ambulance = FHIRUtils.createAmbulance("BUMC " + ambulanceName, "Ambulances provided by the Burgers University Medical Center", "108", Location.LocationStatus.ACTIVE);
            outcome = client.create().resource(ambulance).execute();
            // Check if the ambulance was created successfully
            if (!outcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating ambulance");
                return new ValidationResultInfo(testCaseName, ErrorLevel.ERROR, "Failed to create ambulance");
            }

            // Create another Ambulance Location resource with inactive status
            ambulance = FHIRUtils.createAmbulance("RM " + ambulanceName, "Ambulances provided by the Richard Morris Medical Center", "108", Location.LocationStatus.SUSPENDED);
            outcome = client.create().resource(ambulance).execute();
            // Check if the ambulance was created successfully
            if (!outcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating ambulance");
                return new ValidationResultInfo(testCaseName, ErrorLevel.ERROR, "Failed to create ambulance");
            }

            LOGGER.info("Verify searching ambulance by type");
            // 3. Verification of search functionality of ambulance by type
            bundle = client
                    .search()
                    .forResource(Location.class)
                    .where(new StringClientParam("name").contains().value(ambulanceName))
                    .where(Location.TYPE.exactly().code("AMB"))
                    .returnBundle(Bundle.class)
                    .execute();
            List<Location> ambulances = FHIRUtils.processBundle(Location.class, bundle);
            if (ambulances.size() != 2) {
                LOGGER.error(testCaseName + "Testcase Failed when searching ambulance by type");
                return new ValidationResultInfo(testCaseName, ErrorLevel.ERROR, "Failed to search ambulance by type");
            }

            LOGGER.info("Verifying ambulance by status");
            // 4. Verification of search functionality of ambulance by status
            bundle = client
                    .search()
                    .forResource(Location.class)
                    .where(Location.STATUS.exactly().code("suspended"))
                    .where(new StringClientParam("name").contains().value(ambulanceName))
                    .returnBundle(Bundle.class)
                    .execute();
            ambulances = FHIRUtils.processBundle(Location.class, bundle);
            if (ambulances.size() != 1) {
                LOGGER.error(testCaseName + "Testcase Failed when searching ambulance by status");
                return new ValidationResultInfo(testCaseName, ErrorLevel.ERROR, "Failed to search ambulance by status");
            }

            // Pass the test case if all the above conditions are passed
            LOGGER.info(testCaseName + "Testcase successfully passed!");
            return new ValidationResultInfo(testCaseName, ErrorLevel.OK, "Passed");

        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
