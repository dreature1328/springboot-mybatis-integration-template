package xyz.dreature.smit.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.common.util.MqUtils;
import xyz.dreature.smit.service.MqService;
import xyz.dreature.smit.service.impl.MqServiceImpl;

// 消息队列服务配置
@Configuration
public class MqConfig {
    // 显式声明泛型 Bean，以解决泛型擦除导致的依赖注入失败的问题
    @Bean
    public MqService<JsonNode, Data> mqService(RabbitTemplate rabbitTemplate) {
        return new MqServiceImpl<>(
                rabbitTemplate,
                MqUtils.createDefaultMessageProperties(),
                JsonNode.class
        );
    }
}
