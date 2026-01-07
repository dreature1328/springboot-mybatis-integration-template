package xyz.dreature.smit.service.impl;

import xyz.dreature.smit.service.FileService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

// 文件服务
public class FileServiceImpl<T> implements FileService<T> {
    // 服务键（用于注册）
    private final String key;
    // 文件解析器
    private final Function<String, ?> parser;
    // 线程池
    private final Executor executor;

    public FileServiceImpl(
            String key,
            Function<String, T> parser,
            Executor executor
    ) {
        this.key = key;
        this.parser = parser;
        this.executor = executor;
    }

    // 获取服务键（用于注册）
    public String getKey() {
        return this.key;
    }

    // ===== 文件抽取 =====
    // 获取后缀名
    private String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filePath.length() - 1) {
            throw new IllegalArgumentException("文件无扩展名: " + filePath);
        }
        return filePath.substring(dotIndex + 1);
    }

    // 单份读取
    @Override
    public T read(String filePath) {
        return (T) parser.apply(filePath);
    }

    // 单批异步读取
    @Override
    public List<T> readBatch(List<String> filePaths) {
        List<CompletableFuture<T>> futures = new ArrayList<>();

        try {
            // 添加异步请求任务
            for (String filePath : filePaths) {
                CompletableFuture<T> future = CompletableFuture.supplyAsync(
                        () -> read(filePath),
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

        // 将所有异步请求的结果获取为 List<T>
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
