package xyz.dreature.smit.common.model.job;

import cn.hutool.core.util.IdUtil;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "任务实体")
public class Job {
    // ===== 字段 =====
    @Schema(description = "任务 ID")
    private String jobId;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "Cron 表达式")
    private String cronExpression;

    @Schema(description = "编排器 Bean 名")
    private String orchestratorName;

    @Schema(description = "任务参数")
    private List<Map<String, Object>> params; // 任务参数

    // ===== 构造方法 =====
    // 无参构造器
    public Job() {
        this.jobId = "JOB" + "-" + IdUtil.objectId();
    }

    // 全参构造器
    public Job(String jobId, String jobName, String cronExpression, String orchestratorName, List<Map<String, Object>> params) {
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

    public List<? extends Map<String, Object>> getParams() {
        return params;
    }

    public void setParams(List<Map<String, Object>> params) {
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
