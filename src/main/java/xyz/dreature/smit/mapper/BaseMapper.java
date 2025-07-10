package xyz.dreature.smit.mapper;

import java.io.Serializable;
import java.util.List;

public interface BaseMapper<T, ID extends Serializable> {
    // 查询总数
    int countAll();

    // 查询全表
    List<T> selectAll();

    // 查询随机
    List<T> selectRandom(int count);

    // 查询页面
    List<T> selectByPage(int offset, int limit);

    // 单项查询
    T selectById(ID id);

    // 单批查询
    List<T> selectBatchByIds(List<ID> ids);

    // 单项插入
    int insert(T entity);

    // 单批插入
    int insertBatch(List<T> entities);

    // 单项更新
    int update(T entity);

    // 单批更新
    int updateBatch(List<T> entities);

    // 单项插入或更新
    int upsert(T entity);

    // 单批插入或更新
    int upsertBatch(List<T> entities);

    // 单项删除
    int deleteById(ID id);

    // 单批删除
    int deleteBatchByIds(List<ID> ids);

    // 清空
    void truncate();
}
