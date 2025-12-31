package xyz.dreature.smit.orchestration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.common.model.job.Job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

// 任务调度器
@Component
public class JobScheduler {

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private TaskScheduler taskScheduler;

    // 添加新任务到调度池
    public void scheduleJob(Job job) {
        Runnable task = createTask(job);
        Trigger trigger = new CronTrigger(job.getCronExpression());

        ScheduledFuture<?> future = taskScheduler.schedule(task, trigger);
        scheduledTasks.put(job.getJobId(), future);
    }

    // 创建任务
    private Runnable createTask(Job job) {
        return () -> {
            Orchestrator orchestrator = applicationContext.getBean(
                    job.getOrchestratorName(),
                    Orchestrator.class
            );
            EtlContext context = new EtlContext();
            context.setJobId(job.getJobId());
            orchestrator.runBatch(context, job.getParams());
        };
    }

    // 取消任务
    public void cancelJob(String jobId) {
        ScheduledFuture<?> future = scheduledTasks.get(jobId);
        if (future != null) {
            future.cancel(true);
            scheduledTasks.remove(jobId);
        }
    }
}
