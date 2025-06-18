package dreature.smit.service;
import java.util.List;
import java.util.Map;

public interface ExtractService<T> {
    // 生成数据（测试用）
    List<T> generate(int count);

    // 解析 JSON 数据（测试用）
    List<T> parseFromJson(String filePath);

    // 解析 XML 数据（测试用）
    List<T> parseFromXml(String filePath);

    // 生成请求参数（测试用）
    List<Map<String, String>> generateParams(int count);

    // 单次请求
    List<String> request(Map<String, ?> params);

    // 依次请求
    List<String> request(Map<String, ?>... paramsArray);

    // 单批异步请求
    List<String> requestBatch(List<? extends Map<String, ?>> paramsList);

    // 分批异步请求
    List<String> requestBatch(List<? extends Map<String, ?>> paramsList, int batchSize);

    // 生成响应结果（测试用）
    List<String> generateResponses(int count);

    // 查询总数
    int countAll();

    // 查询全表
    List<T> findAll();

    // 查询 n 条
    List<T> findRandomN(int count);

    // 分批查询
    List<T> selectBatchByIds(List<String> ids, int batchSize);
}
