package xyz.dreature.smit.component.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import xyz.dreature.smit.common.model.context.Context;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// JSON 转换器
public abstract class JsonTransformer<T> implements Transformer<JsonNode, T> {
    protected Function<JsonNode, T> itemParser;

    public JsonTransformer(Function<JsonNode, T> itemParser) {
        this.itemParser = itemParser;
    }

    // 转换器键（用于注册）
    @Override
    public String getKey() {
        return JsonNode.class.getSimpleName() + "->" +
                ((Class) ((ParameterizedType) getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0]).getSimpleName();
    }

    // 单项转换
    @Override
    public List<T> transform(Context context, JsonNode jsonNode) {
        if (jsonNode == null) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>();
        JsonNode arrayNode = jsonNode.path("data");

        if (arrayNode.isArray()) {
            for (JsonNode itemNode : arrayNode) {
                result.add(parseItem(itemNode));
            }
        }

        return result;
    }

    // 流式转换
    @Override
    public List<T> transformStream(Context context, List<JsonNode> jsonNodes) {
        if (jsonNodes == null || jsonNodes.isEmpty()) {
            return Collections.emptyList();
        }

        return jsonNodes.parallelStream()
                .filter(Objects::nonNull)
                .flatMap(jsonNode -> {
                    JsonNode arrayNode = jsonNode.path("data");
                    return arrayNode.isArray()
                            ? StreamSupport.stream(arrayNode.spliterator(), false)
                            : Stream.empty();
                })
                .map(this::parseItem)
                .collect(Collectors.toList());
    }

    // JSON 节点映射实体类
    public T parseItem(JsonNode itemNode) {
        return itemParser.apply(itemNode);
    }

}
