package xyz.dreature.smit.service.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.service.DbService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 数据库服务注册器
@Component
@Lazy
public class DbServiceRegistry {
    // 数据库服务集合
    private final Map<String, DbService<?, ?>> serviceMap;

    // 注册数据库服务（自动）
    @Autowired
    public DbServiceRegistry(List<DbService<?, ?>> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(
                        service -> service.getKey(),
                        Function.identity()
                ));
    }

    // 注册数据库服务（手动）
    public <T, ID extends Serializable> void register(String key, DbService<T, ID> service) {
        serviceMap.put(key, service);
    }

    // 获取数据库服务
    public <T, ID extends Serializable> DbService<T, ID> getService(String key) {
        DbService<T, ID> service = (DbService<T, ID>) serviceMap.get(key);
        if (service == null) {
            throw new IllegalArgumentException("未找到数据库服务: " + key);
        }
        return service;
    }
}
