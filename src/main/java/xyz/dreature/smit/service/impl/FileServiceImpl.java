package xyz.dreature.smit.service.impl;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import xyz.dreature.smit.service.FileService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

// 文件服务
@Slf4j
public class FileServiceImpl<T> implements FileService<T> {
    // 服务键（用于注册）
    private final String key;
    // 资源加载器
    private final ResourceLoader resourceLoader;
    // 文件解析器
    private final Function<Resource, ?> parser;
    // 线程池
    private final Executor executor;

    public FileServiceImpl(
            String key,
            ResourceLoader resourceLoader,
            Function<Resource, T> parser,
            Executor executor
    ) {
        this.key = key;
        this.resourceLoader = resourceLoader;
        this.parser = parser;
        this.executor = executor;
    }

    // 获取服务键（用于注册）
    public String getKey() {
        return this.key;
    }

    // ===== 文件抽取 =====
    // 收集资源
    @Override
    public List<Resource> collect(String location) {
        List<Resource> resources = new ArrayList<>();
        // 目前只考虑网络及文件资源
        if (location.startsWith("http:") || location.startsWith("https:")) {
            resources.add(resourceLoader.getResource(location));
            return resources;
        } else if (location.startsWith("file:")) {
            location = location.substring(5); // 移除 "file:" 前缀
        }

        File target = new File(location);
        if (target.isFile()) {
            resources.add(new FileSystemResource(location));
        } else if (target.isDirectory()) {
            List<File> files = FileUtil.loopFiles(target);
            for (File file : files) {
                resources.add(new FileSystemResource(file));
            }
            log.debug("目录文件数量：{}", files.size());
        } else {
            log.warn("文件不存在：{}", location);
        }

        return resources;
    }

    // 单份读取
    @Override
    public T read(Resource resource) {
        return (T) parser.apply(resource);
    }

    // 单批异步读取
    @Override
    public List<T> readBatch(List<Resource> resources) {
        List<CompletableFuture<T>> futures = new ArrayList<>();

        try {
            // 添加异步读取任务
            for (Resource resource : resources) {
                CompletableFuture<T> future = CompletableFuture.supplyAsync(
                        () -> read(resource),
                        executor
                );
                futures.add(future);
            }

            // 等待异步任务完成，超时时间为 30 分钟
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(1800, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.err.println("请求超时");
            e.printStackTrace();
            return Collections.emptyList();
        }

        // 收集结果
        List<T> results = new ArrayList<>();
        for (CompletableFuture<T> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return results;
    }
}
