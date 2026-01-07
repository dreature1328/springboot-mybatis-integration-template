package xyz.dreature.smit.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.w3c.dom.Document;
import xyz.dreature.smit.common.util.JsonUtils;
import xyz.dreature.smit.common.util.XmlUtils;
import xyz.dreature.smit.service.FileService;
import xyz.dreature.smit.service.impl.FileServiceImpl;

import java.util.concurrent.Executor;

// 文件配置
@Configuration
public class FileConfig {
    // 指定解析方法、线程池
    @Bean
    @Lazy
    public FileService<JsonNode> standardJsonFileService(@Qualifier("ioExecutor") Executor executor) {
        return new FileServiceImpl<>(
                "file11",
                JsonUtils::parseFile,
                executor
        );
    }

    @Bean
    @Lazy
    public FileService<Document> standardXmlFileService(@Qualifier("ioExecutor") Executor executor) {
        return new FileServiceImpl<>(
                "file12",
                XmlUtils::parseFile,
                executor
        );
    }

    // 指定解析方法、线程池
    @Bean
    @Lazy
    public FileService<JsonNode> advancedJsonFileService(@Qualifier("ioExecutor") Executor executor) {
        return new FileServiceImpl<>(
                "file21",
                JsonUtils::parseFile,
                executor
        );
    }

    @Bean
    @Lazy
    public FileService<Document> advancedXmlFileService(@Qualifier("ioExecutor") Executor executor) {
        return new FileServiceImpl<>(
                "file22",
                XmlUtils::parseFile,
                executor
        );
    }
}
