package xyz.dreature.smit.component.transformer;

import xyz.dreature.smit.common.model.context.Context;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// 转换器接口
public interface Transformer<S, T> {
    // 转换器键
    default String getKey() {
        Type superClass = getClass().getGenericInterfaces()[0];
        if (superClass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            Type[] typeArgs = parameterizedType.getActualTypeArguments();
            String sourceType = typeArgs[0].getTypeName();
            String targetType = typeArgs[1].getTypeName();
            return sourceType + "->" + targetType;
        }
        return getClass().getSimpleName();
    }

    // 单项转换
    List<T> transform(Context context, S sourceData);

    // 流式转换
    default List<T> transformStream(Context context, List<S> sourceData) {
        if (sourceData == null || sourceData.isEmpty()) {
            return Collections.emptyList();
        }
        return sourceData.parallelStream()
                .map(item -> transform(context, item))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
