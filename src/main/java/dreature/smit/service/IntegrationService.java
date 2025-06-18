package dreature.smit.service;

import dreature.smit.common.stats.IntegrationStats;

import java.util.List;
import java.util.Map;

public interface IntegrationService<T> {
    // 集成
    IntegrationStats integrate(List<? extends Map<String, ?>> paramsList);

    // 优化集成
    List<IntegrationStats> integrateOptimized(List<? extends Map<String, ?>> paramsList, int reqBatchSize, int persistBatchSize);

    // 分批优化集成
    List<IntegrationStats> integrateBatchOptimized(List<? extends Map<String, ?>> paramsList, int intBatchSize, int reqBatchSize, int persistBatchSize);

}
