package xyz.dreature.smit.component.transformer.registry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.component.transformer.Transformer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 转换器注册器
@Slf4j
@Component
public class TransformerRegistry {
    private final Map<String, Transformer<?, ?>> map;

    @Autowired
    public TransformerRegistry(List<Transformer<?, ?>> transformers) {
        this.map = transformers.stream()
                .collect(Collectors.toMap(
                        transformer -> transformer.getKey(),
                        Function.identity()
                ));
    }

    public <S, T> Transformer<S, T> get(String key) {
        Transformer<S, T> transformer = (Transformer<S, T>) map.get(key);
        if (transformer == null) {
            throw new IllegalArgumentException("未找到转换器: " + key);
        }
        return transformer;
    }
}


