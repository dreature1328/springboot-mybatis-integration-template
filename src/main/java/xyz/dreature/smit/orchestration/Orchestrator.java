package xyz.dreature.smit.orchestration;

import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.common.model.metrics.EtlMetrics;

import java.util.List;
import java.util.Map;

// 流程编排器接口
public interface Orchestrator {
    // 运行流程（逐项 / 单批执行）
    EtlMetrics run(EtlContext context, List<? extends Map<String, ?>> params);

    // 运行流程（逐项 / 分批执行）
    EtlMetrics runBatch(EtlContext context, List<? extends Map<String, ?>> params);
}
