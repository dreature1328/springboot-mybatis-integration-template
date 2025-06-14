package dreature.smit.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dreature.smit.common.util.JsonUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {

    // JSON 处理
    @Bean
    public ObjectMapper objectMapper() { return JsonUtils.DEFAULT_MAPPER; }
}
