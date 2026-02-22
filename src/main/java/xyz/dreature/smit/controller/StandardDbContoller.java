package xyz.dreature.smit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.entity.db1.StandardEntity;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.component.transformer.Transformer;
import xyz.dreature.smit.component.transformer.registry.TransformerRegistry;
import xyz.dreature.smit.controller.base.BaseDbController;
import xyz.dreature.smit.service.DbService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/db1")
@Tag(name = "数据库1操作")
public class StandardDbContoller extends BaseDbController<StandardEntity, Long> {
    @Autowired
    private TransformerRegistry transformerRegistry;

    @Autowired
    StandardDbContoller(@Qualifier("standardDbService") DbService<StandardEntity, Long> dbService) {
        super(dbService);
    }

    // ===== 测试扩展操作 =====
    @Operation(summary = "转换游标数据")
    @PostMapping("/transform-all")
    public ResponseEntity<Result<List<StandardEntity>>> transformAllWithCursor() {
        Transformer<StandardEntity, StandardEntity> transformer = transformerRegistry.get("Object->Object");

        List<StandardEntity> result = dbService.transformAllWithCursor(entity -> transformer.transform(null, entity));
        int count = result.size();
        String message = String.format("转换 %d 条数据", count);
        log.info("数据转换完成，条数：{}", count);
        return ResponseEntity.ok().body(Result.success(message, result));
    }
}