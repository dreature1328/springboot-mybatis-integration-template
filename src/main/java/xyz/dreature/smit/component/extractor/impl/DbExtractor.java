package xyz.dreature.smit.component.extractor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xyz.dreature.smit.common.model.context.Context;
import xyz.dreature.smit.component.extractor.Extractor;
import xyz.dreature.smit.strategy.ExtractStrategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 数据库抽取器
@Component
@Lazy
public class DbExtractor<S> implements Extractor<S> {
    private final Map<String, ExtractStrategy<S>> strategyMap; // 数据库抽取策略集合

    // 注册策略（数据库相关）
    @Autowired
    public DbExtractor(List<ExtractStrategy<S>> strategies) {
        this.strategyMap = strategies.stream()
                .filter(strategy ->
                        strategy.getKey().startsWith("db:"))
                .collect(Collectors.toMap(
                        strategy -> strategy.getKey(),
                        Function.identity()
                ));
    }

    // 单项抽取
    @Override
    @Transactional(readOnly = true)
    public List<S> extract(Context context, Map<String, Object> queryParams) {
        String strategyName = context.getExtractStrategy();
        ExtractStrategy<S> strategy = strategyMap.get(strategyName);

        if (strategy == null) {
            throw new IllegalArgumentException("未知策略: " + strategyName);
        }

        queryParams.put("dataSource", context.getSourceDataSource());

        return strategy.extract(queryParams);
    }

    // 逐项抽取 / 单批抽取
    @Override
    @Transactional(readOnly = true)
    public List<S> extractBatch(Context context, List<? extends Map<String, Object>> queriesParams) {
        String strategyName = context.getExtractStrategy();
        ExtractStrategy<S> strategy = strategyMap.get(strategyName);

        if (strategy == null) {
            throw new IllegalArgumentException("未知策略: " + strategyName);
        }

        // 假设批次内数据源相同，故而取首个设置，后续可按需调整
        queriesParams.get(0).put("dataSource", context.getSourceDataSource());

        return strategy.extractBatch(queriesParams);
    }
}
