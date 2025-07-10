package xyz.dreature.smit.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MqUtils {
    // ===== 常量 / 配置 =====
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    // 创建默认消息属性
    public static MessageProperties createDefaultMessageProperties() {
        MessageProperties props = new MessageProperties();

        props.setContentType(MessageProperties.DEFAULT_CONTENT_TYPE);
        props.setContentEncoding(DEFAULT_CHARSET.name());
        props.setDeliveryMode(MessageProperties.DEFAULT_DELIVERY_MODE);
        props.setPriority(MessageProperties.DEFAULT_PRIORITY);

        props.setHeader("Accept", "application/json");
        props.setHeader("User-Agent", "AMQP-JavaClient");

        return props;
    }

    // ===== 构建消息 =====
    // 构建消息
    public static Message buildMessage(Object payload) throws TransformerException, IOException {
        return buildMessage(payload, (MessageProperties) null);
    }

    // 构建消息（含头）
    public static Message buildMessage(Object payload, Map<String, ?> headers)
            throws TransformerException, IOException {
        MessageProperties properties = new MessageProperties();
        if (headers != null) {
            properties.getHeaders().putAll(headers);
        }
        return buildMessage(payload, properties);
    }

    // 构建消息（含属性）
    public static Message buildMessage(Object payload, MessageProperties properties)
            throws TransformerException, IOException {

        if (properties == null) {
            properties = new MessageProperties();
        }

        // 确保内容类型设置正确
        determineContentType(payload, properties);

        // 构建消息体
        byte[] body = serializePayload(payload);

        return MessageBuilder.withBody(body)
                .andProperties(properties)
                .build();
    }

    // 根据负载类型推断并设置内容类型（消息属性的“Content Type”）
    public static void determineContentType(Object payload, MessageProperties properties) {
        if (payload instanceof String) {
            properties.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
        } else if (payload instanceof byte[]) {
            properties.setContentType(MessageProperties.CONTENT_TYPE_BYTES);
        } else if (payload instanceof Document) {
            properties.setContentType(MessageProperties.CONTENT_TYPE_XML);
        } else if (payload instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) payload;
            Object firstElement = null;
            for (Object element : iterable) {
                firstElement = element; // 获取第一个元素
                break;
            }
            if (firstElement != null) {
                determineContentType(firstElement, properties); // 递归判断首元素的类型
            } else {
                properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            }
        } else if (payload.getClass().isArray()) {
            int length = Array.getLength(payload);
            if (length > 0) {
                Object firstElement = Array.get(payload, 0);
                determineContentType(firstElement, properties); // 递归判断首元素的类型
            } else {
                properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            }
        } else {
            properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        }
    }

    // 序列化负载为字节数组（byte[]）
    public static byte[] serializePayload(Object payload)
            throws TransformerException, IOException {
        if (payload instanceof byte[]) {
            return (byte[]) payload;
        } else if (payload instanceof String) {
            return ((String) payload).getBytes(DEFAULT_CHARSET);
        } else if (payload instanceof Document) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                Transformer transformer = XmlUtils.transformerFactory.newTransformer();
                transformer.transform(
                        new DOMSource((Document) payload),
                        new StreamResult(outputStream)
                );
                return outputStream.toByteArray();
            }
        } else {
            return JsonUtils.DEFAULT_MAPPER.writeValueAsBytes(payload);
        }
    }

    // ===== 发送消息 =====
    // 同步发送消息
    public static void send(RabbitTemplate rabbitTemplate, Object payload, MessageProperties props, CorrelationData correlationData) throws IOException, TransformerException {
        Message message = buildMessage(payload, props);
        String exchange = rabbitTemplate.getExchange();
        String routingKey = rabbitTemplate.getRoutingKey();

        rabbitTemplate.send(exchange, routingKey, message, correlationData);
    }

    // 异步发送消息
    public static CompletableFuture<Void> sendAsync(RabbitTemplate rabbitTemplate, Object payload, MessageProperties props, CorrelationData correlationData) {
        return CompletableFuture.runAsync(() -> {
            try {
                send(rabbitTemplate, payload, props, correlationData);
            } catch (IOException | TransformerException e) {
                throw new CompletionException("异步消息发送失败", e);
            }
        });
    }

    // ===== 接收与解析消息 =====
    // 同步接收单条消息并解析
    public static <T> T receive(RabbitTemplate rabbitTemplate, Class<T> targetType) throws IOException {
        Message message = rabbitTemplate.receive();
        return parseMessage(message, targetType);
    }

    // 同步接收多条消息并解析
    public static <T> List<T> receive(RabbitTemplate rabbitTemplate, int count, Class<T> targetType) throws IOException {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Message message = rabbitTemplate.receive();
            T parsedMessage = parseMessage(message, targetType);
            if (parsedMessage != null) {
                result.add(parsedMessage);
            }
        }
        return result;
    }

    // 同步接收消息并返回字节数组（byte[]）
    public static byte[] receiveAsBytes(RabbitTemplate rabbitTemplate) throws IOException {
        return receive(rabbitTemplate, byte[].class);
    }

    // 同步接收消息并返回字符串（String）
    public static String receiveAsString(RabbitTemplate rabbitTemplate) throws IOException {
        return receive(rabbitTemplate, String.class);
    }

    // 同步接收消息并返回 JSON（JsonNode）
    public static JsonNode receiveAsJson(RabbitTemplate rabbitTemplate) throws IOException {
        return receive(rabbitTemplate, JsonNode.class);
    }

    // 同步接收消息并返回 XML（Document）
    public static Document receiveAsXml(RabbitTemplate rabbitTemplate) throws IOException {
        return receive(rabbitTemplate, Document.class);
    }

    // 解析消息为结构化类型
    public static <T> T parseMessage(Message message, Class<T> targetType) throws IOException {
        if (message == null) return null;
        byte[] messageBytes = message.getBody();
        MessageProperties messageProperties = message.getMessageProperties();
        String contentType = messageProperties != null ? messageProperties.getContentType() : null;

        // 基于预定义类型解析
        if (targetType == byte[].class) {
            return targetType.cast(messageBytes);
        } else if (targetType == String.class) {
            return targetType.cast(parseString(messageBytes));
        } else if (JsonNode.class.isAssignableFrom(targetType)) {
            return targetType.cast(parseJson(messageBytes));
        } else if (Document.class.isAssignableFrom(targetType)) {
            return targetType.cast(parseXml(messageBytes));
        } else if (targetType == Object.class && contentType != null) {
            // 无明确预定义类型时，基于内容类型推测
            if (contentType.contains("json")) {
                return targetType.cast(parseJson(messageBytes));
            } else if (contentType.contains("xml")) {
                return targetType.cast(parseXml(messageBytes));
            } else if (contentType.contains("text")) {
                return targetType.cast(parseString(messageBytes));
            }
        }
        // 最终回退逻辑
        throw new IOException("不受支持的类型解析");
    }

    // 解析字节数组为字符串（String）（默认编码）
    public static String parseString(byte[] bytes) {
        return parseString(bytes, DEFAULT_CHARSET);
    }

    // 解析字节数组为字符串（String）（指定编码）
    public static String parseString(byte[] bytes, Charset charset) {
        return new String(bytes, charset);
    }

    // 从消息属性中获取字符编码
    public static Charset getCharset(Message message) {
        String charsetName = message.getMessageProperties().getContentEncoding();
        if (charsetName == null) {
            return DEFAULT_CHARSET;
        }
        return Charset.forName(charsetName);
    }

    // 解析字节数组为 JSON（JsonNode）
    public static JsonNode parseJson(byte[] bytes) throws IOException {
        try {
            return JsonUtils.DEFAULT_MAPPER.readTree(bytes);
        } catch (JsonProcessingException e) {
            throw new IOException("JSON 解析失败", e);
        }
    }

    // 解析字节数组为 XML（Document）
    public static Document parseXml(byte[] bytes) throws IOException {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
            return XmlUtils.documentBuilder.parse(stream);
        } catch (SAXException e) {
            throw new IOException("XML 解析失败", e);
        }
    }

    // ===== 队列管理 =====
    // 检查队列存在
    public static boolean isQueueExists(RabbitAdmin rabbitAdmin, String queueName) {
        return rabbitAdmin.getQueueProperties(queueName) != null;
    }

    // 获取队列信息
    public static QueueInformation getQueueInfo(RabbitAdmin rabbitAdmin, String queueName) {
        return rabbitAdmin.getQueueInfo(queueName);
    }

    // 创建队列
    public static void createQueue(RabbitAdmin rabbitAdmin, String queueName) {
        boolean durable = true;   // 持久化
        boolean exclusive = false;  // 排他性
        boolean autoDelete = false; // 自动删除与否
        Queue queue = new Queue(queueName, durable, exclusive, autoDelete, null);
        rabbitAdmin.declareQueue(queue);
    }

    // 绑定队列
    public static void bindQueue(RabbitAdmin rabbitAdmin,
                                 String queueName,
                                 String exchangeName,
                                 String routingKey) {
        Binding binding = new Binding(
                queueName,
                Binding.DestinationType.QUEUE,
                exchangeName,
                routingKey,
                null
        );
        rabbitAdmin.declareBinding(binding);
    }

    // 删除队列
    public static boolean deleteQueue(RabbitAdmin rabbitAdmin, String queueName) {
        return isQueueExists(rabbitAdmin, queueName) && rabbitAdmin.deleteQueue(queueName);
    }

    // 清空队列
    public static void purgeQueue(RabbitAdmin rabbitAdmin, String queueName) {
        rabbitAdmin.purgeQueue(queueName, false);
    }


}
