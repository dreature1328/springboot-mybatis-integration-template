package xyz.dreature.smit.service.impl;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import xyz.dreature.smit.common.util.BatchUtils;
import xyz.dreature.smit.common.util.MqUtils;
import xyz.dreature.smit.service.MqService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// 消息队列服务
public class MqServiceImpl<S, T> implements MqService<S, T> {
    private final RabbitTemplate rabbitTemplate;
    private final MessageProperties messageProperties;
    private final Class<S> sourceType;
    private final ParameterizedTypeReference<S> singleTypeRef;
    private final ParameterizedTypeReference<List<S>> batchTypeRef;

    public MqServiceImpl(
            RabbitTemplate rabbitTemplate,
            MessageProperties messageProperties,
            Class<S> sourceType
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageProperties = messageProperties;
        this.sourceType = sourceType;
        this.singleTypeRef = MqUtils.createTypeRef(sourceType);
        this.batchTypeRef = MqUtils.createListTypeRef(sourceType);
    }

    // ===== 消息队列抽取 =====
    @Override
    public int countAll() {
        return rabbitTemplate.execute(channel -> {
            try {
                AMQP.Queue.DeclareOk declareOk = channel.queueDeclarePassive(rabbitTemplate.getDefaultReceiveQueue());
                return declareOk.getMessageCount();
            } catch (IOException e) {
                throw new RuntimeException("获取队列消息数量失败", e);
            }
        });
    }

    // 多项同步接收（指定项数）
    private List<S> baseReceive(int count, MessageReceiver<S> receiver) {
        if (count <= 0) return Collections.emptyList();

        List<S> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                S message = receiver.receive();
                if (message == null) break;
                messages.add(message);
            } catch (Exception e) {
                System.err.println("消息接收失败: " + e.getMessage());
            }
        }
        return messages;
    }

    // 单项同步接收（自定义转换）
    @Override
    public S receive() {
        try {
            return MqUtils.receive(rabbitTemplate, singleTypeRef);
        } catch (Exception e) {
            System.err.println("批量消息接收失败: " + e.getMessage());
            return null;
        }
    }

    // 单项同步接收（模板转换器）
    @Override
    public S receiveWithConverter() {
        return rabbitTemplate.receiveAndConvert(singleTypeRef);
    }

    // 多项同步接收（指定项数，自定义转换）
    @Override
    public List<S> receive(int count) {
        return baseReceive(count, this::receive);
    }

    // 多项同步接收（指定项数，模板转换器）
    @Override
    public List<S> receiveWithConverter(int count) {
        return baseReceive(count, this::receiveWithConverter);
    }

    // 多批同步接收（指定批数）
    private List<S> baseReceiveBatch(int count, MessageReceiver<List<S>> receiver) {
        if (count <= 0) return Collections.emptyList();

        List<S> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                List<S> batch = receiver.receive();
                if (batch == null) break;
                messages.addAll(batch);
            } catch (Exception e) {
                System.err.println("消息接收失败: " + e.getMessage());
            }
        }
        return messages;
    }

    // 单批同步接收（自定义转换）
    @Override
    public List<S> receiveBatch() {
        try {
            return MqUtils.receive(rabbitTemplate, batchTypeRef);
        } catch (Exception e) {
            System.err.println("批量消息接收失败: " + e.getMessage());
            return null;
        }
    }

    // 单批同步接收（模板转换器）
    @Override
    public List<S> receiveBatchWithConverter() {
        return rabbitTemplate.receiveAndConvert(batchTypeRef);
    }

    // 多批同步接收（指定批数，自定义转换）
    @Override
    public List<S> receiveBatch(int count) {
        return baseReceiveBatch(count, this::receiveBatch);
    }

    // 多批同步接收（指定批数，模板转换器）
    @Override
    public List<S> receiveBatchWithConverter(int count) {
        return baseReceiveBatch(count, this::receiveBatchWithConverter);
    }

    // 逐项发送（异步回调）
    private int baseSend(List<T> payloads, MessageSender<T> sender) {
        if (payloads == null || payloads.isEmpty()) return 0;

        final int size = payloads.size();
        List<CorrelationData> correlationpayloads = new ArrayList<>(size);

        for (T payload : payloads) {
            CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());
            correlationpayloads.add(cd);
            try {
                sender.send(payload, cd);
            } catch (Exception e) {
                System.err.println("消息发送失败: " + e.getMessage());
                correlationpayloads.remove(cd);  // 发送失败的不需要等待确认
            }
        }

        return handleConfirmations(correlationpayloads);
    }

//    // 异步监听
//    @RabbitListener(queues = "${spring.rabbitmq.template.routing-key}")
//    public void listen(Message message) {
//        byte[] payload = message.getBody();
//        MessageProperties messageProperties = message.getMessageProperties();
//        S parsedMessage = null;
//        try {
//            parsedMessage = MqUtils.parseMessage(message, singleTypeRef);
//            // TODO 根据需要实现消息处理的业务逻辑
//        } catch (Exception e) {
//            System.err.println("监听失败: " + e.getMessage());
//        }
//    }

    // 逐项发送（异步回调，自定义转换）
    @Override
    public int send(List<T> payloads) {
        return baseSend(payloads, (payload, cd) ->
                MqUtils.send(rabbitTemplate, payload, messageProperties, cd)
        );
    }

    // 逐项发送（异步回调，模板转换器）
    @Override
    public int sendWithConverter(List<T> payloads) {
        return baseSend(payloads, (payload, cd) -> {
            Message message = rabbitTemplate.getMessageConverter()
                    .toMessage(payload, messageProperties);
            rabbitTemplate.send(
                    rabbitTemplate.getExchange(),
                    rabbitTemplate.getRoutingKey(),
                    message,
                    cd
            );
        });
    }

    // 单批发送（异步回调）
    private int baseSendBatch(List<T> payloads, MessageSender<List<T>> sender) {
        if (payloads == null || payloads.isEmpty()) return 0;

        try {
            CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());
            sender.send(payloads, cd);
            return handleConfirmation(cd);
        } catch (Exception e) {
            System.err.println("批量消息发送失败: " + e.getMessage());
            return 0;
        }
    }

    // 单批发送（异步回调，自定义转换）
    @Override
    public int sendBatch(List<T> payloads) {
        return baseSendBatch(payloads, (batch, cd) ->
                MqUtils.send(rabbitTemplate, batch, messageProperties, cd)
        );
    }

    // 单批发送（异步回调，模板转换器）
    @Override
    public int sendBatchWithConverter(List<T> payloads) {
        return baseSendBatch(payloads, (batch, cd) -> {
            Message message = rabbitTemplate.getMessageConverter()
                    .toMessage(batch, messageProperties);
            rabbitTemplate.send(
                    rabbitTemplate.getExchange(),
                    rabbitTemplate.getRoutingKey(),
                    message,
                    cd
            );
        });
    }

    // 分批发送（异步回调，自定义转换）
    @Override
    public int sendBatch(List<T> payloads, int batchSize) {
        return BatchUtils.reduceBatchToInt(payloads, batchSize, this::sendBatch);
    }

    // 分批发送（异步回调，模板转换器）
    @Override
    public int sendBatchWithConverter(List<T> payloads, int batchSize) {
        return BatchUtils.reduceBatchToInt(payloads, batchSize, this::sendBatchWithConverter);
    }

    // ===== 异步回调的确认处理 =====
    // 启用异步回调需配置 spring.rabbitmq.publisher-confirm-type=correlated
    // 单项处理
    private int handleConfirmation(CorrelationData cd) {
        try {
            // 等待确认结果，最多等待5秒
            CorrelationData.Confirm confirm = cd.getFuture().get(5, TimeUnit.SECONDS);

            if (confirm.isAck()) {
                return 1;
            } else {
                System.err.println("消息发送失败: " + confirm.getReason());
                return 0;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("消息确认等待被中断");
            return 0;
        } catch (ExecutionException | TimeoutException e) {
            System.err.println("消息确认异常: " + e.getMessage());
            return 0;
        }
    }

    // 逐项处理
    private int handleConfirmations(List<CorrelationData> correlationpayloads) {
        if (correlationpayloads.isEmpty()) return 0;
        return BatchUtils.reduceEachToInt(correlationpayloads, this::handleConfirmation);
    }

    // 单项数据消息接收接口
    @FunctionalInterface
    private interface MessageReceiver<S> {
        S receive() throws IOException;
    }

    // ===== 消息队列加载 =====
    // 发送方式接口
    @FunctionalInterface
    public interface MessageSender<T> {
        void send(T payload, CorrelationData correlationData) throws Exception;
    }
}
