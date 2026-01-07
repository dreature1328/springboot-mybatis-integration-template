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
import xyz.dreature.smit.common.model.context.Context;
import xyz.dreature.smit.common.model.entity.db1.StandardEntity;
import xyz.dreature.smit.common.model.entity.db2.AdvancedEntity;
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
    @Qualifier("mock->db1")
    private EtlOrchestrator<JsonNode, StandardEntity, Long> orch0;

    @Autowired
    @Qualifier("file11->db1")
    private EtlOrchestrator<JsonNode, StandardEntity, Long> orch1;

    @Autowired
    @Qualifier("file12->db1")
    private EtlOrchestrator<Document, StandardEntity, Long> orch2;

    @Autowired
    @Qualifier("file21->db2")
    private EtlOrchestrator<JsonNode, AdvancedEntity, Long> orch3;

    @Autowired
    @Qualifier("file22->db2")
    private EtlOrchestrator<Document, AdvancedEntity, Long> orch4;

    @Autowired
    @Qualifier("db1->db1")
    private EtlOrchestrator<StandardEntity, StandardEntity, Long> orch5;

    @Autowired
    @Qualifier("mq->db1")
    private EtlOrchestrator<StandardEntity, StandardEntity, Long> orch6;

    // ===== 模拟数据源集成 =====
    // 单次集成
    @RequestMapping("/mock/run")
    public ResponseEntity<Result<String>> mockRun(
            @RequestParam(name = "data-size", defaultValue = "10")
            @Positive(message = "数据大小必须为正")
            int dataSize
    ) {
        Context context = new Context();
        context.setSourceDataSource("mock");
        context.setTargetDataSource("db1");
        context.setLoadStrategy("db:upsert");
        EtlMetrics stats = orch0.run(context, mockService.generateMockParams(dataSize));
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
        Context context = new Context();
        context.setSourceDataSource("mock");
        context.setTargetDataSource("db1");
        context.setLoadStrategy("db:upsert");
        EtlMetrics stats = orch0.runBatch(context, mockService.generateMockParams(dataSize));
        log.info("模拟数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // ===== 文件数据源集成 =====
    // 单次集成
    @RequestMapping("/file/run")
    public ResponseEntity<Result<String>> fileRun(
            @RequestBody
            @NotEmpty(message = "文件参数不能为空")
            List<? extends Map<String, Object>>
                    filesParams
    ) {
        Context context = new Context();
        context.setSourceDataSource("file11");
        context.setTargetDataSource("db1");
        context.setExtractStrategy("file:full");
        context.setLoadStrategy("db:upsert");
        EtlMetrics stats = orch1.run(context, filesParams);
        log.info("文件数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 单次集成（内部分批）
    @RequestMapping("/file/run-batch")
    public ResponseEntity<Result<String>> fileRunBatch(
            @RequestBody
            @NotEmpty(message = "文件参数不能为空")
            List<? extends Map<String, Object>> filesParams
    ) {
        Context context = new Context();
        context.setSourceDataSource("file21");
        context.setTargetDataSource("db2");
        context.setExtractStrategy("file:full");
        context.setLoadStrategy("db:upsert");
        EtlMetrics stats = orch3.runBatch(context, filesParams);
        log.info("文件数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // ===== 数据库数据源集成 =====
    // 单次集成
    @RequestMapping("/db/run")
    public ResponseEntity<Result<String>> dbRun(
            @RequestBody
            @NotEmpty(message = "数据库参数不能为空")
            List<? extends Map<String, Object>> queriesParams
    ) {
        Context context = new Context();
        context.setSourceDataSource("db1");
        context.setTargetDataSource("db1");
        context.setExtractStrategy("db:ids");
        context.setLoadStrategy("db:upsert");
        EtlMetrics stats = orch5.run(context, queriesParams);
        log.info("数据库数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 单次集成（内部分批）
    @RequestMapping("/db/run-batch")
    public ResponseEntity<Result<String>> dbRunBatch(
            @RequestBody
            @NotEmpty(message = "数据库参数不能为空")
            List<? extends Map<String, Object>> queriesParams
    ) {
        Context context = new Context();
        context.setSourceDataSource("db2");
        context.setTargetDataSource("db2");
        context.setExtractStrategy("db:ids");
        context.setLoadStrategy("db:upsert");
        EtlMetrics stats = orch5.runBatch(context, queriesParams);
        log.info("数据库数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // ===== 消息队列数据源集成 =====
    // 单次集成
    @RequestMapping("/mq/run")
    public ResponseEntity<Result<String>> mqRun(
            @RequestBody
            @NotEmpty(message = "消息队列参数不能为空")
            List<? extends Map<String, Object>> params
    ) {
        Context context = new Context();
        context.setSourceDataSource("mq");
        context.setTargetDataSource("db1");
        context.setLoadStrategy("db:upsert");
        EtlMetrics stats = orch6.run(context, params);
        log.info("消息队列数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 单次集成（内部分批）
    @RequestMapping("/mq/run-batch")
    public ResponseEntity<Result<String>> mqRunBatch(
            @RequestBody
            @NotEmpty(message = "消息队列参数不能为空")
            List<? extends Map<String, Object>> params
    ) {
        Context context = new Context();
        context.setSourceDataSource("mq");
        context.setTargetDataSource("db1");
        context.setLoadStrategy("db:upsert");
        EtlMetrics stats = orch6.runBatch(context, params);
        log.info("消息队列数据源集成完成，报告：{}", stats);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }
}
