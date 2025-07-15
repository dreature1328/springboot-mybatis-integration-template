package xyz.dreature.smit.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisUtils {
    // ===== 常量 / 配置 =====
    public static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static <T> T get(RedisTemplate<String, ?> redisTemplate, String key, Class<T> targetType) {
        DataType dataType = redisTemplate.type(key);
        if (dataType == null) return null;

        switch (dataType) {
            case STRING:
                Object value = redisTemplate.opsForValue().get(key);
                return convertValue(value, targetType);

            case HASH:
                if (Map.class.isAssignableFrom(targetType)) {
                    Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);
                    return (T) rawMap;
                }
                throw new IllegalArgumentException("HASH类型需要Map作为目标类型");

            case LIST:
                if (List.class.isAssignableFrom(targetType)) {
                    List<?> rawList = redisTemplate.opsForList().range(key, 0, -1);
                    return (T) rawList;
                }
                throw new IllegalArgumentException("LIST类型需要List作为目标类型");

            case SET:
                if (Set.class.isAssignableFrom(targetType)) {
                    Set<?> rawSet = redisTemplate.opsForSet().members(key);
                    return (T) rawSet;
                }
                throw new IllegalArgumentException("SET类型需要Set作为目标类型");

            case ZSET:
                if (Set.class.isAssignableFrom(targetType)) {
                    Set<?> rawSet = redisTemplate.opsForZSet().range(key, 0, -1);
                    return (T) rawSet;
                }
                throw new IllegalArgumentException("ZSET类型需要Set作为目标类型");

            default:
                throw new UnsupportedOperationException("不支持的数据类型: " + dataType);
        }
    }

    // 获取值作为 JSON
    public static JsonNode getAsJson(RedisTemplate<String, ?> redisTemplate, String key) throws IOException {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;

        if (value instanceof String) {
            return JsonUtils.DEFAULT_MAPPER.readTree((String) value);
        } else if (value instanceof byte[]) {
            return JsonUtils.DEFAULT_MAPPER.readTree((byte[]) value);
        } else {
            // 对于其他类型，先序列化为JSON字符串再解析
            String json = JsonUtils.DEFAULT_MAPPER.writeValueAsString(value);
            return JsonUtils.DEFAULT_MAPPER.readTree(json);
        }
    }

    // 获取值作为字符串
    public static String getAsString(RedisTemplate<String, ?> redisTemplate, String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;

        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof byte[]) {
            return new String((byte[]) value, DEFAULT_CHARSET);
        } else {
            return value.toString();
        }
    }

    // ===== 类型转换 =====
    public static <T> T convertValue(Object value, Class<T> targetType) {
        if (value == null) return null;

        // 如果已经是目标类型，直接返回
        if (targetType.isInstance(value)) {
            return (T) value;
        }

        // 特殊处理常见类型
        if (targetType == String.class) {
            return (T) value.toString();
        }

        try {
            return JsonUtils.DEFAULT_MAPPER.convertValue(value, targetType);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("类型转换失败", e);
        }
    }

    public static <K, T> Map<K, T> convertMapValues(Map<K, Object> rawMap, Class<T> valueType) {
        if (rawMap == null) return null;
        return rawMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> convertValue(entry.getValue(), valueType)
                ));
    }

    public static <T> List<T> convertListValues(List<Object> rawList, Class<T> targetType) {
        if (rawList == null) return null;
        return rawList.stream()
                .map(value -> convertValue(value, targetType))
                .collect(Collectors.toList());
    }

    public static <T> Set<T> convertSetValues(Set<Object> rawSet, Class<T> targetType) {
        if (rawSet == null) return null;
        return rawSet.stream()
                .map(value -> convertValue(value, targetType))
                .collect(Collectors.toSet());
    }
}
