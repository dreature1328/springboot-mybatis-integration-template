package xyz.dreature.smit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.service.DbService;

import java.util.List;

// 测试接口（数据库操作）
@RestController
@RequestMapping("/db")
public class DbController {
    @Autowired
    private DbService<Data, Long> dbService;

    // 查询总数
    @RequestMapping("/count-all")
    public ResponseEntity<Result<Integer>> countAll() {
        int count = dbService.countAll();
        String message = String.format("查询总数为 %d 条", count);
        return ResponseEntity.ok().body(Result.success(message, count));
    }

    // 查询全表
    @RequestMapping("/select-all")
    public ResponseEntity<Result<List<Data>>> selectAll() {
        List<Data> result = dbService.selectAll();
        int resultCount = result.size();
        String message = String.format("全表查询 %d 条数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 查询随机
    @RequestMapping("/select-random")
    public ResponseEntity<Result<List<Data>>> selectRandom(
            @RequestParam(name = "count", defaultValue = "1") int count
    ) {
        List<Data> result = dbService.selectRandom(count);
        int resultCount = result.size();
        String message = String.format("随机查询 %d 条数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 查询页面
    @RequestMapping("/select-page")
    public ResponseEntity<Result<List<Data>>> selectByPage(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit") int limit
    ) {
        List<Data> result = dbService.selectByPage(offset, limit);
        int resultCount = result.size();
        String message = String.format("页面查询 %d 条数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 逐项查询
    @RequestMapping("/select-ids")
    public ResponseEntity<Result<List<Data>>> selectByIds(
            @RequestParam(name = "ids") String ids
    ) {
        List<Data> result = dbService.selectByIds(dbService.parseIdsFromString(ids).toArray(new Long[0]));
        int resultCount = result.size();
        String message = String.format("逐项查询 %d 条数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 分批查询
    @RequestMapping("/select-batch-ids")
    public ResponseEntity<Result<List<Data>>> selectBatchByIds(
            @RequestParam(name = "ids") String ids,
            @RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
    ) {
        List<Data> result = dbService.selectBatchByIds(dbService.parseIdsFromString(ids), batchSize);
        int resultCount = result.size();
        String message = String.format("分批查询 %d 条数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 逐项插入
    @RequestMapping("/insert")
    public ResponseEntity<Result<Void>> insert(
            @RequestBody Data[] entities
    ) {
        int affectedRows = dbService.insert(entities);
        String message = String.format("逐项插入 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批插入
    @RequestMapping("/insert-batch")
    public ResponseEntity<Result<Void>> insertBatch(
            @RequestBody List<Data> entities,
            @RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
    ) {
        int affectedRows = dbService.insertBatch(entities, batchSize);
        String message = String.format("分批插入 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 逐项更新
    @RequestMapping("/update")
    public ResponseEntity<Result<Void>> update(
            @RequestBody Data[] entities
    ) {
        int affectedRows = dbService.update(entities);
        String message = String.format("逐项更新 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批更新
    @RequestMapping("/update-batch")
    public ResponseEntity<Result<Void>> updateBatch(
            @RequestBody List<Data> entities,
            @RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
    ) {
        int affectedRows = dbService.updateBatch(entities, batchSize);
        String message = String.format("分批更新 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 逐项插入或更新
    @RequestMapping("/upsert")
    public ResponseEntity<Result<Void>> upsert(
            @RequestBody Data[] entities
    ) {
        int affectedRows = dbService.upsert(entities);
        String message = String.format("逐项插入或更新 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批插入或更新
    @RequestMapping("/upsert-batch")
    public ResponseEntity<Result<Void>> upsertBatch(
            @RequestBody List<Data> entities,
            @RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
    ) {
        int affectedRows = dbService.upsertBatch(entities, batchSize);
        String message = String.format("分批插入或更新 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 逐项删除
    @RequestMapping("/delete-ids")
    public ResponseEntity<Result<Void>> deleteById(
            @RequestParam(name = "ids") String ids
    ) {
        int affectedRows = dbService.deleteByIds(dbService.parseIdsFromString(ids).toArray(new Long[0]));
        String message = String.format("逐项删除 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批删除
    @RequestMapping("/delete-batch-ids")
    public ResponseEntity<Result<Void>> deleteBatchByIds(
            @RequestParam(name = "ids") String ids,
            @RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
    ) {
        int affectedRows = dbService.deleteBatchByIds(dbService.parseIdsFromString(ids), batchSize);
        String message = String.format("分批删除 %d 条数据", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 清空
    @RequestMapping("/truncate")
    public ResponseEntity<Result<Void>> truncate() {
        int count = dbService.countAll();
        dbService.truncate();
        String message = String.format("清空 %d 条数据", count);
        return ResponseEntity.ok().body(Result.success(message, null));
    }
}
