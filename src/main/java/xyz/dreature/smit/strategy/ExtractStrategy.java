package xyz.dreature.smit.strategy;

import xyz.dreature.smit.common.util.BatchUtils;

import java.util.List;
import java.util.Map;

public interface ExtractStrategy<S> {
    // 获取策略名
    String getStrategyName();

    // 单项抽取
    List<S> extract(Map<String, ?> params);

    // 逐项抽取 / 单批抽取
    default List<S> extractBatch(List<? extends Map<String, ?>> params) {
        // 默认实现，当某个场景不存在批量优化策略时，则以逐项抽取（循环调用单项抽取）代替
        return BatchUtils.flatMapEach(params, each -> extract(each));
    }
}
