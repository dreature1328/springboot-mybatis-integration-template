package xyz.dreature.smit.component.extractor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.Context;
import xyz.dreature.smit.component.extractor.Extractor;
import xyz.dreature.smit.strategy.ExtractStrategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 文件抽取器
@Component
@Lazy
public class FileExtractor<S> implements Extractor<S> {
    // 文件抽取策略集合
    private final Map<String, ExtractStrategy<S>> strategyMap;

    // 注册策略（文件相关）
    @Autowired
    public FileExtractor(List<ExtractStrategy<S>> strategies) {
        this.strategyMap = strategies.stream()
                .filter(strategy -> strategy.getKey().startsWith("file:"))
                .collect(Collectors.toMap(
                        strategy -> strategy.getKey(),
                        Function.identity()
                ));
    }

    // 单项抽取
    @Override
    public List<S> extract(Context context, Map<String, Object> fileParams) {
        String strategyName = context.getExtractStrategy();
        ExtractStrategy<S> strategy = strategyMap.get(strategyName);

        if (strategy == null) {
            throw new IllegalArgumentException("未知文件抽取策略: " + strategyName);
        }

        fileParams.put("dataSource", context.getSourceDataSource());

        return strategy.extract(fileParams);
    }

    // 单批抽取（并发）
    @Override
    public List<S> extractBatch(Context context, List<? extends Map<String, Object>> filesParams) {
        String strategyName = context.getExtractStrategy();
        ExtractStrategy<S> strategy = strategyMap.get(strategyName);

        if (strategy == null) {
            throw new IllegalArgumentException("未知文件抽取策略: " + strategyName);
        }

        // 假设批次内数据源相同，故而取首个设置，后续可按需调整
        filesParams.get(0).put("dataSource", context.getSourceDataSource());

        return strategy.extractBatch(filesParams);
    }
}
