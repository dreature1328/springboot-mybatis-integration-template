package xyz.dreature.smit.service;

import java.util.List;

// 文件服务接口
public interface FileService<T> {
    // 单份读取
    T read(String filePath);

    // 单批异步读取
    List<T> readBatch(List<String> filePaths);
}
