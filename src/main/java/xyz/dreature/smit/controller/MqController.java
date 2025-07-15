package xyz.dreature.smit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.service.MqService;

import java.util.List;

// 测试接口（消息队列操作）
@RestController
@RequestMapping("/mq")
public class MqController {
    @Autowired
    private MqService<Data, Data> mqService;

    // ===== 消息队列抽取 =====
    // 查询总数
    @RequestMapping("/count-all")
    public ResponseEntity<Result<Integer>> countAll(
    ) {
        int count = mqService.countAll();
        String message = String.format("查询总数为 %d 条", count);
        return ResponseEntity.ok().body(Result.success(message, count));
    }

    // 依次接收（定数）
    @RequestMapping("/receive")
    public ResponseEntity<Result<List<Data>>> receive(
            @RequestParam(name = "count", defaultValue = "1") int count
    ) {
        List<Data> messages = mqService.receive(count);
        int resultCount = messages.size();
        String message = String.format("收到 %d 条消息", resultCount);
        return ResponseEntity.ok().body(Result.success(message, messages));
    }

    // 依次接收（全部）
    @RequestMapping("/receive-batch")
    public ResponseEntity<Result<List<Data>>> receiveBatch(
            @RequestParam(name = "count", defaultValue = "1") int count
    ) {
        List<Data> messages = mqService.receiveBatch(count);
        int resultCount = messages.size();
        String message = String.format("收到 %d 条消息", resultCount);
        return ResponseEntity.ok().body(Result.success(message, messages));
    }

    // ===== 消息队列发布 =====
    // 逐项发送
    @RequestMapping("/send")
    public ResponseEntity<Result<Void>> send(
            @RequestBody List<Data> payloads
    ) {
        int successCount = mqService.sendWithConverter(payloads);
        String message = String.format("发送 %d 条消息", successCount);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批发送
    @RequestMapping("/send-batch")
    public ResponseEntity<Result<Void>> sendBatch(
            @RequestBody List<Data> payloads,
            @RequestParam(name = "batch-size", defaultValue = "100") int batchSize
    ) {
        int successCount = mqService.sendBatchWithConverter(payloads, batchSize);
        String message = String.format("发送 %d 条消息", successCount);
        return ResponseEntity.ok().body(Result.success(message, null));
    }
}
