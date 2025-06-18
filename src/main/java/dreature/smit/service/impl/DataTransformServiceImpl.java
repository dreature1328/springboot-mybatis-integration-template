package dreature.smit.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dreature.smit.common.util.JsonUtils;
import dreature.smit.entity.Data;
import dreature.smit.service.TransformService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static dreature.smit.common.util.BatchUtils.mapEach;

@Service
public class DataTransformServiceImpl extends BaseServiceImpl<Data> implements TransformService<Data> {
    // ----- 数据转换 -----
    // 单项转换
    public List<Data> transform(String response) {
        List<Data> result = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode arrayNode = rootNode.path("data");

            if (arrayNode.isArray()) {
                for (JsonNode jsonNode : arrayNode) {
                    result.add(new Data(
                            jsonNode.path("id").asText(),
                            jsonNode.path("key1").asText(),
                            jsonNode.path("key2").asText()
                    ));
                }
            }
        } catch (JsonProcessingException e) {
            System.err.println("JSON 处理异常: " + e.getMessage());
        }

        return result;
    }

    // 逐项转换
    public List<Data> transform(String... responses) {
        return mapEach(new ArrayList<>(Arrays.asList(responses)), this::transform);
    }

    // 流水线转换
    public List<Data> transformPipeline(List<String> responses) {
        return responses.stream()
                .map(JsonUtils::stringToJsonNode)
                .filter(Objects::nonNull)
                .flatMap(jsonObj -> {
                    JsonNode arrayNode = jsonObj.path("data");
                    return arrayNode.isArray()
                            ? StreamSupport.stream(arrayNode.spliterator(), false)
                            : Stream.empty();
                })
                .map(jsonNode -> new Data(
                        jsonNode.path("id").asText(),
                        jsonNode.path("key1").asText(),
                        jsonNode.path("key2").asText()
                ))
                .collect(Collectors.toList());
    }
}
