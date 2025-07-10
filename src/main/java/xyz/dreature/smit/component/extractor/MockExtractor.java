package xyz.dreature.smit.component.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.service.MockService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 模拟抽取器
@Component
public class MockExtractor implements Extractor<JsonNode> {
    @Autowired
    private MockService mockService;

    // 模拟抽取
    public List<JsonNode> extract(EtlContext context, Map<String, ?> params) {
        List<JsonNode> result = new ArrayList<>();
        int dataSize = (Integer) params.get("dataSize");
        result.add(mockService.generateMockResponse(dataSize));
        return result;
    }
}
