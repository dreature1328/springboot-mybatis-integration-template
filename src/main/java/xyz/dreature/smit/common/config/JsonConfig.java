package xyz.dreature.smit.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.dreature.smit.common.util.JsonUtils;

// JSON 配置
@Configuration
public class JsonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = JsonUtils.DEFAULT_MAPPER;
        // 支持 Java 8 日期时间
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
