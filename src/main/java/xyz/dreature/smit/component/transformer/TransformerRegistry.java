package xyz.dreature.smit.component.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TransformerRegistry {
    private final Map<String, Transformer<?, ?>> registry = new HashMap<>();

    @Autowired
    public TransformerRegistry(List<Transformer<?, ?>> transformers) {
        for (Transformer<?, ?> transformer : transformers) {
            String key = transformer.getKey();

            if (registry.containsKey(key)) {
                throw new IllegalStateException("转换器键已存在");
            }

            registry.put(key, transformer);
            log.debug("注册转换器: {} -> {}", key, transformer.getClass().getSimpleName());
        }
    }
}


