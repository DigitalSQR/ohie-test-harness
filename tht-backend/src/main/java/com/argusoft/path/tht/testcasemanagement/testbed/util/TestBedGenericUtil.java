package com.argusoft.path.tht.testcasemanagement.testbed.util;

import java.net.URI;
import java.net.URISyntaxException;

public class TestBedGenericUtil {

    public static URI buildURI(String host, String endpoint) throws URISyntaxException {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("Host cannot be null or empty");
        }
        if (endpoint == null || endpoint.isEmpty()) {
            throw new IllegalArgumentException("Endpoint cannot be null or empty");
        }

        // Check if host ends with "/" and endpoint starts with "/", adjust accordingly
        String adjustedHost = host.endsWith("/") ? host.substring(0, host.length() - 1) : host;
        String adjustedEndpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;

        // Construct the URI
        return new URI(adjustedHost + adjustedEndpoint);
    }

}
