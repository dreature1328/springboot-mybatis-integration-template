package dreature.smit.service;
import java.util.List;

public interface LoadService<T> {
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
}
