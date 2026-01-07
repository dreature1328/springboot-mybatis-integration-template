package xyz.dreature.smit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.entity.db1.StandardEntity;
import xyz.dreature.smit.service.DbService;

// 测试接口（数据库操作）
@RestController
@RequestMapping("/db1")
public class StandardDbContoller extends BaseDbController<StandardEntity, Long> {
    @Autowired
    StandardDbContoller(@Qualifier("standardDbService") DbService<StandardEntity, Long> dbService) {
        super(dbService, StandardEntity.class, Long.class);
    }
    // ===== 测试扩展操作 =====
}
