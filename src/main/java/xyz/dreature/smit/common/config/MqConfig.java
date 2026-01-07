package xyz.dreature.smit.common.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import xyz.dreature.smit.common.model.entity.db1.StandardEntity;
import xyz.dreature.smit.common.util.MqUtils;
import xyz.dreature.smit.service.MqService;
import xyz.dreature.smit.service.impl.MqServiceImpl;

// 消息队列配置
@Configuration
public class MqConfig {
    // 指定模板、默认消息属性、实体类
    @Bean
    @Lazy
    public MqService<StandardEntity, StandardEntity> mqService(RabbitTemplate rabbitTemplate) {
        return new MqServiceImpl<>(
                rabbitTemplate,
                MqUtils.createDefaultMessageProperties(),
                StandardEntity.class
        );
    }

    // 配置消息转换器，需与配置文件一致
    @Bean
    @Lazy
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
