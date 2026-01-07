package xyz.dreature.smit.service.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.service.FileService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Lazy
public class FileServiceRegistry {
    // 文件服务集合
    private final Map<String, FileService<?>> serviceMap;

    // 注册文件服务（自动）
    @Autowired
    public FileServiceRegistry(List<FileService<?>> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(
                        service -> service.getKey(),
                        Function.identity()
                ));
    }

    // 注册文件库服务（手动）
    public <T> void register(String key, FileService<T> service) {
        serviceMap.put(key, service);
    }

    // 获取文件服务
    public <T> FileService<T> getService(String key) {
        FileService<T> service = (FileService<T>) serviceMap.get(key);
        if (service == null) {
            throw new IllegalArgumentException("未找到数据库服务: " + key);
        }
        return service;
    }
}
