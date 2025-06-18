package dreature.smit.service.impl;

import dreature.smit.common.stats.IntegrationStats;
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

import static dreature.smit.common.util.BatchUtils.*;

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

        stats.markStart();
        stats.recordRequests(paramsList.size());

        // 同步请求并获取响应内容
//        List<String> responses = mapEach(paramsList, this::request); // 实际请求
        List<String> responses = extractService.generateResponses(paramsList.size()); // 测试用
        stats.markRequestEnd();
        stats.recordResponses(responses.size());

        // 逐项转换，将响应内容转换成对象列表
        List<Data> dataList = mapEach(responses, transformService::transform);
        stats.markTransformEnd();
        stats.recordEntities(dataList.size());

        // 逐项插入或更新进数据库
        int affectedRows = reduceEach(dataList, baseMapper::upsert);
        stats.markPersistEnd();
        stats.recordAffectedRows(affectedRows);

        return stats;
    }

    // 优化集成（分批异步请求 + 流水线转换 + 分批持久化）
    public List<IntegrationStats> integrateOptimized(List<? extends Map<String, ?>> paramsList, int reqBatchSize, int persistBatchSize) {
        List statsList = new ArrayList<>();
        IntegrationStats stats = new IntegrationStats();

        stats.markStart();
        stats.recordRequests(paramsList.size());
        // 分批异步请求并获取响应内容
//        List<String> responses = requestBatch(paramsList, reqBatchSize); // 实际请求
        List<String> responses = extractService.generateResponses(paramsList.size()); // 测试用
        stats.markRequestEnd();
        stats.recordResponses(responses.size());

        // 流水线转换数据，将响应内容转换成对象列表
        List<Data> dataList = transformService.transformPipeline(responses);
        stats.markTransformEnd();
        stats.recordEntities(dataList.size());

        // 分批插入或更新进数据库
        int affectedRows = loadService.upsertBatch(dataList, persistBatchSize);
        stats.markPersistEnd();
        stats.recordAffectedRows(affectedRows);

        statsList.add(stats);
        return statsList;
    }

    // 分批优化集成
    public List<IntegrationStats> integrateBatchOptimized(
            List<? extends Map<String, ?>> paramsList,
            int intBatchSize,
            int reqBatchSize,
            int persistBatchSize) {
        List<IntegrationStats> result = mapBatch(
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
}
