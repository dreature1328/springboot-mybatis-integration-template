package xyz.dreature.smit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.entity.db2.AdvancedEntity;
import xyz.dreature.smit.service.DbService;

// 测试接口（数据库操作）
@RestController
@RequestMapping("/db2")
public class AdvancedDbContoller extends BaseDbController<AdvancedEntity, Long> {
    @Autowired
    AdvancedDbContoller(@Qualifier("advancedDbService") DbService<AdvancedEntity, Long> dbService) {
        super(dbService, AdvancedEntity.class, Long.class);
    }
    // ===== 测试扩展操作 =====
}
