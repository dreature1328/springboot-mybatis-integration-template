package xyz.dreature.smit.component.extractor.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.Context;
import xyz.dreature.smit.component.extractor.Extractor;
import xyz.dreature.smit.service.MockService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 模拟抽取器
@Component
@Lazy
public class MockExtractor implements Extractor<JsonNode> {
    @Autowired
    private MockService mockService;

    // 模拟抽取
    @Override
    public List<JsonNode> extract(Context context, Map<String, Object> mockParams) {
        List<JsonNode> result = new ArrayList<>();
        int dataSize = (Integer) mockParams.get("dataSize");
        result.add(mockService.generateMockResponse(dataSize));
        return result;
    }

    // 模拟抽取
    @Override
    public List<JsonNode> extractBatch(Context context, List<? extends Map<String, Object>> mocksParams) {
        return mocksParams.parallelStream()
                .flatMap(mockParams -> extract(context, mockParams).stream())
                .collect(Collectors.toList());
    }
}
