package xyz.dreature.smit.component.extractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.service.MqService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 消息队列抽取器
@Component
public class MqExtractor<S, T> implements Extractor<S> {
    @Autowired
    private MqService<S, T> mqService;

    // 单项抽取
    public List<S> extract(EtlContext context, Map<String, ?> params) {
        List<S> result = new ArrayList<>();
        result.add(mqService.receiveWithConverter());
        return result;
    }

    // 单批抽取
    public List<S> extractBatch(EtlContext context, List<? extends Map<String, ?>> params) {
        return mqService.receiveBatchWithConverter();
    }
}
