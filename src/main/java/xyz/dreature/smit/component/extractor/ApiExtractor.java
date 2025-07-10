package xyz.dreature.smit.component.extractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.service.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// API 抽取器
@Component
public class ApiExtractor<S> implements Extractor<S> {
    // 策略常量
    private static final String STRATEGY_FULL = "api:full";
    private static final String STRATEGY_PAGE = "api:page";

    @Autowired
    private ApiService<S> apiService;

    // 单项抽取
    public List<S> extract(EtlContext context, Map<String, ?> requestParams) {
        List<S> result = new ArrayList<>();
        result.add(apiService.call(requestParams));
        return result;
    }

    // 单批抽取（异步）
    public List<S> extractBatch(EtlContext context, List<? extends Map<String, ?>> requestsParams) {
        return apiService.callBatch(requestsParams);
    }
}
