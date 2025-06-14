package dreature.smit.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dreature.smit.common.stats.IntegrationStats;
import dreature.smit.common.util.HttpUtils;
import dreature.smit.common.util.JsonUtils;
import dreature.smit.entity.Data;
import dreature.smit.mapper.DataMapper;
import dreature.smit.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static dreature.smit.common.util.BatchUtils.*;
import static dreature.smit.common.util.HttpUtils.sendAsyncHttpRequest;
import static dreature.smit.common.util.HttpUtils.sendHttpRequest;
import static dreature.smit.common.util.XmlUtils.readXmlFile;

@Service
public class DataServiceImpl extends BaseServiceImpl<Data> implements DataService {
    @Autowired
    private DataMapper dataMapper;

    // ----- 数据集成 -----
    // 集成
    public IntegrationStats integrate(List<? extends Map<String,?>> paramsList) {
        IntegrationStats stats = new IntegrationStats();

        stats.markStart();
        stats.recordRequests(paramsList.size());

        // 同步请求并获取响应内容
//        List<String> responses = mapEach(paramsList, this::request); // 实际请求
        List<String> responses = generateResponses(paramsList.size()); // 测试用
        stats.markRequestEnd();
        stats.recordResponses(responses.size());

        // 依次转换数据，将响应内容转换成对象列表
        List<Data> dataList = mapEach(responses, this::transform);
        stats.markTransformEnd();
        stats.recordEntities(dataList.size());

        // 将对象依次插入或更新进数据库
        int affectedRows = reduceEach(dataList, dataMapper::upsert);
        stats.markPersistEnd();
        stats.recordAffectedRows(affectedRows);

        return stats;
    }

    // 优化集成
    public List<IntegrationStats> integrateOptimized(List<? extends Map<String,?>> paramsList) {
        List statsList = new ArrayList<>();
        IntegrationStats stats = new IntegrationStats();

        stats.markStart();
        stats.recordRequests(paramsList.size());
        // 分页异步请求并获取响应内容
//        List<String> responses = requestPage(paramsList); // 实际请求
        List<String> responses = generateResponses(paramsList.size()); // 测试用
        stats.markRequestEnd();
        stats.recordResponses(responses.size());

        // 流水线转换数据，将响应内容转换成对象列表
        List<Data> dataList = transformPipeline(responses);
        stats.markTransformEnd();
        stats.recordEntities(dataList.size());

        // 将对象分页插入或更新进数据库
        int affectedRows = upsertPage(dataList);
        stats.markPersistEnd();
        stats.recordAffectedRows(affectedRows);

        statsList.add(stats);
        return statsList;
    }

    // 分页优化集成
    public List<IntegrationStats> integratePageOptimized(List<? extends Map<String,?>> paramsList){
        int pageSize = 300;
        List<IntegrationStats> result = mapPage(paramsList, pageSize, this::integrateOptimized);
        return result;
    }

    // ----- 数据获取 -----
    // 生成数据（测试用）
    public List<Data> generate(int count) {
        // 此处以 UUID 作为 ID，长度为 16 的随机字符串作为属性值为例
        List<Data> dataList = new ArrayList<>(count);
        String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        int length = 16;

        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < count; i++) {
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

    // 添加请求头（测试用）
    private static Map<String, String> headers = HttpUtils.getDefaultHeaders();
    static {
        final String key = "key";
        final String value = "abcdefg";
        headers.put(key, value);
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
    public String request(Map<String, ?> params) {
        String baseUrl = "http://www.example.com";
        String method = "GET";
        String response = null;
        try {
            response = sendHttpRequest(baseUrl, method, headers, params);
        }
        catch (Exception e) {
            System.err.println("请求失败: " + e.getMessage());
        }
        return response;
    }

    // 批量异步请求
    public List<String> requestBatch(List<? extends Map<String,?>> paramsList) {
        String baseUrl = "http://www.example.com";
        String method = "GET";
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

    // 分页异步请求
    public List<String> requestPage(List<? extends Map<String,?>> paramsList) {
        int pageSize = 300;
        return mapPage(paramsList, pageSize, this::requestBatch);
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
    // ----- 数据转换 -----
    // 单项转换
    public Data transform(String response) {
        Data data = null;
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode arrayNode = rootNode.path("data");

            if (arrayNode.isArray()) {
                for (JsonNode jsonNode : arrayNode) {
                    data = new Data(
                            jsonNode.path("id").asText(),
                            jsonNode.path("key1").asText(),
                            jsonNode.path("key2").asText()
                    );
                }
            }
        } catch (JsonProcessingException e) {
            System.err.println("JSON 处理异常: " + e.getMessage());
        }

        return data;
    }

    // 流水线转换
    public List<Data> transformPipeline(List<String> responses) {
        return responses.stream()
                .map(JsonUtils::stringToJsonNode)
                .filter(Objects::nonNull)
                .flatMap(jsonObj -> {
                    JsonNode arrayNode = jsonObj.path("data");
                    return arrayNode.isArray()
                            ? StreamSupport.stream(arrayNode.spliterator(), false)
                            : Stream.empty();
                })
                .map(jsonNode -> new Data(
                        jsonNode.path("id").asText(),
                        jsonNode.path("key1").asText(),
                        jsonNode.path("key2").asText()
                ))
                .collect(Collectors.toList());
    }

    // ----- 数据持久化 -----
    // 计算页面大小
    public int calculatePageSize(Class<?> clazz) {
        int cells = 12000; // 每页单元格数，即行数 × 列数
        int fields = clazz.getDeclaredFields().length; // 列数
        int pageSize = cells / fields; // 页面大小，即每页行数
        return pageSize;
    }

    // 查询总数
    public int countAll() {
        return dataMapper.countAll();
    };

    // 查询 n 条
    public List<Data> findTopN(int n) {
        return dataMapper.findTopN(n);
    }

    // 分页查询
    public List<Data> selectPageByIds(List<String> ids) {
        int pageSize = calculatePageSize(Data.class);
        return mapPage(ids, pageSize, dataMapper::selectBatchByIds);
    }
    // 分页插入
    public int insertPage(List<Data> dataList) {
        int pageSize = calculatePageSize(Data.class);
        int affectedRows = reducePage(dataList, pageSize, dataMapper::insertBatch);
        return affectedRows;
    }

    // 分页更新
    public int updatePage(List<Data> dataList) {
        int pageSize = calculatePageSize(Data.class);
        int affectedRows = reducePage(dataList, pageSize, dataMapper::updateBatch);
        return affectedRows;
    }

    // 分页插入或更新
    public int upsertPage(List<Data> dataList) {
        int pageSize = calculatePageSize(Data.class);
        int affectedRows = reducePage(dataList, pageSize, dataMapper::upsertBatch);
        return affectedRows;
    }

    // 分页删除
    public int deletePageByIds(List<String> ids) {
        int pageSize = calculatePageSize(Data.class);
        int affectedRows = reducePage(ids, pageSize, dataMapper::deleteBatchByIds);
        return affectedRows;
    }

    // 清空
    public void truncate(){
        dataMapper.truncate();
        return ;
    }
}
