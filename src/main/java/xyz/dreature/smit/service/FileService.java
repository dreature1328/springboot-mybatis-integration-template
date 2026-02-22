package xyz.dreature.smit.service;

import org.springframework.core.io.Resource;

import java.util.List;

// 文件服务接口
public interface FileService<T> {
    // 获取服务键（用于注册）
    String getKey();

    // 收集资源
    List<Resource> collect(String location);

    // 单份读取
    T read(Resource resource);

    // 单批异步读取
    List<T> readBatch(List<Resource> resources);
}
