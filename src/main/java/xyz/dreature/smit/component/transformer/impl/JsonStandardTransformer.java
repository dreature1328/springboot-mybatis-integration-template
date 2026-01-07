package xyz.dreature.smit.component.transformer.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.entity.db1.StandardEntity;
import xyz.dreature.smit.component.transformer.JsonTransformer;

import java.util.function.Function;

// JSON-标准实体转换器
@Component
public class JsonStandardTransformer extends JsonTransformer<StandardEntity> {
    public JsonStandardTransformer() {
        super(new Function<JsonNode, StandardEntity>() {
            @Override
            public StandardEntity apply(JsonNode itemNode) {
                return new StandardEntity(
                        // 解析常规字段
                        itemNode.path("id").asLong(),
                        itemNode.path("numericValue").asInt(),
                        itemNode.path("decimalValue").asDouble(),
                        itemNode.path("textContent").asText(),
                        itemNode.path("activeFlag").asBoolean()
                );
            }
        });
    }
}
