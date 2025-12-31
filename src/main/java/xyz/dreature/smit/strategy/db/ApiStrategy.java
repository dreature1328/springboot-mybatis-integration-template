package xyz.dreature.smit.strategy.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.service.ApiService;
import xyz.dreature.smit.strategy.ExtractStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Lazy
public class ApiStrategy<S> implements ExtractStrategy<S> {
    @Autowired
    private ApiService<S> apiService;

    @Override
    public String getStrategyName() {
        return "api:basic";
    }

    @Override
    public List<S> extract(Map<String, ?> requestParams) {
        List<S> result = new ArrayList<>();
        result.add(apiService.call(requestParams));
        return result;
    }

    @Override
    public List<S> extractBatch(List<? extends Map<String, ?>> requestsParams) {
        return apiService.callBatch(requestsParams);
    }
}
