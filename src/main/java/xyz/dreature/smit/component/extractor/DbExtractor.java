package xyz.dreature.smit.component.extractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.common.util.BatchUtils;
import xyz.dreature.smit.service.DbService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 数据库抽取器
@Component
public class DbExtractor<S, ID extends Serializable> implements Extractor<S> {
    // 策略常量
    private static final String STRATEGY_ID = "db:id";
    private static final String STRATEGY_IDS = "db:ids";
    private static final String STRATEGY_PAGE = "db:page";
    private static final String STRATEGY_RANDOM = "db:random";
    private static final String STRATEGY_ALL = "db:all";

    @Autowired
    private DbService<S, ID> dbService;

    // 单项抽取
    public List<S> extract(EtlContext context, Map<String, ?> queryParams) {
        switch (context.getExtractStrategy().toLowerCase()) {
            case STRATEGY_ID:
                List<S> result = new ArrayList<>();
                ID id = extractIdFromParams(queryParams);
                result.add(dbService.selectById(id));
                return result;

            case STRATEGY_PAGE:
                return handlePageStrategy(queryParams);

            case STRATEGY_RANDOM:
                return handleRandomStrategy(queryParams);

            case STRATEGY_ALL:
                return dbService.selectAll();

            default:
                throw new IllegalArgumentException("不支持的抽取策略: " + context.getExtractStrategy());
        }
    }

    // 逐项抽取 / 单批抽取
    public List<S> extractBatch(EtlContext context, List<? extends Map<String, ?>> queriesParams) {
        List<ID> ids = new ArrayList<>();
        switch (context.getExtractStrategy().toLowerCase()) {
            case STRATEGY_ID:
                for (Map<String, ?> queryParams : queriesParams) {
                    ids.add(extractIdFromParams(queryParams));
                }
                return dbService.selectBatchByIds(ids);

            case STRATEGY_IDS:
                for (Map<String, ?> queryParams : queriesParams) {
                    ids.addAll(extractIdsFromParams(queryParams));
                }
                return dbService.selectBatchByIds(ids);

            case STRATEGY_PAGE:
                return BatchUtils.flatMapEach(queriesParams, this::handlePageStrategy);

            case STRATEGY_RANDOM:
                return BatchUtils.flatMapEach(queriesParams, this::handleRandomStrategy);

            case STRATEGY_ALL:
                return dbService.selectAll();

            default:
                throw new IllegalArgumentException("不支持的抽取策略: " + context.getExtractStrategy());
        }
    }

    private ID extractIdFromParams(Map<String, ?> queryParams) {
        return (ID) queryParams.get("id");
    }

    private List<ID> extractIdsFromParams(Map<String, ?> queryParams) {
        return dbService.parseIdsFromString((String) queryParams.get("ids"));
    }

    private List<S> handlePageStrategy(Map<String, ?> queryParams) {
        int offset = (Integer) queryParams.get("offset");
        int limit = (Integer) queryParams.get("limit");
        return dbService.selectByPage(offset, limit);
    }

    private List<S> handleRandomStrategy(Map<String, ?> queryParams) {
        int count = (Integer) queryParams.get("count");
        return dbService.selectRandom(count);
    }
}
