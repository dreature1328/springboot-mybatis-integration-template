package xyz.dreature.smit.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.dreature.smit.common.util.HttpUtils;
import xyz.dreature.smit.service.ApiService;
import xyz.dreature.smit.service.impl.ApiServiceImpl;

import java.util.Map;

// API 服务配置
@Configuration
public class ApiConfig {
    // 显式声明泛型 Bean，以解决泛型擦除导致的依赖注入失败的问题
    @Bean
    public ApiService<JsonNode> apiService(
            @Value("${app.api.baseUrl}") String baseUrl,
            @Value("${app.api.method}") String method,
            @Value("${app.api.header.key}") String headerKey,
            @Value("${app.api.header.value}") String headerValue
    ) {
        Map<String, String> headers = HttpUtils.createDefaultHeaders();
        headers.put(headerKey, headerValue);

        return new ApiServiceImpl<>(
                JsonNode.class,
                baseUrl,
                method,
                headers
        );
    }
}
