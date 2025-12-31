package xyz.dreature.smit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.common.model.metrics.EtlMetrics;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.orchestration.EtlOrchestrator;
import xyz.dreature.smit.service.MockService;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

// 测试接口（数据集成）
@Slf4j
@RestController
@RequestMapping("/etl")
@Validated
public class EtlController {
    @Autowired
    private MockService mockService;

    @Autowired
    @Qualifier("mockToDbOrch")
    private EtlOrchestrator<JsonNode, Data, Long> mockToDbOrch;

    @Autowired
    @Qualifier("jsonFileToDbOrch")
    private EtlOrchestrator<JsonNode, Data, Long> jsonFileToDbOrch;

    @Autowired
    @Qualifier("xmlFileToDbOrch")
    private EtlOrchestrator<Document, Data, Long> xmlFileToDbOrch;

    @Autowired
    @Qualifier("dbToDbOrch")
    private EtlOrchestrator<Data, Data, Long> dbToDbOrch;

    @Autowired
    @Qualifier("mqToDbOrch")
    private EtlOrchestrator<Data, Data, Long> mqToDbOrch;

    // ===== 模拟数据源集成 =====
    // 单次集成
    @RequestMapping("/mock/run")
    public ResponseEntity<Result<String>> mockRun(
            @RequestParam(name = "data-size", defaultValue = "10")
            @Positive(message = "数据大小必须为正")
            int dataSize
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setLoadStrategy("db:upsert");
        EtlMetrics stats = mockToDbOrch.run(etlContext, mockService.generateMockParams(dataSize));
        log.info("模拟数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 单次集成（内部分批）
    @RequestMapping("/mock/run-batch")
    public ResponseEntity<Result<String>> mockRunBatch(
            @RequestParam(name = "data-size", defaultValue = "10")
            @Positive(message = "数据大小必须为正")
            int dataSize
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setLoadStrategy("db:upsert");
        EtlMetrics stats = mockToDbOrch.runBatch(etlContext, mockService.generateMockParams(dataSize));
        log.info("模拟数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // ===== 文件数据源集成 =====
    // 单次集成
    @RequestMapping("/file/run")
    public ResponseEntity<Result<String>> fileRun(
            @RequestBody
            @NotEmpty(message = "文件参数不能为空")
            List<? extends Map<String, ?>>
                    filesParams
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setExtractStrategy("file:json");
        etlContext.setLoadStrategy("db:upsert");
        EtlMetrics stats = jsonFileToDbOrch.run(etlContext, filesParams);
        log.info("文件数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 单次集成（内部分批）
    @RequestMapping("/file/run-batch")
    public ResponseEntity<Result<String>> fileRunBatch(
            @RequestBody
            @NotEmpty(message = "文件参数不能为空")
            List<? extends Map<String, ?>> filesParams
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setExtractStrategy("file:json");
        etlContext.setLoadStrategy("db:upsert");
        EtlMetrics stats = jsonFileToDbOrch.runBatch(etlContext, filesParams);
//        etlContext.setExtractStrategy("file:xml");
//        EtlMetrics stats = xmlFileToDbOrch.runBatch(etlContext, filesParams);
        log.info("文件数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // ===== 数据库数据源集成 =====
    // 单次集成
    @RequestMapping("/db/run")
    public ResponseEntity<Result<String>> dbRun(
            @RequestBody
            @NotEmpty(message = "数据库参数不能为空")
            List<? extends Map<String, ?>> queriesParams
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setExtractStrategy("db:id");
        etlContext.setLoadStrategy("db:upsert");
        EtlMetrics stats = dbToDbOrch.run(etlContext, queriesParams);
        log.info("数据库数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 单次集成（内部分批）
    @RequestMapping("/db/run-batch")
    public ResponseEntity<Result<String>> dbRunBatch(
            @RequestBody
            @NotEmpty(message = "数据库参数不能为空")
            List<? extends Map<String, ?>> queriesParams
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setExtractStrategy("db:id");
        etlContext.setLoadStrategy("db:upsert");
        EtlMetrics stats = dbToDbOrch.runBatch(etlContext, queriesParams);
        log.info("数据库数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // ===== 消息队列数据源集成 =====
    // 单次集成
    @RequestMapping("/mq/run")
    public ResponseEntity<Result<String>> mqRun(
            @RequestBody
            @NotEmpty(message = "消息队列参数不能为空")
            List<? extends Map<String, ?>> params
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setLoadStrategy("db:upsert");
        EtlMetrics stats = mqToDbOrch.run(etlContext, params);
        log.info("消息队列数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 单次集成（内部分批）
    @RequestMapping("/mq/run-batch")
    public ResponseEntity<Result<String>> mqRunBatch(
            @RequestBody
            @NotEmpty(message = "消息队列参数不能为空")
            List<? extends Map<String, ?>> params
    ) {
        EtlContext etlContext = new EtlContext();
        etlContext.setLoadStrategy("db:upsert");
        EtlMetrics stats = mqToDbOrch.runBatch(etlContext, params);
        log.info("消息队列数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }
}
