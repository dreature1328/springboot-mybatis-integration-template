package xyz.dreature.smit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.common.util.IdUtils;
import xyz.dreature.smit.service.DbService;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

// 测试接口（数据库操作）
@Slf4j
@RestController
@RequestMapping("/db")
@Validated
public class DbController {
    @Autowired
    private DbService<Data, Long> dbService;

    // 查询总数
    @RequestMapping("/count-all")
    public ResponseEntity<Result<Integer>> countAll() {
        int count = dbService.countAll();
        String message = String.format("查询总数为 %d 条", count);
        log.info("查询总数完成，条数：{}", count);
        return ResponseEntity.ok().body(Result.success(message, count));
    }

    // 查询全表
    @RequestMapping("/select-all")
    public ResponseEntity<Result<List<Data>>> selectAll() {
        List<Data> result = dbService.selectAll();
        int resultCount = result.size();
        String message = String.format("全表查询 %d 条数据", resultCount);
        log.info("全表查询完成，条数：{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 查询随机
    @RequestMapping("/select-random")
    public ResponseEntity<Result<List<Data>>> selectRandom(
            @RequestParam(name = "count", defaultValue = "1")
            @Positive(message = "条数必须为正")
            int count
    ) {
        List<Data> result = dbService.selectRandom(count);
        int resultCount = result.size();
        String message = String.format("随机查询 %d 条数据", resultCount);
        log.info("随机查询完成，条数：{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 查询页面
    @RequestMapping("/select-by-page")
    public ResponseEntity<Result<List<Data>>> selectByPage(
            @RequestParam(name = "offset", defaultValue = "0")
            @Min(value = 0, message = "偏移量不能为负")
            int offset,

            @RequestParam(name = "limit")
            @Min(value = 0, message = "限数不能为负")
            int limit
    ) {
        List<Data> result = dbService.selectByPage(offset, limit);
        int resultCount = result.size();
        String message = String.format("页面查询 %d 条数据", resultCount);
        log.info("页面查询完成，条数:{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 逐项查询
    @RequestMapping("/select-by-ids")
    public ResponseEntity<Result<List<Data>>> selectByIds(
            @RequestParam(name = "ids")
            @NotBlank(message = "ID 不能为空")
            @Pattern(regexp = "^\\d+(,\\d+)*$", message = "ID 需由逗号分隔")
            String ids
    ) {
        List<Data> result = dbService.selectByIds(IdUtils.parseLongIds(ids).toArray(new Long[0]));
        int resultCount = result.size();
        String message = String.format("逐项查询 %d 条数据", resultCount);
        log.info("逐项查询完成，条数:{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 分批查询
    @RequestMapping("/select-batch-by-ids")
    public ResponseEntity<Result<List<Data>>> selectBatchByIds(
            @RequestParam(name = "ids")
            @NotBlank(message = "ID 不能为空")
            @Pattern(regexp = "^\\d+(,\\d+)*$", message = "ID 需由逗号分隔")
            String ids,

            @RequestParam(name = "batch-size", defaultValue = "1000")
            @Positive(message = "批大小必须为正")
            int batchSize
    ) {
        List<Data> result = dbService.selectBatchByIds(IdUtils.parseLongIds(ids), batchSize);
        int resultCount = result.size();
        String message = String.format("分批查询 %d 条数据", resultCount);
        log.info("分批查询完成，条数：{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 逐项插入
    @RequestMapping("/insert")
    public ResponseEntity<Result<Void>> insert(
            @RequestBody
            @NotEmpty(message = "插入的数据不能为空")
            @Valid
            Data[] entities
    ) {
        int affectedRows = dbService.insert(entities);
        String message = String.format("逐项插入 %d 条数据", affectedRows);
        log.info("逐项插入完成，影响行数：{}", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批插入
    @RequestMapping("/insert-batch")
    public ResponseEntity<Result<Void>> insertBatch(
            @RequestBody
            @NotEmpty(message = "插入的数据不能为空")
            @Valid
            List<Data> entities,

            @RequestParam(name = "batch-size", defaultValue = "1000")
            @Positive(message = "批大小必须为正")
            int batchSize
    ) {
        int affectedRows = dbService.insertBatch(entities, batchSize);
        String message = String.format("分批插入 %d 条数据", affectedRows);
        log.info("分批插入完成，影响行数：{}", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 逐项更新
    @RequestMapping("/update")
    public ResponseEntity<Result<Void>> update(
            @RequestBody
            @NotEmpty(message = "更新的数据不能为空")
            @Valid
            Data[] entities
    ) {
        int affectedRows = dbService.update(entities);
        String message = String.format("逐项更新 %d 条数据", affectedRows);
        log.info("逐项更新完成，影响行数：{}", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批更新
    @RequestMapping("/update-batch")
    public ResponseEntity<Result<Void>> updateBatch(
            @RequestBody
            @NotEmpty(message = "更新的数据不能为空")
            @Valid
            List<Data> entities,

            @RequestParam(name = "batch-size", defaultValue = "1000")
            @Positive(message = "批大小必须为正")
            int batchSize
    ) {
        int affectedRows = dbService.updateBatch(entities, batchSize);
        String message = String.format("分批更新 %d 条数据", affectedRows);
        log.info("分批更新完成，影响行数：{}", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 逐项插入或更新
    @RequestMapping("/upsert")
    public ResponseEntity<Result<Void>> upsert(
            @RequestBody
            @NotEmpty(message = "插入或更新的数据不能为空")
            @Valid
            Data[] entities
    ) {
        int affectedRows = dbService.upsert(entities);
        String message = String.format("逐项插入或更新 %d 条数据", affectedRows);
        log.info("逐项插入或更新完成，影响行数：{}", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批插入或更新
    @RequestMapping("/upsert-batch")
    public ResponseEntity<Result<Void>> upsertBatch(
            @RequestBody
            @NotEmpty(message = "插入或更新的数据不能为空")
            @Valid
            List<Data> entities,

            @RequestParam(name = "batch-size", defaultValue = "1000")
            @Positive(message = "批大小必须为正")
            int batchSize
    ) {
        int affectedRows = dbService.upsertBatch(entities, batchSize);
        String message = String.format("分批插入或更新 %d 条数据", affectedRows);
        log.info("分批插入或更新完成，影响行数：{}", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 逐项删除
    @RequestMapping("/delete-by-ids")
    public ResponseEntity<Result<Void>> deleteById(
            @RequestParam(name = "ids")
            @NotBlank(message = "ID 不能为空")
            @Pattern(regexp = "^\\d+(,\\d+)*$", message = "ID 需由逗号分隔")
            String ids
    ) {
        int affectedRows = dbService.deleteByIds(IdUtils.parseLongIds(ids).toArray(new Long[0]));
        String message = String.format("逐项删除 %d 条数据", affectedRows);
        log.info("逐项删除完成，影响行数：{}", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 分批删除
    @RequestMapping("/delete-batch-by-ids")
    public ResponseEntity<Result<Void>> deleteBatchByIds(
            @RequestParam(name = "ids")
            @NotBlank(message = "ID 不能为空")
            @Pattern(regexp = "^\\d+(,\\d+)*$", message = "ID 需由逗号分隔")
            String ids,

            @RequestParam(name = "batch-size", defaultValue = "1000")
            @Positive(message = "批大小必须为正")
            int batchSize
    ) {
        int affectedRows = dbService.deleteBatchByIds(IdUtils.parseLongIds(ids), batchSize);
        String message = String.format("分批删除 %d 条数据", affectedRows);
        log.info("分批删除完成，影响行数：{}", affectedRows);
        return ResponseEntity.ok().body(Result.success(message, null));
    }

    // 清空
    @RequestMapping("/truncate")
    public ResponseEntity<Result<Void>> truncate() {
        int count = dbService.countAll();
        dbService.truncate();
        String message = String.format("清空 %d 条数据", count);
        log.info("清空完成，条数：{}", count);
        return ResponseEntity.ok().body(Result.success(message, null));
    }
}
