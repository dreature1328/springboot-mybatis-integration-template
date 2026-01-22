package xyz.dreature.smit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.entity.db2.AdvancedEntity;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.component.transformer.Transformer;
import xyz.dreature.smit.component.transformer.registry.TransformerRegistry;
import xyz.dreature.smit.controller.base.BaseDbController;
import xyz.dreature.smit.service.DbService;

import java.util.List;

// 测试接口（数据库操作）
@Slf4j
@RestController
@RequestMapping("/db2")
public class AdvancedDbContoller extends BaseDbController<AdvancedEntity, Long> {
    @Autowired
    private TransformerRegistry transformerRegistry;

    @Autowired
    AdvancedDbContoller(@Qualifier("advancedDbService") DbService<AdvancedEntity, Long> dbService) {
        super(dbService);
    }

    // ===== 测试扩展操作 =====
    // 转换游标数据
    @RequestMapping("/transform-all")
    public ResponseEntity<Result<List<AdvancedEntity>>> transformAllWithCursor() {
        Transformer<AdvancedEntity, AdvancedEntity> transformer = transformerRegistry.get("Object->Object");

        List<AdvancedEntity> result = dbService.transformAllWithCursor(entity -> transformer.transform(null, entity));
        int count = result.size();
        String message = String.format("转换 %d 条数据", count);
        log.info("数据转换完成，条数：{}", count);
        return ResponseEntity.ok().body(Result.success(message, result));
    }
}
