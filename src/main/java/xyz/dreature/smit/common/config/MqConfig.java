package xyz.dreature.smit.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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
    public MqService<Data, Data> mqService(RabbitTemplate rabbitTemplate) {
        return new MqServiceImpl<>(
                rabbitTemplate,
                MqUtils.createDefaultMessageProperties(),
                Data.class
        );
    }

    // 配置消息转换器，需与配置文件一致
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

//    // 此处为手动配置方式，注释掉即交由 Spring Boot 自动配置
//    // 配置模板类
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(jsonMessageConverter());
//        return template;
//    }
//
//    // 配置监听器容器工厂，为 @RabbitListener 注解提供底层容器配置
//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
//            ConnectionFactory connectionFactory,
//            MessageConverter jackson2JsonMessageConverter) {
//
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(jackson2JsonMessageConverter);
//        factory.setBatchListener(true); // 启用批处理监听
//        return factory;
//    }
}
