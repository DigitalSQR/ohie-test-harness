package eu.europa.ec.fhir.gitb;

import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

import java.io.IOException;

public class CustomHeaderInterceptor implements IClientInterceptor {
    private String headerName;
    private String headerValue;

    public CustomHeaderInterceptor(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    @Override
    public void interceptRequest(IHttpRequest iHttpRequest) {
        iHttpRequest.addHeader(this.headerName, this.headerValue);
    }

    @Override
    public void interceptResponse(IHttpResponse iHttpResponse) throws IOException {
    }
}
