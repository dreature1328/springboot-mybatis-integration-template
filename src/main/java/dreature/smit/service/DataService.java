package dreature.smit.service;

import dreature.smit.common.stats.IntegrationStats;
import dreature.smit.entity.Data;

import java.util.List;
import java.util.Map;

public interface DataService extends BaseService<Data> {

    // 生成对象（测试用）
    List<Data> generate(int count);

    // 解析 JSON 数据（测试用）
    List<Data> parseFromJson(String filePath);

    // 解析 XML 数据（测试用）
    List<Data> parseFromXml(String filePath);

    // 集成
    IntegrationStats integrate(List<? extends Map<String, ?>> paramsList);

    // 优化集成
    List<IntegrationStats> integrateOptimized(List<? extends Map<String, ?>> paramsList);

    // 分页优化集成
    List<IntegrationStats> integratePageOptimized(List<? extends Map<String, ?>> paramsList);

    // 生成请求参数（测试用）
    List<Map<String, String>> generateParams(int count);

    // 单次请求
    String request(Map<String, ?> params);

    // 批量异步请求
    List<String> requestBatch(List<? extends Map<String, ?>> paramsList);

    // 分页异步请求
    List<String> requestPage(List<? extends Map<String, ?>> paramsList);

    // 生成响应结果（测试用）
    List<String> generateResponses(int count);

    // 单项转换
    Data transform(String response);

    // 流水线转换
    List<Data> transformPipeline(List<String> responses);

    // 查询总数
    int countAll();

    // 查询 n 条
    List<Data> findTopN(int n);

    // 分页查询
    List<Data> selectPageByIds(List<String> ids);

    // 分页插入
    int insertPage(List<Data> dataList);

    // 分页更新
    int updatePage(List<Data> dataList);

    // 分页插入或更新
    int upsertPage(List<Data> dataList);

    // 分页删除
    int deletePageByIds(List<String> idList);

    // 清空
    void truncate();
}
