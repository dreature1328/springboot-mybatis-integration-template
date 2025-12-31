package xyz.dreature.smit.component.extractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.strategy.ExtractStrategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 数据库抽取器
@Component
@Lazy
public class DbExtractor<S> implements Extractor<S> {
    // 数据库抽取策略集合
    private final Map<String, ExtractStrategy<S>> strategyMap;

    // 注册策略（数据库相关）
    @Autowired
    public DbExtractor(List<ExtractStrategy<S>> strategies) {
        this.strategyMap = strategies.stream()
                .filter(strategy -> strategy.getStrategyName().startsWith("db:"))
                .collect(Collectors.toMap(
                        strategy -> strategy.getStrategyName(),
                        Function.identity()
                ));
    }

    // 单项抽取
    @Override
    @Transactional(readOnly = true)
    public List<S> extract(EtlContext context, Map<String, ?> queryParams) {
        String strategyName = context.getExtractStrategy();
        ExtractStrategy<S> strategy = strategyMap.get(strategyName);

        if (strategy == null) {
            throw new IllegalArgumentException("未知策略: " + strategyName);
        }

        return strategy.extract(queryParams);
    }

    // 逐项抽取 / 单批抽取
    @Override
    @Transactional(readOnly = true)
    public List<S> extractBatch(EtlContext context, List<? extends Map<String, ?>> queriesParams) {
        String strategyName = context.getExtractStrategy();
        ExtractStrategy<S> strategy = strategyMap.get(strategyName);

        if (strategy == null) {
            throw new IllegalArgumentException("未知策略: " + strategyName);
        }

        return strategy.extractBatch(queriesParams);
    }

}
