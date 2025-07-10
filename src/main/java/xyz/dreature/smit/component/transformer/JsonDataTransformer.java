package xyz.dreature.smit.component.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.entity.Data;

import java.util.function.Function;

// JSON-实体转换器
@Component
public class JsonDataTransformer extends JsonTransformer<Data> {

    public JsonDataTransformer() {
        super(new Function<JsonNode, Data>() {
            @Override
            public Data apply(JsonNode itemNode) {
                return new Data(
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
