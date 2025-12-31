package xyz.dreature.smit.strategy.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.service.DbService;
import xyz.dreature.smit.strategy.ExtractStrategy;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Component
@Lazy
public class PageStrategy<S, ID extends Serializable> implements ExtractStrategy<S> {
    @Autowired
    private DbService<S, ID> dbService;

    @Override
    public String getStrategyName() {
        return "db:page";
    }

    @Override
    public List extract(Map queryParams) {
        int offset = (Integer) queryParams.get("offset");
        int limit = (Integer) queryParams.get("limit");
        return dbService.selectByPage(offset, limit);
    }

}
