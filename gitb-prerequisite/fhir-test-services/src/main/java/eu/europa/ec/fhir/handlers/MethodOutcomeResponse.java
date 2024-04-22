package eu.europa.ec.fhir.handlers;

/**
 * Record reflecting a request's result.
 *
 * @param status            The HTTP status code.
 * @param methodOutcomeBody The methodOutcome body of the FHIR.
 */
public record MethodOutcomeResponse(int status, String methodOutcomeBody) {

}