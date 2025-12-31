package xyz.dreature.smit.strategy.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.util.IdUtils;
import xyz.dreature.smit.service.DbService;
import xyz.dreature.smit.strategy.ExtractStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Lazy
public class IdsStrategy<S, ID extends Serializable> implements ExtractStrategy<S> {
    @Autowired
    private DbService<S, ID> dbService;

    @Override
    public String getStrategyName() {
        return "db:ids";
    }

    @Override
    public List extract(Map queryParams) {
        List<ID> ids = (List<ID>) IdUtils.parseLongIds((String) queryParams.get("ids"));
        return dbService.selectBatchByIds(ids);
    }

    @Override
    public List extractBatch(List<? extends Map<String, ?>> queriesParams) {
        List<ID> ids = new ArrayList<>();
        for (Map<String, ?> queryParams : queriesParams) {
            ids.addAll((List<ID>) IdUtils.parseLongIds((String) queryParams.get("ids")));
        }
        return dbService.selectBatchByIds(ids);
    }
}
