package dreature.smit.common.config;

import dreature.smit.entity.Data;
import dreature.smit.service.ExtractService;
import dreature.smit.service.IntegrationService;
import dreature.smit.service.LoadService;
import dreature.smit.service.TransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration      // 1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class TaskConfig {
    @Autowired
    private IntegrationService<Data> integrationService;
    @Autowired
    private ExtractService<Data> extractService;
    @Autowired
    private TransformService<Data> transformService;
    @Autowired
    private LoadService<Data> loadService;

    // 此处的 cron 表达式意为每 30 分钟执行一次任务
    @Scheduled(cron = "0 */30 * * * ?")
    public void performTaskOnSchedule() {
        integrationService.integrateBatchOptimized(extractService.generateParams(1000), 200, 200, 1000);
    }
}
