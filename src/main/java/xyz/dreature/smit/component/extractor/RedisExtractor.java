package xyz.dreature.smit.component.extractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.EtlContext;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RedisExtractor<S> implements Extractor<S> {
    // 策略常量
    public static final String STRATEGY_STRING = "redis:string";
    public static final String STRATEGY_STRINGS = "redis:strings";
    public static final String STRATEGY_HASH = "redis:hash";
    public static final String STRATEGY_HASH_FIELD = "redis:hash:field";
    public static final String STRATEGY_LIST = "redis:list";
    public static final String STRATEGY_SET = "redis:set";
    public static final String STRATEGY_ZSET = "redis:zset";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 单项抽取
    public List<S> extract(EtlContext context, Map<String, ?> opParams) {
        String strategy = context.getExtractStrategy().toLowerCase();
        String key = getRequiredParam(opParams, "key", strategy);

        switch (strategy) {
            case STRATEGY_STRING:
                return createResultList(redisTemplate.opsForValue().get(key));

            case STRATEGY_HASH:
                return createResultList(redisTemplate.opsForHash().entries(key));

            case STRATEGY_HASH_FIELD:
                String field = getRequiredParam(opParams, "field", strategy);
                return createResultList(redisTemplate.opsForHash().get(key, field));

            case STRATEGY_LIST:
                long start = getLongParam(opParams, "start", 0L);
                long end = getLongParam(opParams, "end", -1L);
                List<Object> list = redisTemplate.opsForList().range(key, start, end);
                return list != null ? (List<S>) list : new ArrayList<>();

            case STRATEGY_SET:
                Set<Object> set = redisTemplate.opsForSet().members(key);
                return set != null ? new ArrayList<>((Set<S>) set) : new ArrayList<>();

            case STRATEGY_ZSET:
                double minScore = getDoubleParam(opParams, "minScore", 0.0);
                double maxScore = getDoubleParam(opParams, "maxScore", Double.MAX_VALUE);
                Set<Object> zset = redisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore);
                return zset != null ? new ArrayList<>((Set<S>) zset) : new ArrayList<>();

            default:
                throw new IllegalArgumentException("不支持的抽取策略: " + strategy);
        }
    }

    // 单批抽取
    public List<S> extractBatch(EtlContext context, List<? extends Map<String, ?>> opsParams) {
        if (opsParams == null || opsParams.isEmpty()) {
            return new ArrayList<>();
        }

        String strategy = context.getExtractStrategy().toLowerCase();

        switch (strategy) {
            case STRATEGY_STRING:
                List<String> singleKeys = opsParams.stream()
                        .map(p -> getRequiredParam(p, "key", strategy))
                        .collect(Collectors.toList());
                List<Object> values = redisTemplate.opsForValue().multiGet(singleKeys);
                return values != null ? (List<S>) values : new ArrayList<>();

            case STRATEGY_STRINGS:
                List<String> multiKeys = opsParams.stream()
                        .flatMap(p -> {
                            String keysParam = getRequiredParam(p, "keys", strategy);
                            return Arrays.stream(keysParam.split(","))
                                    .map(String::trim)
                                    .filter(k -> !k.isEmpty());
                        })
                        .collect(Collectors.toList());
                List<Object> multiValues = redisTemplate.opsForValue().multiGet(multiKeys);
                return multiValues != null ? (List<S>) multiValues : new ArrayList<>();

            case STRATEGY_HASH_FIELD:
                return opsParams.stream()
                        .map(p -> {
                            String key = getRequiredParam(p, "key", strategy);
                            String field = getRequiredParam(p, "field", strategy);
                            return redisTemplate.opsForHash().get(key, field);
                        })
                        .filter(Objects::nonNull)
                        .map(v -> (S) v)
                        .collect(Collectors.toList());

            default:
                // 通用批量处理方案
                return opsParams.stream()
                        .flatMap(p -> extract(context, p).stream())
                        .collect(Collectors.toList());
        }
    }

    // 创建可变结果列表
    private List<S> createResultList(Object value) {
        List<S> result = new ArrayList<>();
        result.add((S) value);
        return result;
    }

    // 参数验证
    private String getRequiredParam(Map<String, ?> params, String paramName, String strategy) {
        return Optional.ofNullable(params.get(paramName))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("策略[%s]需要参数: %s", strategy, paramName)));
    }

    // 获取长整型参数
    private long getLongParam(Map<String, ?> params, String paramName, long defaultValue) {
        return Optional.ofNullable(params.get(paramName))
                .map(o -> Long.parseLong(o.toString()))
                .orElse(defaultValue);
    }

    // 获取双精浮点数参数
    private double getDoubleParam(Map<String, ?> params, String paramName, double defaultValue) {
        return Optional.ofNullable(params.get(paramName))
                .map(o -> Double.parseDouble(o.toString()))
                .orElse(defaultValue);
    }
}
