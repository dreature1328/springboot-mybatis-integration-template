package xyz.dreature.smit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.common.util.BatchUtils;
import xyz.dreature.smit.service.ApiService;

import java.util.List;
import java.util.Map;

// 测试接口（API 操作）
@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private ApiService<JsonNode> apiService;

    // ===== API 抽取 =====
    // 依次同步请求
    @RequestMapping("/call")
    public ResponseEntity<Result<List<JsonNode>>> call(
            @RequestParam(name = "params") List<Map<String, String>> requestsParams
    ) {
        int total = requestsParams.size();
        List<JsonNode> responses = BatchUtils.mapEach(requestsParams, apiService::call);
        int resultCount = responses.size();
        String message = String.format("依次同步发送 %d 次请求，收到 %d 条响应", total, resultCount);
        return ResponseEntity.ok().body(Result.success(message, responses));
    }

    // 分批异步请求
    @RequestMapping("/call-batch")
    public ResponseEntity<Result<List<JsonNode>>> callBatch(
            @RequestParam(name = "params") List<Map<String, String>> requestsParams,
            @RequestParam(name = "batch-size", defaultValue = "200") int batchSize
    ) {
        int total = requestsParams.size();
        List<JsonNode> responses = apiService.callBatch(requestsParams, batchSize);
        int resultCount = responses.size();
        String message = String.format("分批异步发送 %d 次请求，收到 %d 条响应", total, resultCount);
        return ResponseEntity.ok().body(Result.success(message, responses));
    }
}
