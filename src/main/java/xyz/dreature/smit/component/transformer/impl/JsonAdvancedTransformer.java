package xyz.dreature.smit.component.transformer.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.entity.db2.AdvancedEntity;
import xyz.dreature.smit.component.transformer.JsonTransformer;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// JSON-高级实体转换器
@Component
public class JsonAdvancedTransformer extends JsonTransformer<AdvancedEntity> {
    @Autowired
    private ObjectMapper objectMapper;
    private DateTimeFormatter dateTimeFormatter;

    public JsonAdvancedTransformer() {
        // 为解决父类型构造函数 super() 必须首置的问题，此处暂时传 null，成员初始化交由 @PostConstruct 方法完成
        super(null);
    }

    @PostConstruct
    public void init() {
        this.dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        this.itemParser = createItemParser();
    }

    private Function<JsonNode, AdvancedEntity> createItemParser() {
        return itemNode -> {
            // 解析常规字段
            Long id = itemNode.path("id").asLong();
            String code = itemNode.path("code").asText();
            String name = itemNode.path("name").asText();
            Integer status = itemNode.path("status").asInt();

            // 解析 JSON 字段
            Map<String, Object> attributes = null;
            JsonNode attributesNode = itemNode.path("attributes");
            if (!attributesNode.isMissingNode() && !attributesNode.isNull()) {
                attributes = objectMapper.convertValue(attributesNode, HashMap.class);
            }

            // 解析数组字段
            String[] tags = null;
            JsonNode tagsNode = itemNode.path("tags");
            if (tagsNode.isArray() && tagsNode.size() > 0) {
                tags = new String[tagsNode.size()];
                for (int i = 0; i < tagsNode.size(); i++) {
                    tags[i] = tagsNode.get(i).asText();
                }
            }

            // 解析时间字段
            LocalDateTime createdAt = null;
            LocalDateTime updatedAt = null;

            String createdAtStr = itemNode.path("createdAt").asText(null);
            if (createdAtStr != null) {
                createdAt = LocalDateTime.parse(createdAtStr, dateTimeFormatter);
            }

            String updatedAtStr = itemNode.path("updatedAt").asText(null);
            if (updatedAtStr != null) {
                updatedAt = LocalDateTime.parse(updatedAtStr, dateTimeFormatter);
            }

            return new AdvancedEntity(id, code, name, status, attributes, tags, createdAt, updatedAt);
        };
    }
}
