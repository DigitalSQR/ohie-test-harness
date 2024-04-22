
package com.argusoft.path.tht.testcasemanagement.testbed.dto.start.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "embeddingMethod",
    "value",
    "type",
    "encoding",
    "item"
})
@Generated("jsonschema2pojo")
public class AnyContent {

    @JsonProperty("name")
    private String name;
    @JsonProperty("embeddingMethod")
    private EmbeddingMethod embeddingMethod;
    @JsonProperty("value")
    private String value;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("encoding")
    private String encoding;
    @JsonProperty("item")
    private List<AnyContent> item;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("embeddingMethod")
    public EmbeddingMethod getEmbeddingMethod() {
        return embeddingMethod;
    }

    @JsonProperty("embeddingMethod")
    public void setEmbeddingMethod(EmbeddingMethod embeddingMethod) {
        this.embeddingMethod = embeddingMethod;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("type")
    public Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Type type) {
        this.type = type;
    }

    @JsonProperty("encoding")
    public String getEncoding() {
        return encoding;
    }

    @JsonProperty("encoding")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @JsonProperty("item")
    public List<AnyContent> getItem() {
        return item;
    }

    @JsonProperty("item")
    public void setItem(List<AnyContent> item) {
        this.item = item;
    }

    @Generated("jsonschema2pojo")
    public enum EmbeddingMethod {

        STRING("STRING"),
        BASE_64("BASE64"),
        URI("URI");
        private final String value;
        private final static Map<String, EmbeddingMethod> CONSTANTS = new HashMap<String, EmbeddingMethod>();

        static {
            for (EmbeddingMethod c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        EmbeddingMethod(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static EmbeddingMethod fromValue(String value) {
            EmbeddingMethod constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    @Generated("jsonschema2pojo")
    public enum Type {

        STRING("string"),
        NUMBER("number"),
        BOOLEAN("boolean"),
        BINARY("binary"),
        OBJECT("object"),
        SCHEMA("schema"),
        MAP("map"),
        LIST("list");
        private final String value;
        private final static Map<String, Type> CONSTANTS = new HashMap<String, Type>();

        static {
            for (Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Type fromValue(String value) {
            Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
