package xyz.dreature.smit.component.extractor;

import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.common.util.JsonUtils;
import xyz.dreature.smit.common.util.XmlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 文件抽取器
@Component
public class FileExtractor<S> implements Extractor<S> {
    // 策略常量
    private static final String STRATEGY_JSON = "file:json";
    private static final String STRATEGY_XML = "file:xml";
    private static final String STRATEGY_CSV = "file:csv";

    // 单项抽取
    public List<S> extract(EtlContext context, Map<String, ?> fileParams) {
        List<S> result = new ArrayList<>();
        String filePath = (String) fileParams.get("filePath");
        switch (context.getExtractStrategy().toLowerCase()) {
            case STRATEGY_JSON:
                result.add((S) JsonUtils.parseFile(filePath));
                return result;
            case STRATEGY_XML:
                result.add((S) XmlUtils.parseFile(filePath));
                return result;
            default:
                throw new IllegalArgumentException("不支持的抽取策略: " + context.getExtractStrategy());
        }
    }

    // 单批抽取（并行）
    public List<S> extractBatch(EtlContext context, List<? extends Map<String, ?>> filesParams) {
        return filesParams.parallelStream()
                .flatMap(fileParams -> extract(context, fileParams).stream())
                .collect(Collectors.toList());
    }
}
