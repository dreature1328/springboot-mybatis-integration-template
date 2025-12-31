package xyz.dreature.smit.component.extractor;

import xyz.dreature.smit.common.model.context.EtlContext;

import java.util.List;
import java.util.Map;

// 抽取器接口
public interface Extractor<S> {
    // 单项抽取
    List<S> extract(EtlContext context, Map<String, ?> params);

    // 逐项抽取 / 单批抽取
    List<S> extractBatch(EtlContext context, List<? extends Map<String, ?>> params);
}
