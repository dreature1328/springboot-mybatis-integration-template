package xyz.dreature.smit.common.model.job;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// 任务
public class Job {
    // ===== 属性 =====
    private String jobId;          // 任务 ID
    private String jobName;        // 任务名称
    private String cronExpression; // cron 表达式
    private String orchestratorName; // 编排器 Bean 名
    private List<Map<String, ?>> params; // 任务参数

    // ===== 构造方法 =====
    // 无参构造器
    public Job() {
        this.jobId = UUID.randomUUID().toString();
    }

    // 全参构造器
    public Job(String jobId, String jobName, String cronExpression, String orchestratorName, List<Map<String, ?>> params) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.cronExpression = cronExpression;
        this.orchestratorName = orchestratorName;
        this.params = params;
    }

    // 复制构造器
    public Job(Job job) {
        this.jobId = job.jobId;
        this.jobName = job.jobName;
        this.cronExpression = job.cronExpression;
        this.orchestratorName = job.orchestratorName;
        this.params = job.params;
    }

    // ===== Getter 与 Setter 方法 =====
    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    public List<Map<String, ?>> getParams() {
        return params;
    }

    public void setParams(List<Map<String, ?>> params) {
        this.params = params;
    }

    // ===== 其他 =====
    // 字符串表示
    @Override
    public String toString() {
        return "Job{" +
                "jobId='" + jobId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", orchestratorName='" + orchestratorName + '\'' +
                ", params=" + params +
                '}';
    }
}
