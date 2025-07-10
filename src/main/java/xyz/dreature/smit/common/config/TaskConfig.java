package xyz.dreature.smit.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import xyz.dreature.smit.controller.EtlController;

// 定时任务定义
@Configuration
@EnableScheduling
public class TaskConfig {
    @Autowired
    EtlController etlController;

    // 此处的 cron 表达式意为每 30 分钟执行一次任务
    @Scheduled(cron = "0 */30 * * * ?")
    public void performTaskOnSchedule() {
        etlController.mockRunBatch(10);
    }
}
