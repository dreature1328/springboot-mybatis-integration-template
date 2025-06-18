package dreature.smit.service.impl;

import dreature.smit.entity.Data;
import dreature.smit.service.LoadService;
import org.springframework.stereotype.Service;

import java.util.List;

import static dreature.smit.common.util.BatchUtils.reduceBatch;

@Service
public class DataLoadServiceImpl extends BaseServiceImpl<Data> implements LoadService<Data> {
    // ----- 数据库持久化 -----
    // 分批插入
    public int insertBatch(List<Data> dataList, int batchSize) {
        return reduceBatch(dataList, batchSize, baseMapper::insertBatch);
    }

    // 分批更新
    public int updateBatch(List<Data> dataList, int batchSize) {
        return reduceBatch(dataList, batchSize, baseMapper::updateBatch);
    }

    // 分批插入或更新
    public int upsertBatch(List<Data> dataList, int batchSize) {
        return reduceBatch(dataList, batchSize, baseMapper::upsertBatch);
    }

    // 分批删除
    public int deleteBatchByIds(List<String> ids, int batchSize) {
        return reduceBatch(ids, batchSize, baseMapper::deleteBatchByIds);
    }

    // 清空
    public void truncate() {
        baseMapper.truncate();
    }
}
