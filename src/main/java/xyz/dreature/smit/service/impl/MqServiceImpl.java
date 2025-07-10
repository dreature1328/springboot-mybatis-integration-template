package xyz.dreature.smit.service.impl;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import xyz.dreature.smit.common.util.BatchUtils;
import xyz.dreature.smit.common.util.MqUtils;
import xyz.dreature.smit.service.MqService;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// 消息队列服务
public class MqServiceImpl<S, T> implements MqService<S, T> {
    private RabbitTemplate rabbitTemplate;
    private MessageProperties messageProperties;
    private Class<S> sourceType;

    public MqServiceImpl(
            RabbitTemplate rabbitTemplate,
            MessageProperties messageProperties,
            Class<S> sourceType
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageProperties = messageProperties;
        this.sourceType = sourceType;
    }

    // ===== 消息队列抽取 =====
    // 单次同步接收
    public S receive() {
        S message = null;
        try {
            message = MqUtils.receive(rabbitTemplate, sourceType);
        } catch (Exception e) {
            System.err.println("消息接收失败: " + e.getMessage());
        }
        return message;
    }

    // 依次同步接收（指定数量）
    public List<S> receive(int count) {
        List<S> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            S message = receive();
            if (message != null) {
                messages.add(message);
            } else {
                // 如果中途队列为空（null），则提前结束
                break;
            }
        }
        return messages;
    }

    // 依次同步接收（所有消息）
    public List<S> receiveAll() {
        List<S> messages = new ArrayList<>();
        S message;
        while ((message = receive()) != null) {
            messages.add(message);
        }
        return messages;
    }

//    // 异步监听
//    @RabbitListener(queues = "${spring.rabbitmq.template.routing-key}")
//    public void listen(Message message) {
//        byte[] payload = message.getBody();
//        MessageProperties messageProperties = message.getMessageProperties();
//        S parsedMessage = null;
//        try {
//            parsedMessage = MqUtils.parseMessage(message, sourceType);
//            // TODO 根据需要实现消息处理的业务逻辑
//        } catch (Exception e) {
//            System.err.println("监听失败: " + e.getMessage());
//        }
//    }

    // ===== 消息发布（中转） =====
    // 逐项发送（异步回调）
    public int send(List<T> dataList) {
        int successCount = 0;
        // 需要设置 spring.rabbitmq.publisher-confirm-type=correlated
        List<CorrelationData> correlationDataList = new ArrayList<>();
        try {
            for (T data : dataList) {
                CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
                correlationDataList.add(correlationData);
                MqUtils.send(rabbitTemplate, data, messageProperties, correlationData);
            }

            for (CorrelationData cd : correlationDataList) {
                try {
                    // 等待确认结果，最多等待5秒
                    CorrelationData.Confirm confirm = cd.getFuture().get(5, TimeUnit.SECONDS);
                    if (confirm.isAck()) {
                        successCount++;
                    } else {
                        // 记录失败原因
                        System.err.println("消息发送失败: " + confirm.getReason());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("等待消息确认时被中断");
                } catch (ExecutionException | TimeoutException e) {
                    System.err.println("获取消息确认结果时发生异常" + e);
                }
            }
            return successCount;
        } catch (IOException | TransformerException e) {
            throw new RuntimeException("消息发送异常", e);
        }
    }

    // 单批发送（异步回调）
    public int sendBatch(List<T> dataList) {
        // 以批大小作为一条消息的数据量（列表大小）
        try {
            CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());
            MqUtils.send(rabbitTemplate, dataList, messageProperties, cd);

            try {
                // 等待确认结果，最多等待5秒
                CorrelationData.Confirm confirm = cd.getFuture().get(5, TimeUnit.SECONDS);
                if (confirm.isAck()) {
                    return 1;
                } else {
                    // 记录失败原因
                    System.err.println("批量消息发送失败: " + confirm.getReason());
                    return 0;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("等待批量消息确认时被中断");
                return 0;
            } catch (ExecutionException | TimeoutException e) {
                System.err.println("获取批量消息确认结果时发生异常:" + e);
                return 0;
            }
        } catch (IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    // 分批发送（异步回调）
    public int sendBatch(List<T> dataList, int batchSize) {
        return BatchUtils.reduceBatchToInt(dataList, batchSize, this::sendBatch);
    }
}
