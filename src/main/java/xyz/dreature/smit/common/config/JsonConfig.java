package xyz.dreature.smit.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.dreature.smit.common.util.JsonUtils;

// JSON 处理相关配置
@Configuration
public class JsonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return JsonUtils.DEFAULT_MAPPER;
    }
}
