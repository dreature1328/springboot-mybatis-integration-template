package xyz.dreature.smit.component.extractor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.Context;
import xyz.dreature.smit.component.extractor.Extractor;
import xyz.dreature.smit.service.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// API 抽取器
@Component
@Lazy
public class ApiExtractor<S> implements Extractor<S> {
    @Autowired
    private ApiService<S> apiService;

    // 单项抽取
    @Override
    public List<S> extract(Context context, Map<String, Object> requestParams) {
        List<S> result = new ArrayList<>();
        result.add(apiService.call(requestParams));
        return result;
    }

    // 单批抽取（异步）
    @Override
    public List<S> extractBatch(Context context, List<? extends Map<String, Object>> requestsParams) {
        return apiService.callBatch(requestsParams);
    }
}
