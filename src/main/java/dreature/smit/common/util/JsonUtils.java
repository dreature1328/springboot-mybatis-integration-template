package dreature.smit.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JsonUtils {

    public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final ObjectMapper DEFAULT_MAPPER = createDefaultMapper();

    public static final ObjectMapper SNAKE_CASE_MAPPER = createSnakeCaseMapper();

    private static ObjectMapper createDefaultMapper() {
        return JsonMapper.builder()
                .serializationInclusion(JsonInclude.Include.ALWAYS)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .defaultDateFormat(new StdDateFormat().withColonInTimeZone(true))
                .build();
    }

    private static ObjectMapper createSnakeCaseMapper() {
        return JsonMapper.builder()
                .serializationInclusion(JsonInclude.Include.ALWAYS)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .defaultDateFormat(new StdDateFormat().withColonInTimeZone(true))
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
    }

    public static JsonNode parseFile(String filePath) {
        try {
            return DEFAULT_MAPPER.readTree(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String objectToJson(Object object) {
        if (object == null) return null;
        if (object instanceof String) return (String) object;

        try {
            return DEFAULT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String objectToPrettyJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return object instanceof String ? (String) object : DEFAULT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String objectToSnakeCaseJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = SNAKE_CASE_MAPPER;
            return object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static void objectToJsonFile(File file, Object object){
        if (object == null){
            return;
        }
        try {
            DEFAULT_MAPPER.writeValue(file, object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonNode stringToJsonNode(String str) {
        try {
            return DEFAULT_MAPPER.readTree(str);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) json : DEFAULT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T snakeCaseJsonToObject(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json) || clazz == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = SNAKE_CASE_MAPPER;
            return clazz.equals(String.class) ? (T) json : objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T jsonToTypedObject(String json, TypeReference<T> typeRef) {
        if (StringUtils.isEmpty(json) || typeRef == null) {
            return null;
        }
        try {
            return DEFAULT_MAPPER.readValue(json, typeRef);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> List<T> snakeCaseJsonToTypedObject(String json, TypeReference<List<T>> typeRef) {
        if (StringUtils.isEmpty(json) || typeRef == null) {
            return null;
        }
        try {
            return SNAKE_CASE_MAPPER.readValue(json, typeRef);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T jsonToParametricObject(String str, Class<?> collectionClazz, Class<?>... elementClazzes) {
        JavaType javaType = DEFAULT_MAPPER.getTypeFactory().constructParametricType(collectionClazz, elementClazzes);
        try {
            return DEFAULT_MAPPER.readValue(str, javaType);
        } catch (IOException e) {
            return null;
        }
    }

    // 打印 JSON
    public static void printJson(JsonNode jsonNode, int level) {
        if (jsonNode != null) {
            jsonNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                String indent = String.join("", Collections.nCopies(level, "\t")); // 缩进

                if (value.isObject()) {
                    // 嵌套对象
                    System.out.println(indent + key + " = {");
                    printJson(value, level + 1); // 增加嵌套层级
                    System.out.println(indent + "}");
                } else {
                    // 非嵌套对象
                    System.out.println(indent + key + " = " + value.toString());
                }
            });
        }
    }
}

