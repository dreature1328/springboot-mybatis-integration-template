package dreature.smit.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import dreature.smit.common.util.HttpUtils;
import dreature.smit.entity.Data;
import dreature.smit.service.ExtractService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static dreature.smit.common.util.BatchUtils.mapBatch;
import static dreature.smit.common.util.BatchUtils.mapEach;
import static dreature.smit.common.util.HttpUtils.sendAsyncHttpRequest;
import static dreature.smit.common.util.HttpUtils.sendHttpRequest;
import static dreature.smit.common.util.XmlUtils.readXmlFile;

@Service
public class DataExtractServiceImpl extends BaseServiceImpl<Data> implements ExtractService<Data> {
    // ----- 文件提取 -----
    // 生成数据（测试用）
    public List<Data> generate(int count) {
        // 此处以 UUID 作为 ID，长度为 16 的随机字符串作为属性值为例
        List<Data> dataList = new ArrayList<>(count);
        String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        int length = 16;

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < count; i++) {
            String id = UUID.randomUUID().toString();

            sb.setLength(0);
            for (int j = 0; j < length; j++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            String attr1 = sb.toString();

            sb.setLength(0);
            for (int j = 0; j < length; j++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            String attr2 = sb.toString();

            dataList.add(new Data(id, attr1, attr2));
        }
        return dataList;
    }

    // 解析 JSON 数据（测试用）
    public List<Data> parseFromJson(String filePath) {
        List<Data> dataList = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(new File(filePath));
            JsonNode arrayNode = rootNode.path("data");

            if (arrayNode.isArray()) {
                for (JsonNode jsonNode : arrayNode) {
                    Data data = new Data(
                            jsonNode.path("id").asText(),
                            jsonNode.path("key1").asText(),
                            jsonNode.path("key2").asText()
                    );
                    dataList.add(data);
                }
            }
        } catch (IOException e) {
            System.err.println("读取 JSON 文件失败: " + e.getMessage());
        }
        return dataList;
    }

    // 解析 XML 数据（测试用）
    public List<Data> parseFromXml(String filePath) {
        List<Data> dataList = new ArrayList<>();

        Document doc = readXmlFile(filePath);
        doc.getDocumentElement().normalize();
        NodeList dataNodes = doc.getElementsByTagName("data");
        Element dataElement = (Element) dataNodes.item(0);
        NodeList itemNodes = dataElement.getElementsByTagName("item");

        for (int i = 0; i < itemNodes.getLength(); i++) {
            Node itemNode = itemNodes.item(i);

            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) itemNode;
                Data data = new Data(
                        itemElement.getElementsByTagName("id").item(0).getTextContent(),
                        itemElement.getElementsByTagName("key1").item(0).getTextContent(),
                        itemElement.getElementsByTagName("key2").item(0).getTextContent()
                );
                dataList.add(data);
            }
        }
        return dataList;
    }

    // ----- API 提取 -----
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
        headers = HttpUtils.getDefaultHeaders(); // 获取默认请求头
        headers.put(headerKey, headerValue);    // 添加自定义请求头
    }

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
    public List<String> request(Map<String, ?> params) {
        List<String> responses = new ArrayList<>();
        try {
            responses.add(sendHttpRequest(baseUrl, method, headers, params));
        } catch (Exception e) {
            System.err.println("请求失败: " + e.getMessage());
        }
        return responses;
    }

    // 依次请求
    public List<String> request(Map<String, ?>... paramsArray) {
        return mapEach(new ArrayList<>(Arrays.asList(paramsArray)), this::request);
    }

    // 单批异步请求
    public List<String> requestBatch(List<? extends Map<String, ?>> paramsList) {
        List<CompletableFuture<String>> futures = new ArrayList<>();

        // 添加异步请求任务
        for (Map<String, ?> params : paramsList) {
            CompletableFuture<String> future = sendAsyncHttpRequest(baseUrl, method, headers, params);
            futures.add(future);
        }

        // 等待异步任务完成，超时时间为 30 分钟
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(1800, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("请求超时");
            e.printStackTrace();
            return Collections.emptyList();
        }

        // 将所有异步请求的结果获取为 List<String>
        List<String> responses = new ArrayList<>();
        for (CompletableFuture<String> future : futures) {
            try {
                responses.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return responses;
    }

    // 分批异步请求
    public List<String> requestBatch(List<? extends Map<String, ?>> paramsList, int batchSize) {
        return mapBatch(paramsList, batchSize, this::requestBatch);
    }

    // 生成响应内容（测试用）
    public List<String> generateResponses(int count) {
        final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final SecureRandom random = new SecureRandom();
        final int attrLength = 16;

        List<String> responses = new ArrayList<>(count);

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

            responses.add(jsonBuilder.toString());
        }

        return responses;
    }

    // ----- 数据库提取 -----
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
        return mapBatch(ids, batchSize, baseMapper::selectBatchByIds);
    }

}
