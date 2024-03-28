package eu.europa.ec.fhir.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitb.core.AnyContent;
import com.gitb.core.ValueEmbeddingEnumeration;
import com.gitb.tr.TAR;
import com.gitb.tr.TestResultType;
import com.gitb.tr.ValidationCounters;
import com.google.gson.*;
import eu.europa.ec.fhir.gitb.JsonStringConverterServiceImpl;
import eu.europa.ec.fhir.handlers.MethodOutcomeResponse;
import eu.europa.ec.fhir.handlers.RequestResult;
import jakarta.xml.ws.WebServiceContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.headers.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Function;

/**
 * Component containing utility methods.
 */
@Component
public class Utils {

    public static final QName REPLY_TO_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ReplyTo");
    public static final QName TEST_SESSION_ID_QNAME = new QName("http://www.gitb.com", "TestSessionIdentifier", "gitb");
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    @Value("${fhir.contentTypeBase}")
    private String fhirContentType;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Create a report for the given result.
     * <p/>
     * This method creates the report, sets its time and constructs an empty context map to return values with.
     *
     * @param result The overall result of the report.
     * @return The report.
     */
    public TAR createReport(TestResultType result) {
        TAR report = new TAR();
        report.setContext(new AnyContent());
        report.getContext().setType("map");
        report.setResult(result);
        report.setCounters(new ValidationCounters());
        report.getCounters().setNrOfErrors(BigInteger.ZERO);
        report.getCounters().setNrOfWarnings(BigInteger.ZERO);
        report.getCounters().setNrOfAssertions(BigInteger.ZERO);
        try {
            report.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
        return report;
    }

    /**
     * Collect the inputs that match the provided name.
     *
     * @param parameterItems The items to look through.
     * @param inputName The name of the input to look for.
     * @return The collected inputs (not null).
     */
    public List<AnyContent> getInputsForName(List<AnyContent> parameterItems, String inputName) {
        List<AnyContent> inputs = new ArrayList<>();
        if (parameterItems != null) {
            for (AnyContent anInput: parameterItems) {
                if (inputName.equals(anInput.getName())) {
                    inputs.add(anInput);
                }
            }
        }
        return inputs;
    }

    /**
     * Get a single required input for the provided name.
     *
     * @param parameterItems The items to look through.
     * @param inputName The name of the input to look for.
     * @return The input.
     */
    public AnyContent getSingleRequiredInputForName(List<AnyContent> parameterItems, String inputName) {
        var inputs = getInputsForName(parameterItems, inputName);
        if (inputs.isEmpty()) {
            throw new IllegalArgumentException(String.format("No input named [%s] was found.", inputName));
        } else if (inputs.size() > 1) {
            throw new IllegalArgumentException(String.format("Multiple inputs named [%s] were found when only one was expected.", inputName));
        }
        return inputs.get(0);
    }

    /**
     * Get a single optional input for the provided name.
     *
     * @param parameterItems The items to look through.
     * @param inputName The name of the input to look for.
     * @return The input.
     */
    public Optional<AnyContent> getSingleOptionalInputForName(List<AnyContent> parameterItems, String inputName) {
        var inputs = getInputsForName(parameterItems, inputName);
        if (inputs.isEmpty()) {
            return Optional.empty();
        } else if (inputs.size() > 1) {
            throw new IllegalArgumentException(String.format("Multiple inputs named [%s] were found when at most one was expected.", inputName));
        } else {
            return Optional.of(inputs.get(0));
        }
    }

    /**
     * Convert the provided content to a string value.
     *
     * @param content The content to convert.
     * @return The string value.
     */
    public String asString(AnyContent content) {
        if (content == null || content.getValue() == null) {
            return null;
        } else if (content.getEmbeddingMethod() == ValueEmbeddingEnumeration.BASE_64) {
            // Value provided as BASE64 string.
            return new String(Base64.getDecoder().decode(content.getValue()));
        } else if (content.getEmbeddingMethod() == ValueEmbeddingEnumeration.URI) {
            // Value provided as URI to look up.
            try {
                var request = HttpRequest.newBuilder()
                        .uri(new URI(content.getValue()))
                        .GET()
                        .build();
                return HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString())
                        .body();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(String.format("The provided value [%s] was not a valid URI.", content.getValue()), e);
            } catch (IOException | InterruptedException e) {
                throw new IllegalArgumentException(String.format("Error while calling URI [%s]", content.getValue()), e);
            }
        } else {
            // Value provided as String.
            return content.getValue();
        }
    }

    /**
     * Get a single required input for the provided name as a string value.
     *
     * @param parameterItems The items to look through.
     * @param inputName The name of the input to look for.
     * @return The input's string value.
     */
    public String getRequiredString(List<AnyContent> parameterItems, String inputName) {
        return asString(getSingleRequiredInputForName(parameterItems, inputName));
    }

    /**
     * Get a single required input for the provided name as a AnyContent.
     *
     * @param parameterItems The items to look through.
     * @param inputName      The name of the input to look for.
     * @return The input's AnyContent.
     */
    public AnyContent getRequiredAnyContent(List<AnyContent> parameterItems, String inputName) {
        return getSingleRequiredInputForName(parameterItems, inputName);
    }

    /**
     * Get a single required input for the provided name as a binary value.
     *
     * @param parameterItems The items to look through.
     * @param inputName      The name of the input to look for.
     * @return The input's byte[] value.
     */
    public byte[] getRequiredBinary(List<AnyContent> parameterItems, String inputName) {
        var input = getSingleRequiredInputForName(parameterItems, inputName);
        if (input.getEmbeddingMethod() == null || input.getEmbeddingMethod() == ValueEmbeddingEnumeration.BASE_64) {
            // Base64 encoded string.
            return Base64.getDecoder().decode(input.getValue());
        } else if (input.getEmbeddingMethod() == ValueEmbeddingEnumeration.URI) {
            // Remote URI to read from.
            try {
                var request = HttpRequest.newBuilder()
                        .uri(new URI(input.getValue()))
                        .GET()
                        .build();
                return HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofByteArray())
                        .body();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(String.format("The provided value [%s] was not a valid URI.", input.getValue()), e);
            } catch (IOException | InterruptedException e) {
                throw new IllegalArgumentException(String.format("Error while calling URI [%s]", input.getValue()), e);
            }
        } else {
            throw new IllegalArgumentException(String.format("Input [%s] was expected to be provided as a BASE64 string or a URI.", inputName));
        }
    }

    /**
     * Get a single optional input for the provided name as a string value.
     *
     * @param parameterItems The items to look through.
     * @param inputName The name of the input to look for.
     * @return The input's string value.
     */
    public Optional<String> getOptionalString(List<AnyContent> parameterItems, String inputName) {
        var input = getSingleOptionalInputForName(parameterItems, inputName);
        return input.map(Utils.this::asString);
    }

    /**
     * Create a AnyContent object value based on the provided parameters.
     *
     * @param name The name of the value.
     * @param value The value itself.
     * @param embeddingMethod The way in which this value is to be considered.
     * @return The value.
     */
    public AnyContent createAnyContentSimple(String name, String value, ValueEmbeddingEnumeration embeddingMethod) {
        return createAnyContentSimple(name, value, embeddingMethod, null);
    }

    /**
     * Create a AnyContent object value based on the provided parameters.
     *
     * @param name The name of the value.
     * @param value The value itself.
     * @param embeddingMethod The way in which this value is to be considered.
     * @param mimeType The mime type of the content.
     * @return The value.
     */
    public AnyContent createAnyContentSimple(String name, String value, ValueEmbeddingEnumeration embeddingMethod, String mimeType) {
        AnyContent input = new AnyContent();
        input.setName(name);
        input.setValue(value);
        input.setType("string");
        input.setEmbeddingMethod(embeddingMethod);
        input.setMimeType(mimeType);
        return input;
    }

    /**
     * Parse the received SOAP headers to retrieve the "reply-to" address.
     *
     * @param context The call's context.
     * @return The header's value.
     */
    public Optional<String> getReplyToAddressFromHeaders(WebServiceContext context) {
        return getHeaderAsString(context, REPLY_TO_QNAME).map(h -> StringUtils.appendIfMissing(h, "?wsdl"));
    }

    /**
     * Parse the received SOAP headers to retrieve the test session identifier.
     *
     * @param context The call's context.
     * @return The header's value.
     */
    public Optional<String> getTestSessionIdFromHeaders(WebServiceContext context) {
        return getHeaderAsString(context, TEST_SESSION_ID_QNAME);
    }

    /**
     * Extract a value from the SOAP headers.
     *
     * @param name The name of the header to locate.
     * @param valueExtractor The function used to extract the data.
     * @return The extracted data.
     * @param <T> The type of data extracted.
     */
    private <T> T getHeaderValue(WebServiceContext context, QName name, Function<Header, T> valueExtractor) {
        return ((List<Header>) context.getMessageContext().get(Header.HEADER_LIST))
                .stream()
                .filter(header -> name.equals(header.getName())).findFirst()
                .map(valueExtractor).orElse(null);
    }

    /**
     * Get the specified header element as a string.
     *
     * @param name The name of the header element to lookup.
     * @return The text value of the element.
     */
    private Optional<String> getHeaderAsString(WebServiceContext context, QName name) {
        return Optional.ofNullable(getHeaderValue(context, name, (header) -> ((Element) header.getObject()).getTextContent().trim()));
    }

    /**
     * Pretty-print the provided JSON content.
     *
     * @param jsonContent The JSON content to process.
     * @return The pretty-printed JSON.
     */
    public String prettyPrintJson(String jsonContent) {
        try {
            var object = objectMapper.readValue(jsonContent, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.warn("Error while pretty-printing JSON.", e);
            // Just the return the string as-is.
            return jsonContent;
        }
    }

    /**
     * Method to verify that string is JsonString or not.
     *
     * @param string which needs to be verified.
     * @return boolean true/false based on string is JsonString or not.
     */
    private boolean isJSON(String string) {
        try {
            JsonParser.parseString(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to verify that string is NonNegative Integer or not.
     *
     * @param string which needs to be verified.
     * @return boolean true/false based on string is NonNegative Integer or not.
     */
    public boolean isNonNegativeIntegerPattern(String string) {
        // Regular expression to match a non-negative integer pattern
        String nonNegativeIntegerPattern = "^\\d+$"; // Only allows non-negative integers

        // Check if the string matches the non-negative integer pattern
        return string.matches(nonNegativeIntegerPattern);
    }

    /**
     * Convert jsonString => AnyConnect.
     *
     * @param variableName variable name in which JSONString should get stored.
     * @param jsonString   JSONString which needs to be converted in AnyConnect Value or Items.
     * @param mimeType     memeType if the JSONString is simple string.
     * @return AnyConnect for the given inputs.
     */
    public AnyContent getAnyConnectForString(String variableName, String jsonString, String mimeType) {
        //verify that JsonString is actually Json String or simple String
        if (isJSON(jsonString)) {
            //parse Json String to JsonElement
            JsonElement jsonElement = JsonParser.parseString(jsonString);

            if (jsonElement.isJsonObject()) {
                //JsonElement JsonObject Case

                //create AnyConnect
                AnyContent requestItem = new AnyContent();
                requestItem.setType("map");
                requestItem.setName(variableName);

                //iterate JsonObjects to fill AnyConnect Items
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                for (String key : jsonObject.keySet()) {
                    JsonElement value = jsonObject.get(key);

                    //If value is JsonObject or JsonArray then convert them to AnyConnect
                    if (value.isJsonObject() || value.isJsonArray()) {
                        requestItem.getItem().add(getAnyConnectForString(key, value.toString(), mimeType));
                    } else if (value.isJsonNull()) {
                        //If value is JsonNull then add value null
                        requestItem.getItem().add(createAnyContentSimple(key, null, ValueEmbeddingEnumeration.STRING, mimeType));
                    } else {
                        //Store Item as Simple AnyConnect which has simple ID/String Value
                        requestItem.getItem().add(createAnyContentSimple(key, value.getAsJsonPrimitive().getAsString(), ValueEmbeddingEnumeration.STRING, mimeType));
                    }
                }
                return requestItem;
            } else if (jsonElement.isJsonArray()) {
                //JsonElement JsonObject Case

                //create AnyConnect
                AnyContent requestItem = new AnyContent();
                requestItem.setType("list");
                requestItem.setName(variableName);

                //iterate jsonArray to fill AnyConnect Items and those Items key will be index number.
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonElement element = jsonArray.get(i);

                    //If value is JsonObject or JsonArray then convert them to AnyConnect
                    if (jsonElement.isJsonObject() || jsonElement.isJsonArray()) {
                        requestItem.getItem().add(getAnyConnectForString(String.valueOf(i), element.toString(), mimeType));
                    } else if (element.isJsonNull()) {
                        //If value is JsonNull then add value null
                        requestItem.getItem().add(getAnyConnectForString(String.valueOf(i), null, mimeType));
                    } else {
                        //Store Item as Simple AnyConnect which has simple ID/String Value
                        requestItem.getItem().add(getAnyConnectForString(String.valueOf(i), element.getAsJsonPrimitive().getAsString(), mimeType));
                    }
                }
                return requestItem;
            } else if (jsonElement.isJsonNull()) {
                //If value is JsonNull then create AnyConnect with null value
                return createAnyContentSimple(variableName, null, ValueEmbeddingEnumeration.STRING, mimeType);
            } else {
                //Create AnyConnect as SimpleAnyConnect which has simple ID/String Value
                return createAnyContentSimple(variableName, jsonElement.getAsJsonPrimitive().getAsString(), ValueEmbeddingEnumeration.STRING, mimeType);
            }
        } else {
            //Create AnyConnect as SimpleAnyConnect which has simple ID/String Value
            return createAnyContentSimple(variableName, jsonString, ValueEmbeddingEnumeration.STRING, mimeType);
        }
    }

    /**
     * Convert anyConnects => JsonObject.
     *
     * @param anyConnects Which needs to be converted.
     * @return JsonObject for the given inputs.
     */
    public JsonObject getJsonObjectForAnyConnects(List<AnyContent> anyConnects) {
        JsonObject jsonObject = new JsonObject();
        for (AnyContent item : anyConnects) {
            jsonObject.add(item.getName(), getJsonElementForAnyConnect(item));
        }
        return jsonObject;
    }

    /**
     * Convert anyConnects => JsonArray.
     *
     * @param anyConnects Which needs to be converted.
     * @return JsonArray for the given inputs.
     */
    public JsonArray getJsonArrayForAnyConnects(List<AnyContent> anyConnects) {
        JsonArray jsonArray = new JsonArray();
        for (AnyContent item : anyConnects) {
            jsonArray.add(getJsonElementForAnyConnect(item));
        }
        return jsonArray;
    }


    /**
     * Convert AnyConnect => JsonElement.
     *
     * @param anyConnect Which needs to be converted.
     * @return JsonElement for the given inputs.
     */
    public JsonElement getJsonElementForAnyConnect(AnyContent anyConnect) {
        if (anyConnect.getType().equals("map")) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add(anyConnect.getName(), getJsonObjectForAnyConnects(anyConnect.getItem()));
            return jsonObject;
        } else if (anyConnect.getType().equals("list")) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add(anyConnect.getName(), getJsonArrayForAnyConnects(anyConnect.getItem()));
            return jsonObject;
        } else if(asString(anyConnect) == null) {
            return new JsonNull();
        } else {
            return new JsonPrimitive(asString(anyConnect));
        }
    }


    /**
     * Add common request/response content to the given report as context items to return.
     *
     * @param report         The report to add the items to.
     * @param endpoint       The called FHIR server endpoint.
     * @param requestBody    The requestBody.
     * @param responseEntity The call responseEntity.
     * @param responseType   The responseType.
     */
    public void addCommonReportData(
            TAR report,
            String endpoint,
            String requestBody,
            ResponseEntity<String> responseEntity,
            String responseType) {
        if (endpoint != null || requestBody != null) {
            var requestItem = new AnyContent();
            requestItem.setType("map");
            requestItem.setName("request");
            if (endpoint != null) {
                requestItem.getItem().add(createAnyContentSimple("url", endpoint, ValueEmbeddingEnumeration.STRING));
            }
            if (requestBody != null) {
                requestItem.getItem().add(
                        JsonStringConverterServiceImpl.JSON_STRING.equals(responseType) ?
                                createAnyContentSimple("body",
                                        requestBody,
                                        ValueEmbeddingEnumeration.STRING,
                                        MediaType.APPLICATION_JSON_VALUE)
                                :
                                getAnyConnectForString(
                                        "body",
                                        requestBody,
                                        null)
                );
            }
            report.getContext().getItem().add(requestItem);
        }

        var responseItem = new AnyContent();
        responseItem.setType("map");
        responseItem.setName("response");
        responseItem.getItem().add(createAnyContentSimple("status", String.valueOf(responseEntity.getStatusCode().value()), ValueEmbeddingEnumeration.STRING));
        responseItem.getItem().add(
                JsonStringConverterServiceImpl.JSON_STRING.equals(responseType) ?
                        createAnyContentSimple("body",
                                responseEntity.getBody(),
                                ValueEmbeddingEnumeration.STRING,
                                MediaType.APPLICATION_JSON_VALUE)
                        :
                        getAnyConnectForString(
                                "body",
                                responseEntity.getBody(),
                                null)
        );
        report.getContext().getItem().add(responseItem);
    }

    /**
     * Add common request/response content to the given report as context items to return.
     *
     * @param report       The report to add the items to.
     * @param endpoint     The called FHIR server endpoint.
     * @param requestBody  The requestBody.
     * @param result       The call result.
     * @param responseType The responseType.
     */
    public void addCommonReportData(
            TAR report,
            String endpoint,
            String requestBody,
            MethodOutcomeResponse result,
            String responseType) {
        if (endpoint != null || requestBody != null) {
            var requestItem = new AnyContent();
            requestItem.setType("map");
            requestItem.setName("request");
            if (endpoint != null) {
                requestItem.getItem().add(createAnyContentSimple("endpoint", endpoint, ValueEmbeddingEnumeration.STRING));
            }
            if (requestBody != null) {
                requestItem.getItem().add(
                        JsonStringConverterServiceImpl.JSON_STRING.equals(responseType) ?
                                createAnyContentSimple("body",
                                        requestBody,
                                        ValueEmbeddingEnumeration.STRING,
                                        MediaType.APPLICATION_JSON_VALUE)
                                :
                                getAnyConnectForString(
                                        "body",
                                        requestBody,
                                        null)
                );
            }
            report.getContext().getItem().add(requestItem);
        }
        var responseItem = new AnyContent();
        responseItem.setType("map");
        responseItem.setName("response");
        responseItem.getItem().add(createAnyContentSimple("status", String.valueOf(result.status()), ValueEmbeddingEnumeration.STRING));

        responseItem.getItem().add(
                JsonStringConverterServiceImpl.JSON_STRING.equals(responseType) ?
                        createAnyContentSimple("body",
                                result.methodOutcomeBody(),
                                ValueEmbeddingEnumeration.STRING,
                                MediaType.APPLICATION_JSON_VALUE)
                        :
                        getAnyConnectForString(
                                "body",
                                result.methodOutcomeBody(),
                                null)
        );

        report.getContext().getItem().add(responseItem);
    }

    /**
     * Add common request/response content to the given report as context items to return.
     *
     * @param report   The report to add the items to.
     * @param endpoint The called FHIR server endpoint.
     * @param payload  The payload sent.
     * @param result   The call result.
     */
    public void addCommonReportData(TAR report, String endpoint, String payload, RequestResult result) {
        if (endpoint != null || payload != null) {
            var requestItem = new AnyContent();
            requestItem.setType("map");
            requestItem.setName("request");
            if (endpoint != null) {
                requestItem.getItem().add(createAnyContentSimple("endpoint", endpoint, ValueEmbeddingEnumeration.STRING));
            }
            if (payload != null) {
                requestItem.getItem().add(createAnyContentSimple("body", payload, ValueEmbeddingEnumeration.STRING, MediaType.APPLICATION_JSON_VALUE));
            }
            report.getContext().getItem().add(requestItem);
        }
        var responseItem = new AnyContent();
        responseItem.setType("map");
        responseItem.setName("response");
        responseItem.getItem().add(createAnyContentSimple("status", String.valueOf(result.status()), ValueEmbeddingEnumeration.STRING));

        if (result.body() != null && !result.body().isBlank()) {
            String contentType = null;
            var contentTypeHeader = result.contentType();
            if (contentTypeHeader.isPresent() && contentTypeHeader.get().toLowerCase().startsWith(fhirContentType)) {
                contentType = MediaType.APPLICATION_JSON_VALUE;
            }
            responseItem.getItem().add(createAnyContentSimple("body", result.body(), ValueEmbeddingEnumeration.STRING, contentType));
        }
        report.getContext().getItem().add(responseItem);
    }
}
