package xyz.dreature.smit.strategy.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.service.DbService;
import xyz.dreature.smit.strategy.ExtractStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Lazy
public class IdStrategy<S, ID extends Serializable> implements ExtractStrategy<S> {
    @Autowired
    private DbService<S, ID> dbService;

    @Override
    public String getStrategyName() {
        return "db:id";
    }

    @Override
    public List extract(Map queryParams) {
        List<S> result = new ArrayList<>();
        ID id = (ID) queryParams.get("id");
        result.add(dbService.selectById(id));
        return result;
    }

    @Override
    public List extractBatch(List<? extends Map<String, ?>> queriesParams) {
        List<ID> ids = new ArrayList<>();
        for (Map<String, ?> queryParams : queriesParams) {
            ID id = (ID) queryParams.get("id");
            ids.add(id);
        }
        return dbService.selectBatchByIds(ids);
    }


}
