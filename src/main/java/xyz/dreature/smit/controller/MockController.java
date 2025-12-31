package xyz.dreature.smit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.service.MockService;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

// 测试接口（模拟操作）
@Slf4j
@RestController
@RequestMapping("/mock")
@Validated
public class MockController {
    @Autowired
    MockService mockService;

    // 生成模拟数据
    @RequestMapping("/generate-data")
    public ResponseEntity<Result<List<Data>>> generateMockData(
            @RequestParam(name = "count", defaultValue = "10")
            @Positive(message = "生成数量必须为正")
            int count
    ) {
        List<Data> result = mockService.generateMockData(count);
        int resultCount = result.size();
        String message = String.format("生成 %d 条数据", resultCount);
        log.info("模拟数据生成完成，生成：{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 生成模拟参数
    @RequestMapping("/generate-params")
    public ResponseEntity<Result<List<Map<String, ?>>>> generateMockParams(
            @RequestParam(name = "count", defaultValue = "10")
            @Positive(message = "生成数量必须为正")
            int count
    ) {
        List<Map<String, ?>> result = mockService.generateMockParams(count);
        int resultCount = result.size();
        String message = String.format("生成 %d 组参数", resultCount);
        log.info("模拟参数生成完成，生成：{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 生成模拟响应
    @RequestMapping("/generate-responses")
    public ResponseEntity<Result<List<JsonNode>>> generateMockResponses(
            @RequestParam(name = "count", defaultValue = "10")
            @Positive(message = "生成数量必须为正")
            int count,

            @RequestParam(name = "data-size", defaultValue = "10")
            @Positive(message = "数据大小必须为正")
            int dataSize
    ) {
        List<JsonNode> result = mockService.generateMockResponses(count, dataSize);
        int resultCount = result.size();
        String message = String.format("生成 %d 条响应", resultCount);
        log.info("模拟响应生成完成，生成：{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }
}
