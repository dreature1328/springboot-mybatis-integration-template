package xyz.dreature.smit.common.model.metrics;

import java.util.concurrent.atomic.AtomicInteger;

// ETL 指标
public class EtlMetrics {
    //  ===== 计数指标（线程安全） =====
    private final AtomicInteger sourceUnits = new AtomicInteger(0);      // 源数据单元数（消息/记录/文件等）
    private final AtomicInteger extractedItems = new AtomicInteger(0);   // 已抽取数据项数
    private final AtomicInteger transformedItems = new AtomicInteger(0); // 成功转换数据项数
    private final AtomicInteger loadedItems = new AtomicInteger(0);      // 成功加载数据项数
    private final AtomicInteger errors = new AtomicInteger(0);           // 错误计数

    // ===== 时间点指标（毫秒级） =====
    private long extractStartTime;      // 数据抽取开始时间
    private long extractEndTime;        // 数据抽取结束时间
    private long transformEndTime;      // 数据转换结束时间
    private long loadEndTime;           // 数据加载结束时间

    // ===== 时间点标记 =====
    public void markExtractStart() {
        extractStartTime = System.currentTimeMillis();
    }

    public void markExtractEnd() {
        extractEndTime = System.currentTimeMillis();
    }

    public void markTransformEnd() {
        transformEndTime = System.currentTimeMillis();
    }

    public void markLoadEnd() {
        loadEndTime = System.currentTimeMillis();
    }

    // ===== 时间间隔计算 =====
    public long getExtractDuration() {
        return extractEndTime - extractStartTime;
    }

    public long getTransformDuration() {
        return transformEndTime - extractEndTime;
    }

    public long getLoadDuration() {
        return loadEndTime - transformEndTime;
    }

    public long getTotalDuration() {
        return loadEndTime - extractStartTime;
    }

    // ===== 计数操作 =====
    public void recordSourceUnits(int count) {
        sourceUnits.addAndGet(count);
    }

    public void recordExtractedItems(int count) {
        extractedItems.addAndGet(count);
    }

    public void recordTransformedItems(int count) {
        transformedItems.addAndGet(count);
    }

    public void recordLoadedItems(int count) {
        loadedItems.addAndGet(count);
    }

    public void recordError() {
        errors.incrementAndGet();
    }

    public void recordErrors(int count) {
        errors.addAndGet(count);
    }

    // =====- 统计指标计算 =====
    public double getExtractionYield() {
        int units = sourceUnits.get();
        return units > 0 ? (extractedItems.get() * 100.0) / units : 0.0;
    }

    public double getTransformationYield() {
        int extracted = extractedItems.get();
        return extracted > 0 ? (transformedItems.get() * 100.0) / extracted : 0.0;
    }

    public double getLoadingYield() {
        int transformed = transformedItems.get();
        return transformed > 0 ? (loadedItems.get() * 100.0) / transformed : 0.0;
    }

    public double getErrorRate() {
        int units = sourceUnits.get();
        return units > 0 ? (errors.get() * 100.0) / units : 0.0;
    }

    // ===== 统计报告生成 =====
    public String generateReport() {
        return String.format(
                "集成[%.1fs] | " +
                        "源单元:%d | " +
                        "抽取:%d(%.2f%%)[%.1fs] | " +
                        "转换:%d(%.2f%%)[%.1fs] | " +
                        "加载:%d(%.2f%%)[%.1fs] | " +
                        "错误:%d(%.2f%%) | ",
                getTotalDuration() / 1000.0,
                sourceUnits.get(),
                extractedItems.get(), getExtractionYield(), getExtractDuration() / 1000.0,
                transformedItems.get(), getTransformationYield(), getTransformDuration() / 1000.0,
                loadedItems.get(), getLoadingYield(), getLoadDuration() / 1000.0,
                errors.get(), getErrorRate()
        );
    }
}
