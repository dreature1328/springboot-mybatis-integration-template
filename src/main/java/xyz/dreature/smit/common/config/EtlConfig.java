package xyz.dreature.smit.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.w3c.dom.Document;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.component.extractor.DbExtractor;
import xyz.dreature.smit.component.extractor.FileExtractor;
import xyz.dreature.smit.component.extractor.MockExtractor;
import xyz.dreature.smit.component.extractor.MqExtractor;
import xyz.dreature.smit.component.loader.DbLoader;
import xyz.dreature.smit.component.transformer.IdentityTransformer;
import xyz.dreature.smit.component.transformer.JsonDataTransformer;
import xyz.dreature.smit.component.transformer.XmlTransformer;
import xyz.dreature.smit.orchestration.EtlOrchestrator;

// ETL 组件及编排器配置
@Configuration
public class EtlConfig {
    // 显式声明泛型 Bean，以解决泛型擦除导致的依赖注入失败的问题
    @Bean("mockToDbOrch")
    @Lazy
    public EtlOrchestrator<JsonNode, Data, Long> mockToDbOrchestrator(
            MockExtractor extractor,
            JsonDataTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("jsonFileToDbOrch")
    @Lazy
    public EtlOrchestrator<JsonNode, Data, Long> jsonFileToDbOrchestrator(
            FileExtractor extractor,
            JsonDataTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("xmlFileToDbOrch")
    @Lazy
    public EtlOrchestrator<Document, Data, Long> xmlFileToDbOrchestrator(
            FileExtractor extractor,
            XmlTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("apiToDbOrch")
    @Lazy
    public EtlOrchestrator<JsonNode, Data, Long> apiToDbOrchestrator(
            MockExtractor extractor,
            JsonDataTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("dbToDbOrch")
    @Lazy
    public EtlOrchestrator<Data, Data, Long> mockToDbOrchestrator(
            DbExtractor extractor,
            IdentityTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("mqToDbOrch")
    @Lazy
    public EtlOrchestrator<Data, Data, Long> mqToDbOrchestrator(
            MqExtractor extractor,
            IdentityTransformer transformer,
            DbLoader loader) {

        return new EtlOrchestrator<>(extractor, transformer, loader);
    }
}
