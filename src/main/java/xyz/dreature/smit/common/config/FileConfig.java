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

@Configuration
public class FileConfig {
    // 显式声明泛型 Bean，以解决泛型擦除导致的依赖注入失败的问题
    @Bean
    @Lazy
    public FileService<JsonNode> jsonFileService(@Qualifier("ioExecutor") Executor executor) {
        return new FileServiceImpl<>(
                JsonUtils::parseFile,
                executor
        );
    }

    @Bean
    @Lazy
    public FileService<Document> xmlFileService(@Qualifier("ioExecutor") Executor executor) {
        return new FileServiceImpl<>(
                XmlUtils::parseFile,
                executor
        );
    }
}
