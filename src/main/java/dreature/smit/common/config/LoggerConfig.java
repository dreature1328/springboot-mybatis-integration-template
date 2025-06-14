package dreature.smit.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerConfig {
    // 日志记录
    @Bean
    public Logger logger() { return LoggerFactory.getLogger("GlobalLogger"); }
}
