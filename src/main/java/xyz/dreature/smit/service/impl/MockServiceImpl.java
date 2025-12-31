package xyz.dreature.smit.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.service.MockService;

import java.security.SecureRandom;
import java.util.*;

// 模拟服务
@Service
public class MockServiceImpl implements MockService {
    private final SecureRandom random = new SecureRandom();
    @Autowired
    private ObjectMapper objectMapper;

    // 生成模拟数据（单条）
    @Override
    public Data generateMockData() {
        return new Data(
                // 取 UUID 的高 64 位并转换为非负长整型值
                Math.abs(UUID.randomUUID().getMostSignificantBits()),
                // 取 0-10000 之间的随机整数
                random.nextInt(10001),
                // 取 0.0-100.0 之间的随机小数（保留两位）
                Math.round(random.nextDouble() * 100 * 100.0) / 100.0,
                // 取 16 位随机字符串（大小写字母及数字）
                generateRandomString(16),
                // 取随机布尔值
                random.nextBoolean()
        );
    }

    // 生成模拟数据（多条）
    @Override
    public List<Data> generateMockData(int count) {
        List<Data> dataList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            dataList.add(generateMockData());
        }
        return dataList;
    }

    // 生成随机字符串
    @Override
    public String generateRandomString(int length) {
        String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    // 生成模拟请求参数（单组）
    @Override
    public Map<String, ?> generateMockParams() {
        Map<String, Integer> params = new HashMap<>();
        params.put("dataSize", random.nextInt(100));
        return params;
    }

    // 生成模拟请求参数（多组）
    @Override
    public List<Map<String, ?>> generateMockParams(int count) {
        List<Map<String, ?>> params = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            params.add(generateMockParams());
        }
        return params;
    }

    // 生成模拟响应（单个）
    @Override
    public JsonNode generateMockResponse(int dataSize) {
        try {
            // 构建响应结构
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("code", "SUCCESS");
            response.put("message", "操作成功");
            response.put("data", generateMockData(dataSize));

            return objectMapper.valueToTree(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "操作失败: " + e.getMessage());
            return objectMapper.valueToTree(errorResponse);
        }
    }

    // 生成模拟响应（多个）
    @Override
    public List<JsonNode> generateMockResponses(int count, int dataSize) {
        List<JsonNode> responses = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            responses.add(generateMockResponse(dataSize));
        }
        return responses;
    }
}
