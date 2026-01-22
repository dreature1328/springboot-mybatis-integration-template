package xyz.dreature.smit.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.w3c.dom.Document;
import xyz.dreature.smit.common.model.entity.db1.StandardEntity;
import xyz.dreature.smit.common.model.entity.db2.AdvancedEntity;
import xyz.dreature.smit.component.extractor.impl.DbExtractor;
import xyz.dreature.smit.component.extractor.impl.FileExtractor;
import xyz.dreature.smit.component.extractor.impl.MockExtractor;
import xyz.dreature.smit.component.extractor.impl.MqExtractor;
import xyz.dreature.smit.component.loader.impl.DbLoader;
import xyz.dreature.smit.component.transformer.impl.*;
import xyz.dreature.smit.orchestration.EtlOrchestrator;

// 编排器配置
@Configuration
public class EtlConfig {
    // 指定抽取器、转换器、加载器
    @Bean("mock->db1")
    @Lazy
    public EtlOrchestrator<JsonNode, StandardEntity, Long> mockToDb1Orchestrator(
            MockExtractor extractor,
            JsonStandardTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("file11->db1")
    @Lazy
    public EtlOrchestrator<JsonNode, StandardEntity, Long> file11ToDb1Orchestrator(
            FileExtractor extractor,
            JsonStandardTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("file12->db1")
    @Lazy
    public EtlOrchestrator<Document, StandardEntity, Long> file12ToDb1Orchestrator(
            FileExtractor extractor,
            XmlStandardTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("file21->db2")
    @Lazy
    public EtlOrchestrator<JsonNode, AdvancedEntity, Long> file21ToDb2Orchestrator(
            FileExtractor extractor,
            JsonAdvancedTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("file22->db2")
    @Lazy
    public EtlOrchestrator<Document, AdvancedEntity, Long> fil22ToDb2Orchestrator(
            FileExtractor extractor,
            XmlAdvancedTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("api->db1")
    @Lazy
    public EtlOrchestrator<JsonNode, StandardEntity, Long> apiToDb1Orchestrator(
            MockExtractor extractor,
            JsonStandardTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("db1->db1")
    @Lazy
    public EtlOrchestrator<StandardEntity, StandardEntity, Long> db1ToDb1Orchestrator(
            DbExtractor extractor,
            IdentityTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }

    @Bean("mq->db1")
    @Lazy
    public EtlOrchestrator<StandardEntity, StandardEntity, Long> mqToDb1Orchestrator(
            MqExtractor extractor,
            IdentityTransformer transformer,
            DbLoader loader) {
        return new EtlOrchestrator<>(extractor, transformer, loader);
    }
}
