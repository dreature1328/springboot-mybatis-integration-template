package dreature.smit.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dreature.smit.common.util.BatchUtils;
import dreature.smit.common.util.HttpUtils;
import dreature.smit.common.util.JsonUtils;
import dreature.smit.common.util.MqUtils;
import dreature.smit.entity.Data;
import dreature.smit.service.ExtractService;
import dreature.smit.service.TransformService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Service
public class DataExtractServiceImpl extends BaseServiceImpl<Data> implements ExtractService<Data> {
    @Autowired
    TransformService<Data> transformService;

    // ----- API 抽取 -----
    // 请求 URL、方法、头
    @Value("${api.baseUrl}")
    private String baseUrl;

    @Value("${api.method}")
    private String method;

    private Map<String, String> headers;

    @Value("${api.header.key}")
    private String headerKey;

    @Value("${api.header.value}")
    private String headerValue;

    @PostConstruct
    public void init() {
        headers = HttpUtils.createDefaultHeaders(); // 获取默认请求头
        headers.put(headerKey, headerValue);    // 添加自定义请求头
    }

    // ----- API 抽取 -----
    // 生成请求参数（测试用）
    public List<Map<String, String>> generateParams(int count) {
        // 此处以一年中每一天的时间切片为例
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(count).plusDays(1); // 包含起始日
        long numOfDays = ChronoUnit.DAYS.between(startDate, today.plusDays(1)); // 总天数

        List<Map<String, String>> paramsList = new ArrayList<>((int) numOfDays);
        for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
            paramsList.add(Collections.singletonMap("date", date.toString()));
        }
        return paramsList;
    }

    // 单次请求
    public List<JsonNode> request(Map<String, ?> params) {
        List<JsonNode> responses = new ArrayList<>();
        try {
            responses.add(HttpUtils.executeAsJson(baseUrl, method, headers, params));
        } catch (Exception e) {
            System.err.println("请求失败: " + e.getMessage());
        }
        return responses;
    }

    // 依次请求
    public List<JsonNode> request(Map<String, ?>... paramsArray) {
        return BatchUtils.mapEach(new ArrayList<>(Arrays.asList(paramsArray)), this::request);
    }

    // 单批异步请求
    public List<JsonNode> requestBatch(List<? extends Map<String, ?>> paramsList) {
        List<CompletableFuture<JsonNode>> futures = new ArrayList<>();
        try {
            // 添加异步请求任务
            for (Map<String, ?> params : paramsList) {
                CompletableFuture<JsonNode> future = HttpUtils.executeAsyncAsJson(baseUrl, method, headers, params);
                futures.add(future);
            }

            // 等待异步任务完成，超时时间为 30 分钟
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(1800, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.err.println("请求超时");
            e.printStackTrace();
            return Collections.emptyList();
        }

        // 将所有异步请求的结果获取为 List<JsonNode>
        List<JsonNode> responses = new ArrayList<>();
        for (CompletableFuture<JsonNode> future : futures) {
            try {
                responses.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return responses;
    }

    // 分批异步请求
    public List<JsonNode> requestBatch(List<? extends Map<String, ?>> paramsList, int batchSize) {
        return BatchUtils.mapBatch(paramsList, batchSize, this::requestBatch);
    }

    // 生成响应内容（测试用）
    public List<JsonNode> generateResponses(int count) {
        final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final SecureRandom random = new SecureRandom();
        final int attrLength = 16;

        List<JsonNode> responses = new ArrayList<>(count);

        StringBuilder jsonBuilder = new StringBuilder();
        StringBuilder randomBuilder = new StringBuilder(attrLength);

        for (int i = 0; i < count; i++) {
            // 重置构建器（比创建新实例更高效）
            jsonBuilder.setLength(0);
            randomBuilder.setLength(0);

            // 生成UUID
            String id = UUID.randomUUID().toString();

            // 生成第一个随机属性
            for (int j = 0; j < attrLength; j++) {
                randomBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            String attr1 = randomBuilder.toString();

            // 重置并生成第二个随机属性
            randomBuilder.setLength(0);
            for (int j = 0; j < attrLength; j++) {
                randomBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            String attr2 = randomBuilder.toString();

            // 构建完整的API响应JSON
            jsonBuilder.append("{")
                    .append("\"success\": true,")
                    .append("\"code\": \"SUCCESS\",")
                    .append("\"message\": \"操作成功\",")
                    .append("\"data\": [{")
                    .append("\"id\":\"").append(id).append("\",")
                    .append("\"key1\":\"").append(attr1).append("\",")
                    .append("\"key2\":\"").append(attr2).append("\"")
                    .append("}]}")
                    .append("}");
            try {
                responses.add(JsonUtils.DEFAULT_MAPPER.readTree(jsonBuilder.toString()));
            } catch (JsonProcessingException e) {

            }

        }

        return responses;
    }

    // ----- 数据库抽取 -----
    // 查询总数
    public int countAll() {
        return baseMapper.countAll();
    }

    // 查询全表
    public List<Data> findAll() {
        return baseMapper.findAll();
    }

    // 查询 n 条
    public List<Data> findRandomN(int count) {
        return baseMapper.findRandomN(count);
    }

    // 分批查询
    public List<Data> selectBatchByIds(List<String> ids, int batchSize) {
        return BatchUtils.mapBatch(ids, batchSize, baseMapper::selectBatchByIds);
    }

    // ----- 消息队列抽取 -----
    @Autowired
    RabbitTemplate rabbitTemplate;

    // 单次同步接收
    public JsonNode receive() {
        JsonNode message = null;
        try {
            message = MqUtils.receiveAsJson(rabbitTemplate);
        } catch (Exception e) {
            System.err.println("消息接收失败: " + e.getMessage());
        }
        return message;
    }

    // 依次同步接收（指定数量）
    public List<JsonNode> receive(int count) {
        List<JsonNode> messages = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            JsonNode message = receive();
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
    public List<JsonNode> receiveAll() {
        List<JsonNode> messages = new ArrayList<>();
        JsonNode message;
        while ((message = receive()) != null) {
            messages.add(message);
        }
        return messages;
    }

}
