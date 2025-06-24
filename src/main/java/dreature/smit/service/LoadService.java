package dreature.smit.service;
import dreature.smit.entity.Data;

import java.util.List;

public interface LoadService<T> {
    // ----- 数据库持久化 -----
    // 分批插入
    int insertBatch(List<T> dataList, int batchSize);

    // 分批更新
    int updateBatch(List<T> dataList, int batchSize);

    // 分批插入或更新
    int upsertBatch(List<T> dataList, int batchSize);

    // 分批删除
    int deleteBatchByIds(List<String> idList, int batchSize);

    // 清空
    void truncate();

    // ----- 消息发布（中转） -----
    // 逐项发送（异步回调）
    int send(List<Data> dataList);

    // 单批发送（异步回调）
    int sendBatch(List<Data> dataList);

    // 分批发送（异步回调）
    int sendBatch(List<Data> dataList, int batchSize);
}
