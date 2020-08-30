package com.observe.shorturl.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public static String getJsonFromObject(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writer().withDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException exception occurred while paring object to json string : ", e);
            return null;
        }
    }

    public static <T> T getObjectFromJsonStr(String jsonStr, Class<T> obj) {
        log.debug("getObjectFromJsonStr method called.");
        if (jsonStr != null && obj != null) {
            try {
                return OBJECT_MAPPER.readValue(jsonStr, obj);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException exception occured while paring json string to java object {}: ", e);
                return null;
            } catch (IOException io) {
                log.error("IOException exception occured while paring json string to java object : {}", io);
                return null;
            }
        }
        return null;
    }

    public static <T> List<T> getListObject(String jsonString, Class<T> objectType) {
        if (jsonString != null) {
            try {
                return OBJECT_MAPPER.readValue(jsonString,
                        TypeFactory.defaultInstance().constructCollectionType(List.class, objectType));

            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException exception occured while paring json string to java object {}: ", e);
                return null;
            } catch (IOException io) {
                log.error("IOException exception occured while paring json string to java object : {}", io);
                return null;
            }
        }
        return null;
    }

    public static <K, V> Map<K, V> getMapObject(String jsonString, Class<K> keyType, Class<V> valueType) {
        if (jsonString != null) {
            try {
                JavaType keyJavaType = TypeFactory.defaultInstance().constructType(keyType);
                JavaType valueJavaType = TypeFactory.defaultInstance().constructType(valueType);
                return OBJECT_MAPPER.readValue(jsonString, TypeFactory.defaultInstance().constructMapType(Map.class, keyJavaType, valueJavaType));
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException exception occured while paring json string to java object {}: ", e);
                return null;
            } catch (IOException io) {
                log.error("IOException exception occured while paring json string to java object : {}", io);
                return null;
            }
        }
        return null;
    }

    public static <T> Collection<T> getCollectionObject(String jsonString, Class<? extends Collection> collectionType, Class<T> objectType) {
        if (jsonString != null) {
            try {
                return OBJECT_MAPPER.readValue(jsonString,
                        TypeFactory.defaultInstance().constructCollectionType(collectionType, objectType));

            } catch (Exception e) {
                log.error(" exception Occurred while paring json string to java object {}: ", e);
                return null;
            }
        }
        return null;
    }

    public static JsonNode getJasonNode(String jsonString) {
        log.debug("getJasonNode() method called with arguments :{}", jsonString);
        try {
            return StringUtils.isEmpty(jsonString) ? null : OBJECT_MAPPER.readTree(jsonString);
        } catch (Exception e) {
            log.error("Cannot Convert Json String  to Json Object" + e);
        }
        return null;
    }

    public static JsonNode getJsonNode(Object object) {
        return OBJECT_MAPPER.convertValue(object, JsonNode.class);
    }

    public static String getValueByKeyFromJsonString(String jsonStr, String key) {
        JsonNode jsonNode = getJasonNode(jsonStr);
        if (jsonNode != null && !jsonNode.isNull()) {
            JsonNode jsonNodeKey = getValueFromObject(jsonNode, key);
            if (jsonNodeKey != null && !jsonNodeKey.isNull()) {
                return jsonNodeKey.asText();
            }
        }
        return null;
    }

    public static JsonNode getValueFromObject(JsonNode rootNode, String s) {
        log.debug("getValueFromObject method called with jsonNode :{} and Path : {}", rootNode, s);
        if (rootNode.isNull())
            return rootNode;
        JsonNode node = null;
        try {
            node = rootNode;
            JsonPointer ptr = JsonPointer.compile("/" + s);
            node = node.at(ptr);
            if (node.isMissingNode()) {
                log.error("(\"" + ptr + "\") Path Doesn't exists in the JsonNode --> " + rootNode);
                node = null;
            }
        } catch (Exception e) {
            log.error("Exception during the getValueFromObject is {}", e);
            node = null;
        }
        return node;
    }

    public static JsonNode getValueFromObject(JsonNode rootNode, String s, char separator) {
        log.debug("getValueFromObject() method called with jsonNode :{} and Path : {} and separator: {}", rootNode, s,
                separator);
        if (rootNode.isNull())
            return rootNode;

        JsonNode node = null;
        try {
            node = rootNode;
            String path = s.replace(separator, '/');
            JsonPointer ptr = JsonPointer.compile("/" + path);
            node = node.at(ptr);
            if (node.isMissingNode()) {
                log.error("(\"" + ptr + "\") Path Doesn't exists in the JsonNode --> " + rootNode);
            }
        } catch (Exception e) {
            log.error("Exception during the getValueFromObject is {}", e);
            node = null;
        }
        return node;
    }


}
