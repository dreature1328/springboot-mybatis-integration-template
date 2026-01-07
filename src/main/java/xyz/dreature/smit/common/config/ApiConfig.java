package xyz.dreature.smit.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import xyz.dreature.smit.common.util.HttpUtils;
import xyz.dreature.smit.service.ApiService;
import xyz.dreature.smit.service.impl.ApiServiceImpl;

import java.util.Map;
import java.util.concurrent.Executor;

// API 配置
@Configuration
public class ApiConfig {
    // 指定基础 URL、方法、鉴权键值、线程池
    @Bean
    @Lazy
    public ApiService<JsonNode> apiService(
            @Value("${app.api.baseUrl}") String baseUrl,
            @Value("${app.api.method}") String method,
            @Value("${app.api.header.key}") String headerKey,
            @Value("${app.api.header.value}") String headerValue,
            @Qualifier("ioExecutor") Executor executor
    ) {
        Map<String, String> headers = HttpUtils.createDefaultHeaders();
        headers.put(headerKey, headerValue);

        return new ApiServiceImpl<>(
                JsonNode.class,
                baseUrl,
                method,
                headers,
                executor
        );
    }
}
