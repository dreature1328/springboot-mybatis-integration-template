package dreature.smit.service;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public interface ExtractService<T> {
    // ----- API 抽取 -----
    // 生成请求参数（测试用）
    List<Map<String, String>> generateParams(int count);

    // 单次请求
    List<JsonNode> request(Map<String, ?> params);

    // 依次请求
    List<JsonNode> request(Map<String, ?>... paramsArray);

    // 单批异步请求
    List<JsonNode> requestBatch(List<? extends Map<String, ?>> paramsList);

    // 分批异步请求
    List<JsonNode> requestBatch(List<? extends Map<String, ?>> paramsList, int batchSize);

    // 生成响应内容（测试用）
    List<JsonNode> generateResponses(int count);

    // ----- 数据库抽取 -----
    // 查询总数
    int countAll();

    // 查询全表
    List<T> findAll();

    // 查询 n 条
    List<T> findRandomN(int count);

    // 分批查询
    List<T> selectBatchByIds(List<String> ids, int batchSize);

    // ----- 消息队列抽取 -----
    // 单次同步接收
    JsonNode receive();

    // 依次同步接收（指定数量）
    List<JsonNode> receive(int count);

    // 依次同步接收（所有消息）
    List<JsonNode> receiveAll();

}
