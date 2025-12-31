package xyz.dreature.smit.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

@Configuration
@EnableAsync
public class ExecutorConfig {
    // I/O 密集型线程池
    @Bean("ioExecutor")
    public Executor ioTaskExecutor() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                cpuCores * 2,
                cpuCores * 4, // 最佳线程数 = CPU核心数 / (1 - 阻塞系数)
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                new ThreadPoolExecutor.AbortPolicy()
        );
        return executor;
    }

    // CPU 密集型线程池
    @Bean("cpuExecutor")
    public Executor cpuTaskExecutor() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                cpuCores + 1,
                cpuCores + 1,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        return executor;
    }
}
