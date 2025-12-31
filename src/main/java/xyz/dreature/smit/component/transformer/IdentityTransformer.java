package xyz.dreature.smit.component.transformer;

import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.EtlContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 恒等转换器
@Component
public class IdentityTransformer<S, T> implements Transformer<S, T> {
    // 转换器键
    @Override
    public String getKey() {
        return Object.class.getName() + "->" + Object.class.getName();
    }

    // 单项转换
    @Override
    public List<T> transform(EtlContext context, S sourceData) {
        List<T> result = new ArrayList<>();
        result.add((T) sourceData);
        return result;

    }

    // 流式转换
    @Override
    public List<T> transformStream(EtlContext context, List<S> sourceData) {
        return sourceData != null ? (List<T>) sourceData : Collections.emptyList();
    }
}
