package xyz.dreature.smit.service;

import java.util.List;

// 消息队列服务接口
public interface MqService<S, T> {
    // ===== 消息队列抽取 =====
    int countAll();

    // 单次同步接收（自定义转换）
    S receive();

    // 单次同步接收（模板转换器）
    S receiveWithConverter();

    // 依次同步接收（指定数量，自定义转换）
    List<S> receive(int count);

    // 依次同步接收（指定数量，模板转换器）
    List<S> receiveWithConverter(int count);

    // 单批同步接收（自定义转换）
    List<S> receiveBatch();

    // 单批同步接收（模板转换器）
    List<S> receiveBatchWithConverter();

    // 分批同步接收（自定义转换）
    List<S> receiveBatch(int count);

    // 分批同步接收（模板转换器）
    List<S> receiveBatchWithConverter(int count);

    // ===== 消息队列加载 =====
    // 逐项发送（异步回调，自定义转换）
    int send(List<T> payloads);

    // 逐项发送（异步回调，模板转换器）
    int sendWithConverter(List<T> payloads);

    // 单批发送（异步回调，自定义转换）
    int sendBatch(List<T> payloads);

    // 单批发送（异步回调，模板转换器）
    int sendBatchWithConverter(List<T> payloads);

    // 分批发送（异步回调，自定义转换）
    int sendBatch(List<T> payloads, int batchSize);

    // 分批发送（异步回调，模板转换器）
    int sendBatchWithConverter(List<T> payloads, int batchSize);
}
