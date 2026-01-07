package xyz.dreature.smit.common.model.context;

// 上下文
public class Context {
    // ===== 字段 =====
    private String jobId;

    private int extractBatchSize; // 抽取批大小
    private int transformBatchSize; // 转换批大小
    private int loadBatchSize; // 加载批大小

    private String extractStrategy; // 抽取策略
    private String transformStrategy; // 转换策略（保留）
    private String loadStrategy; // 加载策略（保留）

    private String sourceDataSource; // 抽取源头数据源
    private String targetDataSource; // 加载目标数据源

    // ===== 构造方法 =====
    // 无参构造器
    public Context() {
        this.extractBatchSize = 200;
        this.transformBatchSize = 1000;
        this.loadBatchSize = 2000;
        this.extractStrategy = "db:ids";
        this.transformStrategy = "basic";
        this.loadStrategy = "db:upsert";
        this.sourceDataSource = "db1";
        this.targetDataSource = "db1";
    }

    // 全参构造器
    public Context(String jobId, int extractBatchSize, int transformBatchSize, int loadBatchSize, String extractStrategy, String transformStrategy, String loadStrategy, String sourceDataSource, String targetDataSource) {
        this.jobId = jobId;
        this.extractBatchSize = extractBatchSize;
        this.transformBatchSize = transformBatchSize;
        this.loadBatchSize = loadBatchSize;
        this.extractStrategy = extractStrategy;
        this.transformStrategy = transformStrategy;
        this.loadStrategy = loadStrategy;
        this.sourceDataSource = sourceDataSource;
        this.targetDataSource = targetDataSource;
    }

    // 全参构造器
    public Context(Context context) {
        this.jobId = context.getJobId();
        this.extractBatchSize = context.getExtractBatchSize();
        this.transformBatchSize = context.getTransformBatchSize();
        this.loadBatchSize = context.getLoadBatchSize();
        this.extractStrategy = context.getExtractStrategy();
        this.transformStrategy = context.getTransformStrategy();
        this.loadStrategy = context.getLoadStrategy();
        this.sourceDataSource = context.getSourceDataSource();
        this.targetDataSource = context.getTargetDataSource();
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

    public String getSourceDataSource() {
        return sourceDataSource;
    }

    public void setSourceDataSource(String sourceDataSource) {
        this.sourceDataSource = sourceDataSource;
    }

    public String getTargetDataSource() {
        return targetDataSource;
    }

    public void setTargetDataSource(String targetDataSource) {
        this.targetDataSource = targetDataSource;
    }

    // ===== 其他 =====
    // 字符串表示
    @Override
    public String toString() {
        return "EtlContext{" +
                "jobId='" + jobId + '\'' +
                ", extractBatchSize=" + extractBatchSize +
                ", transformBatchSize=" + transformBatchSize +
                ", loadBatchSize=" + loadBatchSize +
                ", extractStrategy='" + extractStrategy + '\'' +
                ", transformStrategy='" + transformStrategy + '\'' +
                ", loadStrategy='" + loadStrategy + '\'' +
                ", sourceDataSource='" + sourceDataSource + '\'' +
                ", targetDataSource='" + targetDataSource + '\'' +
                '}';
    }
}
