package xyz.dreature.smit.strategy.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.service.DbService;
import xyz.dreature.smit.service.registry.DbServiceRegistry;
import xyz.dreature.smit.strategy.ExtractStrategy;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

// 数据库-按页抽取策略
@Component
@Lazy
public class PageStrategy<S, ID extends Serializable> implements ExtractStrategy<S> {
    @Autowired
    private DbServiceRegistry registry;

    @Override
    public String getKey() {
        return "db:page";
    }

    @Override
    public List extract(Map<String, ?> queryParams) {
        String dataSource = (String) queryParams.get("dataSource");
        DbService<S, ID> service = registry.getService(dataSource);

        int offset = (Integer) queryParams.get("offset");
        int limit = (Integer) queryParams.get("limit");

        return service.selectByPage(offset, limit);
    }
}
