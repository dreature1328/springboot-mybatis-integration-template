package dreature.smit.service;

import dreature.smit.common.stats.IntegrationStats;

import java.util.List;
import java.util.Map;

public interface IntegrationService<T> {
    // 集成（依次同步请求 + 逐项转换 + 逐项持久化）
    IntegrationStats integrate(List<? extends Map<String, ?>> paramsList);

    // 优化集成（分批异步请求 + 流式转换 + 分批持久化）
    List<IntegrationStats> integrateOptimized(List<? extends Map<String, ?>> paramsList, int reqBatchSize, int persistBatchSize);

    // 分批优化集成
    List<IntegrationStats> integrateBatchOptimized(List<? extends Map<String, ?>> paramsList, int intBatchSize, int reqBatchSize, int persistBatchSize);

//    // 监听集成（异步监听消息 + 逐项转换 + 逐项持久化）
//    public void listen(Message message);
}
