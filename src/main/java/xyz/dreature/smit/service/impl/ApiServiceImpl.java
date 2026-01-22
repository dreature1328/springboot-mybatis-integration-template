package xyz.dreature.smit.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.TypeUtil;
import xyz.dreature.smit.common.util.BatchUtils;
import xyz.dreature.smit.common.util.HttpUtils;
import xyz.dreature.smit.service.ApiService;

import java.util.*;
import java.util.concurrent.*;

// API 服务
public class ApiServiceImpl<T> implements ApiService<T> {
    // 请求 URL、方法、头
    private final Class<T> responseType;
    private final String baseUrl;
    private final String method;
    private final Map<String, String> headers;

    // 线程池
    private final Executor executor;

    public ApiServiceImpl(
            String baseUrl,
            String method,
            Map<String, String> headers,
            Executor executor
    ) {
        this.responseType = (Class<T>) TypeUtil.getTypeArgument(new TypeReference<ApiService<T>>() {
        }, 0);
        this.baseUrl = baseUrl;
        this.method = method;
        this.headers = headers;
        this.executor = executor;
    }

    // ===== API 抽取 =====
    // 单次调用
    @Override
    public T call(Map<String, ?> requestParams) {
        T response = null;
        try {
            response = HttpUtils.execute(baseUrl, method, headers, requestParams, responseType);
        } catch (Exception e) {
            System.err.println("请求失败: " + e.getMessage());
        }
        return response;
    }

    // 依次同步调用
    @Override
    public List<T> call(Map<String, ?>... requestsParams) {
        return BatchUtils.mapEach(new ArrayList<>(Arrays.asList(requestsParams)), this::call);
    }

    // 单批异步调用
    @Override
    public List<T> callBatch(List<? extends Map<String, ?>> requestsParams) {
        List<CompletableFuture<T>> futures = new ArrayList<>();
        try {
            // 添加异步请求任务
            for (Map<String, ?> requestParams : requestsParams) {
                CompletableFuture<T> future = HttpUtils.executeAsync(baseUrl, method, headers, requestParams, responseType, executor);
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
        List<T> responses = new ArrayList<>();
        for (CompletableFuture<T> future : futures) {
            try {
                responses.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return responses;
    }

    // 分批异步调用
    @Override
    public List<T> callBatch(List<? extends Map<String, ?>> requestsParams, int batchSize) {
        return BatchUtils.flatMapBatch(requestsParams, batchSize, this::callBatch);
    }
}
