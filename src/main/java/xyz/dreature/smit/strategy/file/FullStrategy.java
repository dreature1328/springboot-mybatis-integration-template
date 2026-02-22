package xyz.dreature.smit.strategy.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.service.FileService;
import xyz.dreature.smit.service.registry.FileServiceRegistry;
import xyz.dreature.smit.strategy.ExtractStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 文件-完整抽取策略
@Slf4j
@Component
@Lazy
public class FullStrategy<S> implements ExtractStrategy<S> {
    @Autowired
    private FileServiceRegistry registry;

    @Override
    public String getKey() {
        return "file:full";
    }

    @Override
    public List<S> extract(Map<String, ?> fileParams) {
        String dataSource = (String) fileParams.get("dataSource");
        FileService<S> service = registry.getService(dataSource);
        String location = (String) fileParams.get("location");

        List<Resource> resources = service.collect(location);
        List<S> result = service.readBatch(resources);
        return result;
    }

    @Override
    public List<S> extractBatch(List<? extends Map<String, ?>> filesParams) {
        // 假设批次内数据源相同，故而取首个获取，后续可按需调整
        String dataSource = (String) filesParams.get(0).get("dataSource");
        FileService<S> service = registry.getService(dataSource);

        List<Resource> resources = new ArrayList<>();
        for (Map<String, ?> fileParams : filesParams) {
            String location = (String) fileParams.get("location");
            resources.addAll(service.collect(location));
        }
        return service.readBatch(resources);
    }
}
