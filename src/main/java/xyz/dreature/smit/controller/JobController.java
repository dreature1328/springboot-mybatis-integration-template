package xyz.dreature.smit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.job.Job;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.orchestration.JobScheduler;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/job")
@Validated
public class JobController {
    @Autowired
    private JobScheduler jobScheduler;

    @RequestMapping("/create")
    public ResponseEntity<Result<String>> createJob(
            @RequestBody
            @NotNull(message = "任务不能为空")
            Job job) {
        jobScheduler.scheduleJob(job);
        log.info("任务创建完成，任务ID：{}", job.getJobId());
        return ResponseEntity.ok(Result.success("任务创建成功", null));
    }

//    示例任务
//    {
//        "jobId":"mock",
//            "cronExpression":"0 * * * * ?",  // 每分钟执行
//            "orchestratorName":"mockToDbOrch",
//            "params": [
//            {
//                "dataSize":1000
//            }
//      ]
//    }
}
