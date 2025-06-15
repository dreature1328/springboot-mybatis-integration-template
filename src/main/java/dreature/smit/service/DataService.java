package dreature.smit.service;

import dreature.smit.common.stats.IntegrationStats;
import dreature.smit.entity.Data;

import java.util.List;
import java.util.Map;

public interface DataService extends BaseService<Data> {

    // 生成数据（测试用）
    List<Data> generate(int count);

    // 解析 JSON 数据（测试用）
    List<Data> parseFromJson(String filePath);

    // 解析 XML 数据（测试用）
    List<Data> parseFromXml(String filePath);

    // 集成
    IntegrationStats integrate(List<? extends Map<String, ?>> paramsList);

    // 优化集成
    List<IntegrationStats> integrateOptimized(List<? extends Map<String, ?>> paramsList, int reqBatchSize, int persistBatchSize);

    // 分批优化集成
    List<IntegrationStats> integrateBatchOptimized(List<? extends Map<String, ?>> paramsList, int intBatchSize, int reqBatchSize, int persistBatchSize);

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

    // 单项转换
    List<Data> transform(String response);

    // 逐项转换
    List<Data> transform(String... responses);

    // 流水线转换
    List<Data> transformPipeline(List<String> responses);

    // 查询总数
    int countAll();

    // 查询全表
    List<Data> findAll();

    // 查询 n 条
    List<Data> findRandomN(int count);

    // 分批查询
    List<Data> selectBatchByIds(List<String> ids, int batchSize);

    // 分批插入
    int insertBatch(List<Data> dataList, int batchSize);

    // 分批更新
    int updateBatch(List<Data> dataList, int batchSize);

    // 分批插入或更新
    int upsertBatch(List<Data> dataList, int batchSize);

    // 分批删除
    int deleteBatchByIds(List<String> idList, int batchSize);

    // 清空
    void truncate();
}
