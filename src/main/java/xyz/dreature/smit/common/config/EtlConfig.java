package xyz.dreature.smit.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.component.extractor.*;
import xyz.dreature.smit.component.loader.DbLoader;
import xyz.dreature.smit.component.transformer.IdentityTransformer;
import xyz.dreature.smit.component.transformer.JsonEntityTransformer;
import xyz.dreature.smit.component.transformer.XmlTransformer;
import xyz.dreature.smit.orchestration.EtlOrchestrator;

// ETL 组件及编排器配置
@Configuration
public class EtlConfig {
    // 显式声明泛型 Bean，以解决泛型擦除导致的依赖注入失败的问题
    @Bean("mockToDbOrch")
    public EtlOrchestrator<JsonNode, Data, Long> mockToDbOrchestrator(
            MockExtractor extractor,
            JsonEntityTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("jsonFileToDbOrch")
    public EtlOrchestrator<JsonNode, Data, Long> jsonFileToDbOrchestrator(
            FileExtractor extractor,
            JsonEntityTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("xmlFileToDbOrch")
    public EtlOrchestrator<JsonNode, Data, Long> xmlFileToDbOrchestrator(
            FileExtractor extractor,
            XmlTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("apiToDbOrch")
    public EtlOrchestrator<JsonNode, Data, Long> apiToDbOrchestrator(
            MockExtractor extractor,
            JsonEntityTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("dbToDbOrch")
    public EtlOrchestrator<Data, Data, Long> mockToDbOrchestrator(
            DbExtractor extractor,
            IdentityTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("redisToDbOrch")
    public EtlOrchestrator<Data, Data, Long> redisToDbOrchestrator(
            RedisExtractor extractor,
            IdentityTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("mqToDbOrch")
    public EtlOrchestrator<Data, Data, Long> mqToDbOrchestrator(
            MqExtractor extractor,
            IdentityTransformer transformer,
            DbLoader loader) {

        return new EtlOrchestrator<>(extractor, transformer, loader);
    }
}
