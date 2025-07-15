package xyz.dreature.smit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.job.Job;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.orchestration.JobScheduler;

@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    private JobScheduler jobScheduler;

    @RequestMapping("/create")
    public ResponseEntity<Result<String>> createJob(@RequestBody Job job) {
        jobScheduler.scheduleJob(job);
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
