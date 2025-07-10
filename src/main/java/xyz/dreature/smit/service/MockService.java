package xyz.dreature.smit.service;

import com.fasterxml.jackson.databind.JsonNode;
import xyz.dreature.smit.common.model.entity.Data;

import java.util.List;
import java.util.Map;

// 模拟服务接口
public interface MockService {
    // 生成模拟数据（单条）
    Data generateMockData();

    // 生成模拟数据（多条）
    List<Data> generateMockData(int count);

    // 生成随机字符串
    String generateRandomString(int length);

    // 生成模拟请求参数（单组）
    Map<String, ?> generateMockParams();

    // 生成模拟请求参数（多组）
    List<Map<String, ?>> generateMockParams(int count);

    // 生成模拟响应（单个）
    JsonNode generateMockResponse(int dataSize);

    // 生成模拟响应（多个）
    List<JsonNode> generateMockResponses(int count, int dataSize);
}
