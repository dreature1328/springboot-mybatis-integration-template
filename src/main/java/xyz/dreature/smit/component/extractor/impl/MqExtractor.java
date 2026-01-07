package xyz.dreature.smit.component.extractor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.Context;
import xyz.dreature.smit.component.extractor.Extractor;
import xyz.dreature.smit.service.MqService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 消息队列抽取器
@Component
@Lazy
public class MqExtractor<S, T> implements Extractor<S> {
    @Autowired
    private MqService<S, T> mqService;

    // 单项抽取
    @Override
    public List<S> extract(Context context, Map<String, Object> params) {
        List<S> result = new ArrayList<>();
        result.add(mqService.receiveWithConverter());
        return result;
    }

    // 单批抽取
    @Override
    public List<S> extractBatch(Context context, List<? extends Map<String, Object>> params) {
        return mqService.receiveBatchWithConverter();
    }
}
