package xyz.dreature.smit.strategy.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.service.DbService;
import xyz.dreature.smit.service.registry.DbServiceRegistry;
import xyz.dreature.smit.strategy.ExtractStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 数据库-按 ID 抽取策略
@Component
@Lazy
public class IdStrategy<S, ID extends Serializable> implements ExtractStrategy<S> {
    @Autowired
    private DbServiceRegistry registry;

    @Override
    public String getKey() {
        return "db:id";
    }

    @Override
    public List<S> extract(Map<String, ?> queryParams) {
        String dataSource = (String) queryParams.get("dataSource");
        DbService<S, ID> service = registry.getService(dataSource);

        List<S> result = new ArrayList<>();
        ID id = (ID) queryParams.get("id");
        result.add(service.selectById(id));

        return result;
    }

    @Override
    public List<S> extractBatch(List<? extends Map<String, ?>> queriesParams) {
        // 假设批次内数据源相同，故而取首个获取，后续可按需调整
        String dataSource = (String) queriesParams.get(0).get("dataSource");
        DbService<S, ID> service = registry.getService(dataSource);

        List<ID> ids = new ArrayList<>();
        for (Map<String, ?> queryParams : queriesParams) {
            ids.add((ID) queryParams.get("id"));
        }
        return service.selectBatchByIds(ids);
    }
}
