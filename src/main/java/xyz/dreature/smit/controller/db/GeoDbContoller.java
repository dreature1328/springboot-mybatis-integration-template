package xyz.dreature.smit.controller.db;

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
import xyz.dreature.smit.common.model.entity.db2.GeoEntity;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.component.transformer.Transformer;
import xyz.dreature.smit.component.transformer.registry.TransformerRegistry;
import xyz.dreature.smit.controller.base.BaseDbController;
import xyz.dreature.smit.service.DbService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/db2-geo")
@Tag(name = "数据库 2 表 geo 操作")
public class GeoDbContoller extends BaseDbController<GeoEntity, Long> {
    @Autowired
    private TransformerRegistry transformerRegistry;

    @Autowired
    GeoDbContoller(@Qualifier("geoDbService") DbService<GeoEntity, Long> dbService) {
        super(dbService);
    }

    // ===== 测试扩展操作 =====
    @Operation(summary = "转换游标数据")
    @PostMapping("/transform-all")
    public ResponseEntity<Result<List<GeoEntity>>> transformAllWithCursor() {
        Transformer<GeoEntity, GeoEntity> transformer = transformerRegistry.get("Object->Object");

        List<GeoEntity> result = dbService.transformAllWithCursor(entity -> transformer.transform(null, entity));
        int count = result.size();
        String message = String.format("转换 %d 条数据", count);
        log.info("数据转换完成，条数：{}", count);
        return ResponseEntity.ok().body(Result.success(message, result));
    }
}