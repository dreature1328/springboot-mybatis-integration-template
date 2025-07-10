package xyz.dreature.smit.common.model.context;

import java.util.UUID;

// ETL 上下文
public class EtlContext {
    // ===== 属性 =====
    private String jobId;

    private int extractBatchSize;
    private int transformBatchSize;
    private int loadBatchSize;

    private String extractStrategy; // 可选值: "db:id", "db:ids", "db:page", "db:random", "db:all"
    private String transformStrategy; // 可选值: "basic"
    private String loadStrategy; // 可选值: "db:insert", "db:update", "db:upsert"

    // ===== 构造方法 =====
    // 无参构造器
    public EtlContext() {
        this.jobId = UUID.randomUUID().toString();
        this.extractBatchSize = 200;
        this.transformBatchSize = 1000;
        this.loadBatchSize = 2000;
        this.extractStrategy = "db:id";
        this.transformStrategy = "basic";
        this.loadStrategy = "db:upsert";
    }

    // 全参构造器
    public EtlContext(String jobId, int extractBatchSize, int transformBatchSize, int loadBatchSize, String extractStrategy, String transformStrategy, String loadStrategy) {
        this.jobId = jobId;
        this.extractBatchSize = extractBatchSize;
        this.transformBatchSize = transformBatchSize;
        this.loadBatchSize = loadBatchSize;
        this.extractStrategy = extractStrategy;
        this.transformStrategy = transformStrategy;
        this.loadStrategy = loadStrategy;
    }

    // 全参构造器
    public EtlContext(EtlContext etlContext) {
        this.jobId = etlContext.getJobId();
        this.extractBatchSize = etlContext.getExtractBatchSize();
        this.transformBatchSize = etlContext.getTransformBatchSize();
        this.loadBatchSize = etlContext.getLoadBatchSize();
        this.extractStrategy = etlContext.getExtractStrategy();
        this.transformStrategy = etlContext.getTransformStrategy();
        this.loadStrategy = etlContext.getLoadStrategy();
    }

    // ===== Getter 与 Setter 方法 =====
    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getExtractBatchSize() {
        return extractBatchSize;
    }

    public void setExtractBatchSize(int extractBatchSize) {
        this.extractBatchSize = extractBatchSize;
    }

    public int getTransformBatchSize() {
        return transformBatchSize;
    }

    public void setTransformBatchSize(int transformBatchSize) {
        this.transformBatchSize = transformBatchSize;
    }

    public int getLoadBatchSize() {
        return loadBatchSize;
    }

    public void setLoadBatchSize(int loadBatchSize) {
        this.loadBatchSize = loadBatchSize;
    }

    public String getExtractStrategy() {
        return extractStrategy;
    }

    public void setExtractStrategy(String extractStrategy) {
        this.extractStrategy = extractStrategy;
    }

    public String getTransformStrategy() {
        return transformStrategy;
    }

    public void setTransformStrategy(String transformStrategy) {
        this.transformStrategy = transformStrategy;
    }

    public String getLoadStrategy() {
        return loadStrategy;
    }

    public void setLoadStrategy(String loadStrategy) {
        this.loadStrategy = loadStrategy;
    }

    // ===== 其他 =====
    // 字符串表示
    public String toString() {
        return "EtlContext{" +
                "jobId='" + jobId + '\'' +
                ", extractBatchSize=" + extractBatchSize +
                ", transformBatchSize=" + transformBatchSize +
                ", loadBatchSize=" + loadBatchSize +
                ", extractStrategy='" + extractStrategy + '\'' +
                ", transformStrategy='" + transformStrategy + '\'' +
                ", loadStrategy='" + loadStrategy + '\'' +
                '}';
    }
}
