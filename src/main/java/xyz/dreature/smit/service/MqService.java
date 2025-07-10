package xyz.dreature.smit.service;

import java.util.List;

// 消息队列服务接口
public interface MqService<S, T> {
    // ===== 消息队列抽取 =====
    // 单次同步接收
    S receive();

    // 依次同步接收（指定数量）
    List<S> receive(int count);

    // 依次同步接收（所有消息）
    List<S> receiveAll();

    // ===== 消息发布（中转） =====
    // 逐项发送（异步回调）
    int send(List<T> dataList);

    // 单批发送（异步回调）
    int sendBatch(List<T> dataList);

    // 分批发送（异步回调）
    int sendBatch(List<T> dataList, int batchSize);
}
