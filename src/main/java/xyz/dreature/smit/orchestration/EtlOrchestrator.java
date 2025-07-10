// orchestrator/EtlJobOrchestrator.java
package xyz.dreature.smit.orchestration;

import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.common.model.metrics.EtlMetrics;
import xyz.dreature.smit.common.util.BatchUtils;
import xyz.dreature.smit.component.extractor.Extractor;
import xyz.dreature.smit.component.loader.Loader;
import xyz.dreature.smit.component.transformer.Transformer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

// 编排器
public class EtlOrchestrator<S, T, ID extends Serializable> implements Orchestrator {
    // ETL 组件
    private Extractor<S> extractor;
    private Transformer<S, T> transformer;
    private Loader<T, ID> loader;

    public EtlOrchestrator(
            Extractor<S> extractor,
            Transformer<S, T> transformer,
            Loader<T, ID> loader
    ) {
        this.extractor = extractor;
        this.transformer = transformer;
        this.loader = loader;
    }

    // 运行流程（逐项 / 单批执行）
    public EtlMetrics run(EtlContext context, List<? extends Map<String, ?>> params) {
        EtlMetrics metrics = new EtlMetrics();
        metrics.markExtractStart();
        metrics.recordSourceUnits(params.size());

        try {
            // 1. 抽取阶段（逐项 / 单批执行）
            List<S> extractedData = extractor.extractBatch(context, params);
            metrics.markExtractEnd();
            metrics.recordExtractedItems(extractedData.size());

            // 2. 转换阶段（逐项 / 单批执行）
            List<T> transformedData = transformer.transformStream(context, extractedData);
            metrics.markTransformEnd();
            metrics.recordTransformedItems(transformedData.size());

            // 3. 加载阶段（逐项 / 单批执行）
            int affectedRows = loader.loadBatch(context, transformedData);
            metrics.markLoadEnd();
            metrics.recordLoadedItems(affectedRows);

        } catch (Exception e) {
            metrics.recordError();
            throw new RuntimeException("ETL 流程运行失败: " + e.getMessage(), e);
        }

        return metrics;
    }

    // 运行流程（逐项 / 分批执行）
    public EtlMetrics runBatch(EtlContext context, List<? extends Map<String, ?>> params) {
        EtlMetrics metrics = new EtlMetrics();
        metrics.markExtractStart();
        metrics.recordSourceUnits(params.size());

        try {
            // 1. 抽取阶段（逐项 / 分批执行）
            List<S> extractedData = BatchUtils.flatMapBatch(
                    params,
                    context.getExtractBatchSize(),
                    batch -> extractor.extractBatch(context, batch)
            );
            metrics.recordExtractedItems(extractedData.size());
            metrics.markExtractEnd();

            // 2. 转换阶段（逐项 / 分批执行）
            List<T> transformedData = BatchUtils.flatMapBatch(
                    extractedData,
                    context.getTransformBatchSize(),
                    batch -> transformer.transformStream(context, batch)
            );
            metrics.recordTransformedItems(transformedData.size());
            metrics.markTransformEnd();

            // 3. 加载阶段（逐项 / 分批执行）
            int affectedRows = BatchUtils.reduceBatchToInt(
                    transformedData,
                    context.getLoadBatchSize(),
                    batch -> loader.loadBatch(context, batch)
            );
            metrics.recordLoadedItems(affectedRows);
            metrics.markLoadEnd();

        } catch (Exception e) {
            metrics.recordError();
            throw new RuntimeException("ETL 流程运行失败: " + e.getMessage(), e);
        }

        return metrics;
    }
}
