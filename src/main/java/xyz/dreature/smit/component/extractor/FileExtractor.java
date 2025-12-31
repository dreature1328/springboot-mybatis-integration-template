package xyz.dreature.smit.component.extractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.EtlContext;
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
                .filter(strategy -> strategy.getStrategyName().startsWith("file:"))
                .collect(Collectors.toMap(
                        strategy -> strategy.getStrategyName(),
                        Function.identity()
                ));
    }

    // 单项抽取
    @Override
    public List<S> extract(EtlContext context, Map<String, ?> fileParams) {
        String strategyName = context.getExtractStrategy();
        ExtractStrategy<S> strategy = strategyMap.get(strategyName);

        if (strategy == null) {
            throw new IllegalArgumentException("未知文件抽取策略: " + strategyName);
        }

        return strategy.extract(fileParams);
    }

    // 单批抽取（并发）
    @Override
    public List<S> extractBatch(EtlContext context, List<? extends Map<String, ?>> filesParams) {
        String strategyName = context.getExtractStrategy();
        ExtractStrategy<S> strategy = strategyMap.get(strategyName);

        if (strategy == null) {
            throw new IllegalArgumentException("未知文件抽取策略: " + strategyName);
        }

        return strategy.extractBatch(filesParams);
    }
}
