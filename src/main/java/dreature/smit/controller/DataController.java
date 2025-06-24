package dreature.smit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dreature.smit.common.stats.IntegrationStats;
import dreature.smit.common.util.BatchUtils;
import dreature.smit.common.util.JsonUtils;
import dreature.smit.common.util.XmlUtils;
import dreature.smit.common.vo.Result;
import dreature.smit.entity.Data;
import dreature.smit.service.ExtractService;
import dreature.smit.service.IntegrationService;
import dreature.smit.service.LoadService;
import dreature.smit.service.TransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataController {
    @Autowired
    private IntegrationService<Data> integrationService;
    @Autowired
    private ExtractService<Data> extractService;
    @Autowired
    private TransformService<Data> transformService;
    @Autowired
    private LoadService<Data> loadService;

    // ----- 数据集成 -----
    // 集成
    @RequestMapping("/integrate")
    public ResponseEntity<Result> integrate(
            @RequestParam(name = "total", defaultValue = "10") int total
    ) {
        List<Map<String, String>> paramsList = extractService.generateParams(total);
        IntegrationStats stats = integrationService.integrate(paramsList);
        return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
    }

    // 优化集成
    @RequestMapping("/integrate-x")
    public ResponseEntity<Result> integrateOptimized(
            @RequestParam(name = "total", defaultValue = "1000") int total, // 数据总数
            @RequestParam(name = "req-batch-size", defaultValue = "200") int reqBatchSize, // 请求批大小
            @RequestParam(name = "persist-batch-size", defaultValue = "1000") int persistBatchSize // 持久化批大小
    ) {
        List<Map<String, String>> paramsList = extractService.generateParams(total);
        List<IntegrationStats> result = integrationService.integrateOptimized(paramsList, reqBatchSize, persistBatchSize);
        return ResponseEntity.ok().body(Result.success(result.get(0).generateReport(), null));
    }

    // 分批优化集成
    @RequestMapping("/integrate-batch-x")
    public ResponseEntity<Result> integrateBatchOptimized(
            @RequestParam(name = "total", defaultValue = "1000") int total, // 数据总数
            @RequestParam(name = "int-batch-size", defaultValue = "200") int intBatchSize, // 集成批大小
            @RequestParam(name = "req-batch-size", defaultValue = "200") int reqBatchSize, // 请求批大小
            @RequestParam(name = "persist-batch-size", defaultValue = "1000") int persistBatchSize // 持久化批大小
    ) {
        List<Map<String, String>> paramsList = extractService.generateParams(total);
        List<IntegrationStats> statsList = integrationService.integrateBatchOptimized(paramsList, intBatchSize, reqBatchSize, persistBatchSize);

        // 合并多个报告
        StringBuilder combinedReport = new StringBuilder();
        for (int i = 0; i < statsList.size(); i++) {
            combinedReport.append("第").append(i + 1).append("批: ")
                    .append(statsList.get(i).generateReport());
            if (i < statsList.size() - 1) {
                combinedReport.append("\n");
            }
        }
        return ResponseEntity.ok().body(Result.success(combinedReport.toString(), null));
    }

    // ----- 本地抽取 -----
    // 生成数据（测试用）
    @RequestMapping("/generate")
    public ResponseEntity<Result> generate(
            @RequestParam(name = "count", defaultValue = "10") int count
    ) {
        List<Data> result = transformService.generate(count);
        int resultCount = result.size();
        String message = String.format("生成 %d 条数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 解析 JSON 文件（测试用）
    @RequestMapping("/parse-json")
    public ResponseEntity<Result> parseFromJson() {
        JsonNode jsonNode = JsonUtils.parseFile("script/mock_data.json");
        List<Data> result = transformService.transform(jsonNode);
        int resultCount = result.size();
        String message = String.format("解析 %d 条 JSON 数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 解析 XML 文件（测试用）
    @RequestMapping("/parse-xml")
    public ResponseEntity<Result> parseFromXml() {
        Document document = XmlUtils.parseFile("script/mock_data.xml");
        List<Data> result = transformService.transform(document);
        int resultCount = result.size();
        String message = String.format("解析 %d 条 XML 数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // ----- API 抽取 -----
    // 生成请求参数（测试用）
    @RequestMapping("/params")
    public ResponseEntity<Result> generateParams(
            @RequestParam(name = "count", defaultValue = "10") int count
    ) {
        List<Map<String, String>> result = extractService.generateParams(count);
        int resultCount = result.size();
        String message = String.format("生成 %d 组参数", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 依次同步请求
    @RequestMapping("/request")
    public ResponseEntity<Result> request(
            @RequestParam(name = "total", defaultValue = "10") int total
    ) {
        List<Map<String, String>> paramsList = extractService.generateParams(total);
        List<JsonNode> responses = BatchUtils.mapEach(paramsList, extractService::request);
        int resultCount = responses.size();
        String message = String.format("依次同步发送 %d 次请求，收到 %d 条响应", total, resultCount);
        return ResponseEntity.ok().body(Result.success(message, responses));
    }

    // 分批异步请求
    @RequestMapping("/request-batch")
    public ResponseEntity<Result> requestBatch(
            @RequestParam(name = "total", defaultValue = "1000") int total,
            @RequestParam(name = "batch-size", defaultValue = "200") int batchSize
    ) {
        List<Map<String, String>> paramsList = extractService.generateParams(total);
        List<JsonNode> responses = extractService.requestBatch(paramsList, batchSize);
        int resultCount = responses.size();
        String message = String.format("分批异步发送 %d 次请求，收到 %d 条响应", total, resultCount);
        return ResponseEntity.ok().body(Result.success(message, responses));
    }

    // 生成响应内容（测试用）
    @RequestMapping("/responses")
    public ResponseEntity<Result> generateResponses(
            @RequestParam(name = "count", defaultValue = "10") int count
    ) {
        List<JsonNode> result = extractService.generateResponses(count);
        int resultCount = result.size();
        String message = String.format("生成 %d 条响应", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // ----- 数据库抽取 -----
    // 查询总数
    @RequestMapping("/count")
    public ResponseEntity<Result> countAll() {
        int count = extractService.countAll();
        String message = String.format("查询总数为 %d 条", count);
        return ResponseEntity.ok().body(Result.success(message, count));
    }

    // 查询数据
    @RequestMapping("/find")
    public ResponseEntity<Result> find(
            @RequestParam(name = "count", required = false) Integer count // 为空则查询所有记录
    ) {
        List<Data> result = (count == null ? extractService.findAll() : extractService.findRandomN(count));
        int resultCount = result.size();
        String message = String.format("查询 %d 条数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 分批查询
    @RequestMapping("/select-batch")
    public ResponseEntity<Result> selectBatchByIds(
            @RequestParam(name = "ids") String ids,
            @RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
    ) {
        List<Data> result = extractService.selectBatchByIds(new ArrayList<>(Arrays.asList(ids.split(","))), batchSize);
        int resultCount = result.size();
        String message = String.format("分批查询 %d 条数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // ----- 消息队列抽取 -----
    // 依次接收
    @RequestMapping("/receive")
    public ResponseEntity<Result> receive(
            @RequestParam(name = "count", required = false) Integer count // 为空则接收所有消息
    ) {
        List<JsonNode> messages = (count == null ? extractService.receiveAll() : extractService.receive(count));
        int resultCount = messages.size();
        String message = String.format("收到 %d 条消息", resultCount);
        return ResponseEntity.ok().body(Result.success(message, messages));
    }

    // ----- 数据转换 -----
    // 传统转换
    @RequestMapping("/transform")
    public ResponseEntity<Result> transform(
            @RequestParam(name = "total", defaultValue = "1000") int total
    ) {
//		List<Map<String, String>> paramsList = extractService.generateParams();
//		List<String> responses = extractService.request(paramsList);
        List<JsonNode> responses = extractService.generateResponses(total);
        List<Data> dataList = transformService.transform(responses.toArray(new JsonNode[0]));
        int resultCount = dataList.size();
        String message = String.format("传统转换 %d 条数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, dataList));
    }

    // 流式转换
    @RequestMapping("/transform-stream")
    public ResponseEntity<Result> transformStream(
            @RequestParam(name = "total", defaultValue = "1000") int total
    ) {
//		List<Map<String, String>> paramsList = extractService.generateParams();
//		List<String> responses = extractService.requestBatch(paramsList);
        List<JsonNode> responses = extractService.generateResponses(total);
        List<Data> dataList = transformService.transformStream(responses);
        int resultCount = dataList.size();
        String message = String.format("流式转换 %d 条数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, dataList));
    }

    // ----- 数据库持久化 -----
    // 分批插入
    @RequestMapping("/insert-batch")
    public ResponseEntity<Result> insertBatch(
            @RequestParam(name = "total", defaultValue = "1000") int total,
            @RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
    ) {
        int affectedRows = loadService.insertBatch(transformService.generate(total), batchSize);
        String message = String.format("分批插入 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批更新
    @RequestMapping("/update-batch")
    public ResponseEntity<Result> updateBatch(
            @RequestParam(name = "total", defaultValue = "1000") int total,
            @RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
    ) {
        int affectedRows = loadService.updateBatch(transformService.generate(total), batchSize);
        String message = String.format("分批更新 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批插入或更新
    @RequestMapping("/upsert-batch")
    public ResponseEntity<Result> upsertBatch(
            @RequestParam(name = "total", defaultValue = "1000") int total,
            @RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
    ) {
        int affectedRows = loadService.upsertBatch(transformService.generate(total), batchSize);
        String message = String.format("分批插入或更新 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批删除
    @RequestMapping("/delete-batch")
    public ResponseEntity<Result> deleteBatchByIds(
            @RequestParam(name = "ids") String ids,
            @RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
    ) {
        int affectedRows = loadService.deleteBatchByIds(new ArrayList<>(Arrays.asList(ids.split(","))), batchSize);
        String message = String.format("分批删除 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 清空
    @RequestMapping("/truncate")
    public ResponseEntity<Result> truncate() {
        int count = extractService.countAll();
        loadService.truncate();
        String message = String.format("清空 %d 条数据", count);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // ----- 消息队列发布 -----
    // 逐项发送
    @RequestMapping("/send")
    public ResponseEntity<Result> send(
            @RequestParam(name = "total", defaultValue = "10") int total
    ) {
        int successCount = loadService.send(transformService.generate(total));
        String message = String.format("发送 %d 条消息", successCount);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批发送
    @RequestMapping("/send-batch")
    public ResponseEntity<Result> sendBatch(
            @RequestParam(name = "total", defaultValue = "1000") int total,
            @RequestParam(name = "batch-size", defaultValue = "100") int batchSize
    ) {
        int successCount = loadService.sendBatch(transformService.generate(total), batchSize);
        String message = String.format("发送 %d 条消息", successCount);
        return ResponseEntity.ok().body(Result.success(message, null));
    }
}
