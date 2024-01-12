package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.facilityregistry;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository.CRWF1TestCase1;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FRF9TestCase1 implements TestCase {

    public static final Logger LOGGER =  LoggerFactory.getLogger(CRWF1TestCase1.class);

    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {

            String city = "Stockholm City";

            // Search for organizations by city
            Bundle bundle = client
                    .search()
                    .forResource(Organization.class)
                    .where(new StringClientParam("address-city").matchesExactly().value(city))
                    .returnBundle(Bundle.class)
                    .execute();
            // Get current organization count
            int orgCount = FHIRUtils.processBundle(Organization.class, bundle).size();

            // Create a new organization
            Organization organization = FHIRUtils.createOrganization("Burgers University Medical Center", "Sweden",city,"+41-123-23");
            MethodOutcome outcome = client.create().resource(organization).execute();
            // Check if the organization was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testFRF9Case1", ErrorLevel.ERROR, "Failed to create organization");
            }

            // 1. Verification of search functionality of organization by city
            // Search for organizations by city again
            bundle = client
                    .search()
                    .forResource(Organization.class)
                    .where(new StringClientParam("address-city").matchesExactly().value(city))
                    .returnBundle(Bundle.class)
                    .execute();
            List<Organization> organizations = FHIRUtils.processBundle(Organization.class, bundle);
            // Verify the search functionality by checking the orgCount has increased after creation
            if (organizations.size() != orgCount) {
                return new ValidationResultInfo("testFRF9Case1", ErrorLevel.ERROR, "Failed to search organization by city");
            }

            // Create a new Ambulance Location resource
            Location ambulance = FHIRUtils.createAmbulance("BUMC Ambulance", "Ambulances provided by the Burgers University Medical Center","108");
            outcome = client.create().resource(ambulance).execute();
            // Check if the ambulance was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testFRF9Case1", ErrorLevel.ERROR, "Failed to create ambulance");
            }

            // Create another Ambulance Location resource
            ambulance = FHIRUtils.createAmbulance("RM Ambulance", "Ambulances provided by the Richard Morris Medical Center","108");
            outcome = client.create().resource(ambulance).execute();
            // Check if the ambulance was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testFRF9Case1", ErrorLevel.ERROR, "Failed to create ambulance");
            }

            // 2. Verification of search functionality of ambulance by name
            // Search for organizations by city again
            bundle = client
                    .search()
                    .forResource(Location.class)
                    .where(new StringClientParam("description").contains().value("Ambulances provided by the"))
                    .returnBundle(Bundle.class)
                    .execute();
            List<Location> ambulances = FHIRUtils.processBundle(Location.class, bundle);
            if (ambulances.size() != 2) {
                return new ValidationResultInfo("testFRF9Case1", ErrorLevel.ERROR, "Failed to search ambulance by description");
            }

            return new ValidationResultInfo("testFRF9Case1", ErrorLevel.ERROR, "Failed to search patients by Identifiers and demographics");

        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
