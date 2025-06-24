package dreature.smit.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import dreature.smit.common.stats.IntegrationStats;
import dreature.smit.common.util.BatchUtils;
import dreature.smit.entity.Data;
import dreature.smit.service.ExtractService;
import dreature.smit.service.IntegrationService;
import dreature.smit.service.LoadService;
import dreature.smit.service.TransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataIntegrationServiceImpl extends BaseServiceImpl<Data> implements IntegrationService<Data> {
    @Autowired
    private ExtractService<Data> extractService;
    @Autowired
    private TransformService<Data> transformService;
    @Autowired
    private LoadService<Data> loadService;

    // ----- 数据集成 -----
    // 集成（依次同步请求 + 逐项转换 + 逐项持久化）
    public IntegrationStats integrate(List<? extends Map<String, ?>> paramsList) {
        IntegrationStats stats = new IntegrationStats();

        stats.markExtractStart();
        stats.recordSourceUnits(paramsList.size());

        // 抽取：同步请求并获取响应内容
//        List<JsonNode> jsonNodes = BatchUtils.mapEach(paramsList, this::request); // 实际请求 API
        List<JsonNode> jsonNodes = extractService.generateResponses(paramsList.size()); // 测试用
        stats.markExtractEnd();
        stats.recordExtractedItems(jsonNodes.size());

        // 转换：逐项转换，将响应内容转换成对象列表
        List<Data> dataList = BatchUtils.mapEach(jsonNodes, transformService::transform);
        stats.markTransformEnd();
        stats.recordTransformedItems(dataList.size());

        // 加载：逐项插入或更新进数据库
        int affectedRows = BatchUtils.reduceEach(dataList, baseMapper::upsert);
        stats.markLoadEnd();
        stats.recordLoadedItems(affectedRows);

        return stats;
    }

    // 优化集成（分批异步请求 + 流式转换 + 分批持久化）
    public List<IntegrationStats> integrateOptimized(List<? extends Map<String, ?>> paramsList, int reqBatchSize, int persistBatchSize) {
        List statsList = new ArrayList<>();
        IntegrationStats stats = new IntegrationStats();

        stats.markExtractStart();
        stats.recordSourceUnits(paramsList.size());

        // 抽取：分批异步请求并获取响应内容
//        List<JsonNode> jsonNodes = requestBatch(paramsList, reqBatchSize); // 实际请求 API
        List<JsonNode> jsonNodes = extractService.generateResponses(paramsList.size()); // 测试用
        stats.markExtractEnd();
        stats.recordExtractedItems(jsonNodes.size());

        // 转换：流式转换，将响应内容转换成对象列表
        List<Data> dataList = transformService.transformStream(jsonNodes);
        stats.markTransformEnd();
        stats.recordTransformedItems(dataList.size());

        // 加载：分批插入或更新进数据库
        int affectedRows = loadService.upsertBatch(dataList, persistBatchSize);
        stats.markLoadEnd();
        stats.recordLoadedItems(affectedRows);

        statsList.add(stats);
        return statsList;
    }

    // 分批优化集成
    public List<IntegrationStats> integrateBatchOptimized(
            List<? extends Map<String, ?>> paramsList,
            int intBatchSize,
            int reqBatchSize,
            int persistBatchSize) {
        List<IntegrationStats> result = BatchUtils.mapBatch(
                paramsList,
                intBatchSize,
                batch -> integrateOptimized(
                        batch,
                        reqBatchSize,
                        persistBatchSize
                )
        );
        return result;
    }

//    // 监听集成（异步监听消息 + 逐项转换 + 逐项持久化）
//    @RabbitListener(queues = "${spring.rabbitmq.template.routing-key}")
//    public void listen(Message message) {
//        byte[] payload = message.getBody();
//        MessageProperties messageProperties = message.getMessageProperties();
//        JsonNode jsonNode = null;
//        try {
//            jsonNode = MqUtils.parseJson(payload);
//            System.out.println(jsonNode);
//            // TODO 根据需要实现消息处理的业务逻辑
//        } catch (Exception e) {
//            System.err.println("监听失败: " + e.getMessage());
//        }
//    }
}
