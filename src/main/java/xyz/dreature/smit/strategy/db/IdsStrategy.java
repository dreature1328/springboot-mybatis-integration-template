package xyz.dreature.smit.strategy.db;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.TypeUtil;
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

// 数据库-按 IDs 抽取策略
@Component
@Lazy
public class IdsStrategy<S, ID extends Serializable> implements ExtractStrategy<S> {
    private final Class<ID> idClass;
    @Autowired
    private DbServiceRegistry registry;

    public IdsStrategy() {
        this.idClass = (Class<ID>) TypeUtil.getTypeArgument(this.getClass(), 1);
    }

    @Override
    public String getKey() {
        return "db:ids";
    }

    @Override
    public List<S> extract(Map<String, ?> queryParams) {
        String dataSource = (String) queryParams.get("dataSource");
        DbService<S, ID> service = registry.getService(dataSource);

        String ids = (String) queryParams.get("ids");
        List<ID> idList = Convert.toList(idClass, StrSplitter.split(ids, ',', 0, true, false));

        return service.selectBatchByIds(idList);
    }

    @Override
    public List<S> extractBatch(List<? extends Map<String, ?>> queriesParams) {
        // 假设批次内数据源相同，故而取首个获取，后续可按需调整
        String dataSource = (String) queriesParams.get(0).get("dataSource");
        DbService<S, ID> service = registry.getService(dataSource);

        List<ID> idList = new ArrayList<>();
        for (Map<String, ?> queryParams : queriesParams) {
            String idBatch = (String) queryParams.get("ids");
            List<ID> idBatchList = Convert.toList(idClass, StrSplitter.split(idBatch, ',', 0, true, false));
            idList.addAll(idBatchList);
        }

        return service.selectBatchByIds(idList);
    }
}
