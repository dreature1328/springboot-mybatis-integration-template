package xyz.dreature.smit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.common.model.metrics.EtlMetrics;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.orchestration.EtlOrchestrator;
import xyz.dreature.smit.service.MockService;

import java.util.List;
import java.util.Map;

// 测试接口（数据集成）
@RestController
@RequestMapping("/etl")
public class EtlController {
    @Autowired
    MockService mockService;

    @Autowired
    @Qualifier("mockToDbOrch")
    EtlOrchestrator<JsonNode, Data, Long> mockToDbOrch;

    @Autowired
    @Qualifier("jsonFileToDbOrch")
    EtlOrchestrator<JsonNode, Data, Long> jsonFileToDbOrch;

    @Autowired
    @Qualifier("xmlFileToDbOrch")
    EtlOrchestrator<JsonNode, Data, Long> xmlFileToDbOrch;

    @Autowired
    @Qualifier("dbToDbOrch")
    EtlOrchestrator<Data, Data, Long> dbToDbOrch;

    @Autowired
    @Qualifier("mqToDbOrch")
    private EtlOrchestrator mqToDbOrch;

    // ===== 模拟数据源集成 =====
    // 单次集成
    @RequestMapping("/mock/run")
    public ResponseEntity<Result<String>> mockRun(
            @RequestParam(name = "data-size", defaultValue = "10") int dataSize
    ) {
        EtlContext etlContext = new EtlContext();
        EtlMetrics stats = mockToDbOrch.run(etlContext, mockService.generateMockParams(dataSize));
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 单次集成（内部分批）
    @RequestMapping("/mock/run-batch")
    public ResponseEntity<Result<String>> mockRunBatch(
            @RequestParam(name = "data-size", defaultValue = "10") int dataSize
    ) {
        EtlContext etlContext = new EtlContext();
        EtlMetrics stats = mockToDbOrch.runBatch(etlContext, mockService.generateMockParams(dataSize));
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // ===== 文件数据源集成 =====
    // 单次集成
    @RequestMapping("/file/run")
    public ResponseEntity<Result<String>> fileRun(
            @RequestBody List<? extends Map<String, ?>> filesParams
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setExtractStrategy("file:json");
        EtlMetrics stats = xmlFileToDbOrch.run(etlContext, filesParams);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 单次集成（内部分批）
    @RequestMapping("/file/run-batch")
    public ResponseEntity<Result<String>> fileRunBatch(
            @RequestBody List<? extends Map<String, ?>> filesParams
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setExtractStrategy("file:json");
        EtlMetrics stats = jsonFileToDbOrch.runBatch(etlContext, filesParams);
//        etlContext.setExtractStrategy("file:xml");
//        EtlMetrics stats = xmlFileToDbOrch.runBatch(etlContext, filesParams);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // ===== 数据库数据源集成 =====
    // 单次集成
    @RequestMapping("/db/run")
    public ResponseEntity<Result<String>> dbRun(
            @RequestBody List<? extends Map<String, ?>> filesParams
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setExtractStrategy("db:id");
        etlContext.setLoadStrategy("db:upsert");
        EtlMetrics stats = dbToDbOrch.run(etlContext, filesParams);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 单次集成（内部分批）
    @RequestMapping("/db/run-batch")
    public ResponseEntity<Result<String>> dbRunBatch(
            @RequestBody List<? extends Map<String, ?>> filesParams
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setExtractStrategy("db:id");
        etlContext.setLoadStrategy("db:upsert");
        EtlMetrics stats = dbToDbOrch.runBatch(etlContext, filesParams);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // ===== 消息队列数据源集成 =====
    // 单次集成
    @RequestMapping("/mq/run")
    public ResponseEntity<Result<String>> mqRun(
            @RequestBody List<? extends Map<String, ?>> filesParams
    ) {
        EtlContext etlContext = new EtlContext();
        EtlMetrics stats = mqToDbOrch.run(etlContext, filesParams);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 单次集成（内部分批）
    @RequestMapping("/mq/run-batch")
    public ResponseEntity<Result<String>> mqRunBatch(
            @RequestBody List<? extends Map<String, ?>> filesParams
    ) {
        EtlContext etlContext = new EtlContext();
        EtlMetrics stats = mqToDbOrch.runBatch(etlContext, filesParams);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }
}
