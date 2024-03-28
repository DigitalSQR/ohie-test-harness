package eu.europa.ec.fhir.utils;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.europa.ec.fhir.handlers.MethodOutcomeResponse;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
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
}
