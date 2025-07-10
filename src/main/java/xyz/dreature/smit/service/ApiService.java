package xyz.dreature.smit.service;

import java.util.List;
import java.util.Map;

// API 服务接口
public interface ApiService<T> {
    // ===== API 抽取 =====
    // 单次调用
    T call(Map<String, ?> params);

    // 依次同步调用
    List<T> call(Map<String, ?>... requestsParam);

    // 单批异步调用
    List<T> callBatch(List<? extends Map<String, ?>> requestsParam);

    // 分批异步调用
    List<T> callBatch(List<? extends Map<String, ?>> requestsParam, int batchSize);
}
