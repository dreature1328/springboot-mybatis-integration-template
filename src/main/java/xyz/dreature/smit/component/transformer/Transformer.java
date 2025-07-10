package xyz.dreature.smit.component.transformer;

import xyz.dreature.smit.common.model.context.EtlContext;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// 转换器接口
public interface Transformer<S, T> {
    // 单项转换
    List<T> transform(EtlContext context, S sourceData);

    // 流式转换
    default List<T> transformStream(EtlContext context, List<S> sourceData) {
        if (sourceData == null || sourceData.isEmpty()) {
            return Collections.emptyList();
        }
        return sourceData.stream()
                .map(item -> transform(context, item))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
