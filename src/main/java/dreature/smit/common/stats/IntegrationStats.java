package dreature.smit.common.stats;

import java.util.concurrent.atomic.AtomicInteger;

public class IntegrationStats {
    // ----- 时间点指标（毫秒级） -----
    private long startTime;
    private long requestEndTime;
    private long transformEndTime;
    private long persistEndTime;

    //  ----- 计数指标（线程安全） -----
    private final AtomicInteger requests = new AtomicInteger(0);
    private final AtomicInteger responses = new AtomicInteger(0);
    private final AtomicInteger entities = new AtomicInteger(0);
    private final AtomicInteger affectedRows = new AtomicInteger(0);
    private final AtomicInteger errors = new AtomicInteger(0);

    // ----- 时间点操作 -----
    public void markStart() {
        startTime = System.currentTimeMillis();
    }

    public void markRequestEnd() {
        requestEndTime = System.currentTimeMillis();
    }

    public void markTransformEnd() {
        transformEndTime = System.currentTimeMillis();
    }

    public void markPersistEnd() {
        persistEndTime = System.currentTimeMillis();
    }

    public long getRequestDuration() {
        return requestEndTime - startTime;
    }

    public long getTransformDuration() {
        return transformEndTime - requestEndTime;
    }

    public long getPersistDuration() {
        return persistEndTime - transformEndTime;
    }

    public long getTotalDuration() {
        return persistEndTime - startTime;
    }

    // ----- 计数操作 -----
    public void recordRequests(int count) {
        requests.addAndGet(count);
    }

    public void recordResponses(int count) {
        responses.addAndGet(count);
    }

    public void recordEntities(int count) {
        entities.addAndGet(count);
    }

    public void recordAffectedRows(int count) {
        affectedRows.addAndGet(count);
    }

    public void recordError() {
        errors.incrementAndGet();
    }

    public void recordErrors(int count) {
        errors.addAndGet(count);
    }

    // ------ 成功率计算 -----
    public double getResponseRate() {
        int reqs = requests.get();
        return reqs > 0 ? (responses.get() * 100.0) / reqs : 0.0;
    }

    public double getTransformationRate() {
        int resps = responses.get();
        return resps > 0 ? (entities.get() * 100.0) / resps : 0.0;
    }

    public double getPersistenceRate() {
        int objs = entities.get();
        return objs > 0 ? (affectedRows.get() * 100.0) / objs : 0.0;
    }

    public double getOverallSuccessRate() {
        int reqs = requests.get();
        return reqs > 0 ? (affectedRows.get() * 100.0) / reqs : 0.0;
    }

    public double getErrorRate() {
        int reqs = requests.get();
        return reqs > 0 ? (errors.get() * 100.0) / reqs : 0.0;
    }

    public String generateReport() {
        return String.format(
                "集成完毕(%.2f%%)[%.1fs] | " +
                "请求:%d | " +
                "响应:%d(%.2f%%)[%.1fs] | " +
                "转换:%d(%.2f%%)[%.1fs] | " +
                "入库:%d(%.2f%%)[%.1fs] | " +
                "错误:%d(%.2f%%)",
                getOverallSuccessRate(), getTotalDuration() / 1000.0,
                requests.get(),
                responses.get(), getResponseRate(), getRequestDuration() / 1000.0,
                entities.get(), getTransformationRate(), getTransformDuration() / 1000.0,
                affectedRows.get(), getPersistenceRate(), getPersistDuration() / 1000.0,
                errors.get(), getErrorRate()
        );
    }
}
