package eu.europa.ec.fhir.gitb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

/**
 * Configuration class responsible for creating the Spring beans required by the service.
 */
@Configuration
public class BeanConfig {

    /**
     * The AnyContent assign service endpoint.
     *
     * @return The endpoint.
     */
    @Bean
    public EndpointImpl anyContentAssignService(Bus cxfBus, AnyContentAssignServiceImpl serviceImplementation) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceImplementation);
        endpoint.setServiceName(new QName("http://www.gitb.com/ms/v1/", "MessagingServiceService"));
        endpoint.setEndpointName(new QName("http://www.gitb.com/ms/v1/", "MessagingServicePort"));
        endpoint.publish("/any-content-assign-service");
        return endpoint;
    }

    /**
     * The JsonString Converter service endpoint.
     *
     * @return The endpoint.
     */
    @Bean
    public EndpointImpl jsonStringConverterService(Bus cxfBus, JsonStringConverterServiceImpl serviceImplementation) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceImplementation);
        endpoint.setServiceName(new QName("http://www.gitb.com/ms/v1/", "MessagingServiceService"));
        endpoint.setEndpointName(new QName("http://www.gitb.com/ms/v1/", "MessagingServicePort"));
        endpoint.publish("/json-string-converter-service");
        return endpoint;
    }

    /**
     * The Fhir Context service endpoint.
     *
     * @return The endpoint.
     */
    @Bean
    public EndpointImpl fhirContextService(Bus cxfBus, FhirContextServiceImpl serviceImplementation) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceImplementation);
        endpoint.setServiceName(new QName("http://www.gitb.com/ms/v1/", "MessagingServiceService"));
        endpoint.setEndpointName(new QName("http://www.gitb.com/ms/v1/", "MessagingServicePort"));
        endpoint.publish("/fhir-context-service");
        return endpoint;
    }

    /**
     * The API Call service endpoint.
     *
     * @return The endpoint.
     */
    @Bean
    public EndpointImpl apiCallService(Bus cxfBus, ApiCallServiceImpl serviceImplementation) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceImplementation);
        endpoint.setServiceName(new QName("http://www.gitb.com/ms/v1/", "MessagingServiceService"));
        endpoint.setEndpointName(new QName("http://www.gitb.com/ms/v1/", "MessagingServicePort"));
        endpoint.publish("/api-call-service");
        return endpoint;
    }

    /**
     * Jackson object mapper to process JSON content.
     *
     * @return The mapper.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
