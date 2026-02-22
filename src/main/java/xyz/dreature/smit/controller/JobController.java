package xyz.dreature.smit.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.job.Job;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.orchestration.JobScheduler;

import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequestMapping("/job")
@Tag(name = "任务操作")
public class JobController {
    @Autowired
    private JobScheduler jobScheduler;

    @PostMapping("/schedule")
    public ResponseEntity<Result<String>> schedule(
            @RequestBody
            @NotNull(message = "任务不能为空")
            Job job) {
        jobScheduler.schedule(job);
        log.info("任务注册完成，任务ID：{}", job.getJobId());
        return ResponseEntity.ok(Result.success("任务注册成功", null));
    }
}