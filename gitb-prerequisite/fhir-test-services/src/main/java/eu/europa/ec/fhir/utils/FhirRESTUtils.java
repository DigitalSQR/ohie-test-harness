package eu.europa.ec.fhir.utils;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import eu.europa.ec.fhir.handlers.MethodOutcomeResponse;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Component used to make calls to FHIR servers using IGenericClient.
 */
@Component
public class FhirRESTUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FhirRESTUtils.class);

    /**
     * Retrieve a resource by its ID using IGenericClient
     *
     * @param fhirClient   FHIR client
     * @param resourceType Resource type
     * @param id           ID of the resource to retrieve
     * @return MethodOutcomeResponse
     */
    public MethodOutcomeResponse getById(
            IGenericClient fhirClient,
            String resourceType,
            String id) {
        try {
            IBaseResource resource = fhirClient.read().resource(resourceType).withId(id).execute();
            return new MethodOutcomeResponse(200, fhirClient.getFhirContext().newJsonParser().encodeResourceToString(resource));
        } catch (Exception ex) {
            return new MethodOutcomeResponse(500, ex.getMessage());
        }
    }

    /**
     * Retrieve the history of a resource using IGenericClient
     *
     * @param fhirClient   FHIR client
     * @param resourceType Resource type
     * @param id           ID of the resource
     * @return MethodOutcomeResponse
     */
    public MethodOutcomeResponse getHistoryById(
            IGenericClient fhirClient,
            String resourceType,
            String id) {
        try {
            IBaseBundle history = fhirClient.history().onType(resourceType).andReturnBundle(IBaseBundle.class).execute();
            return new MethodOutcomeResponse(200, fhirClient.getFhirContext().newJsonParser().encodeResourceToString(history));
        } catch (Exception ex) {
            return new MethodOutcomeResponse(500, ex.getMessage());
        }
    }

    /**
     * Retrieves a summary bundle of a resource by ID.
     *
     * @param fhirClient   FHIR client instance.
     * @param resourceType The FHIR resource type (e.g., "Patient", "Observation", etc.).
     * @param resourceId   ID of the resource.
     * @return Summary bundle of the resource.
     */
    public MethodOutcomeResponse summary(IGenericClient fhirClient, String resourceType, String resourceId, String summaryType) {
        IBaseBundle summaryBundle = fhirClient
                .search()
                .forResource(resourceType)
                .where(new TokenClientParam("_id").exactly().code(resourceId))
                .returnBundle(Bundle.class)
                .encodedJson()
                .encodedXml()
                .summaryMode(SummaryEnum.valueOf(summaryType))
                .execute();
        return new MethodOutcomeResponse(200, fhirClient.getFhirContext().newJsonParser().encodeResourceToString(summaryBundle));
    }

    /**
     * Do the search operation. using IGenericClient
     *
     * @param fhirClient   fhir client
     * @param resourceType resource type
     * @param parameters   criteria filter parameters
     * @return MethodOutcomeResponse
     */
    public MethodOutcomeResponse search(
            IGenericClient fhirClient,
            String resourceType,
            String parameters) {
        // Convert the string to Class<T>
        try {
            IBaseBundle methodOutcome = fhirClient.search()
                    .byUrl(resourceType + "?" + parameters).execute();

            return new MethodOutcomeResponse(200, fhirClient.getFhirContext().newJsonParser().encodeResourceToString(methodOutcome));
        } catch (Exception ex) {
            return new MethodOutcomeResponse(500, ex.getMessage());
        }
    }

    /**
     * Do the create operation. using IGenericClient
     *
     * @param fhirClient  fhir client
     * @param fhirContext fhir context
     * @param resource    resource
     * @return MethodOutcomeResponse
     */
    public MethodOutcomeResponse create(
            IGenericClient fhirClient,
            FhirContext fhirContext,
            IBaseResource resource) {
        try {
            MethodOutcome methodOutcome = fhirClient.create().resource(resource).execute();
            IParser parser = fhirContext.newJsonParser();

            resource = methodOutcome.getResource();
            return new MethodOutcomeResponse(methodOutcome.getResponseStatusCode(), parser.encodeResourceToString(resource));
        } catch (Exception ex) {
            return new MethodOutcomeResponse(500, ex.getMessage());
        }
    }

    /**
     * Do the update operation. using IGenericClient
     *
     * @param fhirClient  fhir client
     * @param fhirContext fhir context
     * @param resource    resource
     * @return MethodOutcomeResponse
     */
    public MethodOutcomeResponse update(
            IGenericClient fhirClient,
            FhirContext fhirContext,
            IBaseResource resource) {
        try {
            MethodOutcome methodOutcome = fhirClient.update().resource(resource).execute();
            IParser parser = fhirContext.newJsonParser();

            resource = methodOutcome.getResource();
            return new MethodOutcomeResponse(methodOutcome.getResponseStatusCode(), parser.encodeResourceToString(resource));
        } catch (Exception ex) {
            return new MethodOutcomeResponse(500, ex.getMessage());
        }
    }

    /**
     * Do the delete operation. using IGenericClient
     *
     * @param fhirClient     fhir client
     * @param fhirContext    fhir context
     * @param resourceType   resource
     * @param resourceTypeId resourceTypeId
     * @return MethodOutcomeResponse
     */
    public MethodOutcomeResponse delete(
            IGenericClient fhirClient,
            FhirContext fhirContext,
            String resourceType,
            String resourceTypeId) {
        try {
            MethodOutcome methodOutcome = fhirClient.delete().resourceById(resourceType, resourceTypeId).execute();
            IParser parser = fhirContext.newJsonParser();

            IBaseResource resource = methodOutcome.getResource();
            return new MethodOutcomeResponse(methodOutcome.getResponseStatusCode(), parser.encodeResourceToString(resource));
        } catch (Exception ex) {
            return new MethodOutcomeResponse(500, ex.getMessage());
        }
    }

    /**
     * Create Resource based on JsonString
     *
     * @param fhirContext fhir context
     * @param jsonString  resource body in jsonString
     * @return MethodOutcomeResponse
     */
    public IBaseResource createResource(FhirContext fhirContext, String jsonString) {
        IParser parser = fhirContext.newJsonParser();
        return parser.parseResource(jsonString);
    }

    /**
     * Perform the validate-code operation using IGenericClient.
     *
     * @param fhirClient   fhir client
     * @param resourceType resource
     * @return MethodOutcomeResponse
     */
    public MethodOutcomeResponse validateCode(
            IGenericClient fhirClient,
            String resourceType,
            String url,
            String code,
            String system) {
        try {

            UriType urlType = new UriType(url);
            CodeType codeType = new CodeType(code);
            UriType systemType = new UriType(system);
            Parameters parameters = new Parameters();
            parameters.addParameter().setName("code").setValue(codeType);

            if (!system.isEmpty()) {
                parameters.addParameter().setName("url").setValue(urlType);
                parameters.addParameter().setName("system").setValue(systemType);
            } else {
                parameters.addParameter().setName("url").setValue(urlType);
            }

            //Perform $validate-code operation
            Parameters outcome = fhirClient
                    .operation()
                    .onType(resourceType)
                    .named("$validate-code")
                    .withParameters(parameters)
                    .execute();

            String outcomeJson = fhirClient.getFhirContext().newJsonParser().encodeResourceToString(outcome);

            return new MethodOutcomeResponse(200, outcomeJson);
        } catch (Exception ex) {
            return new MethodOutcomeResponse(500, ex.getMessage());
        }
    }

    /**
     * Perform the $expand operation using IGenericClient.
     *
     * @param fhirClient   fhir client
     * @param resourceType resource
     * @return MethodOutcomeResponse
     */
    public MethodOutcomeResponse expand(
            IGenericClient fhirClient,
            String resourceType,
            String url) {
        try {
            UriType urlType = new UriType(url);
            Parameters parameters = new Parameters();
            parameters.addParameter().setName("url").setValue(urlType);

            // Perform $expand operation
            ValueSet resultant = fhirClient
                    .operation()
                    .onType(resourceType)
                    .named("$expand")
                    .withParameters(parameters)
                    .returnResourceType(ValueSet.class)
                    .execute();

            String outcomeJson = fhirClient.getFhirContext().newJsonParser().encodeResourceToString(resultant);

            return new MethodOutcomeResponse(200, outcomeJson);
        } catch (Exception ex) {
            return new MethodOutcomeResponse(500, ex.getMessage());
        }
    }

    /**
     * Perform the $lookup-code operation using IGenericClient.
     *
     * @param fhirClient   fhir client
     * @param resourceType resource
     * @return MethodOutcomeResponse
     */
    public MethodOutcomeResponse lookup(
            IGenericClient fhirClient,
            String resourceType,
            String system,
            String code) {
        try {

            UriType systemUrl = new UriType(system);
            CodeType codeType = new CodeType(code);
            Parameters parameters = new Parameters();
            parameters.addParameter().setName("system").setValue(systemUrl);
            parameters.addParameter().setName("code").setValue(codeType);

            //Perform $lookup operation
            Parameters outcome = fhirClient
                    .operation()
                    .onType(resourceType)
                    .named("$lookup")
                    .withParameters(parameters)
                    .execute();

            String outcomeJson = fhirClient.getFhirContext().newJsonParser().encodeResourceToString(outcome);

            return new MethodOutcomeResponse(200, outcomeJson);
        } catch (Exception ex) {
            return new MethodOutcomeResponse(500, ex.getMessage());
        }
    }

    /**
     * Perform the $translate operation using IGenericClient.
     *
     * @param fhirClient   fhir client
     * @param resourceType resource
     * @return MethodOutcomeResponse
     */
    public MethodOutcomeResponse translate(
            IGenericClient fhirClient,
            String resourceType,
            String url,
            String system,
            String code) {
        try {
            UriType conceptMapUrl = new UriType(url);
            UriType systemUrl = new UriType(system);
            CodeType codeType = new CodeType(code);
            Parameters parameters = new Parameters();
            parameters.addParameter().setName("url").setValue(conceptMapUrl);
            parameters.addParameter().setName("system").setValue(systemUrl);
            parameters.addParameter().setName("code").setValue(codeType);

            //Perform $lookup operation
            Parameters outcome = fhirClient
                    .operation()
                    .onType(resourceType)
                    .named("$translate")
                    .withParameters(parameters)
                    .execute();

            String outcomeJson = fhirClient.getFhirContext().newJsonParser().encodeResourceToString(outcome);

            return new MethodOutcomeResponse(200, outcomeJson);
        } catch (Exception ex) {
            return new MethodOutcomeResponse(500, ex.getMessage());
        }
    }

}