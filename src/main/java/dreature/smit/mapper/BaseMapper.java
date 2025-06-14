package dreature.smit.mapper;

import java.util.List;

public interface BaseMapper<T> {
    // 查询总数
    public int countAll();
    // 查询 n 条
    List<T> findTopN(int n);
    // 依次查询
    public List<T> selectById(String id);
    // 批量查询
    public List<T> selectBatchByIds(List<String> ids);
    // 依次插入
    public int insert(T obj);
    // 批量插入
    public int insertBatch(List<T> list);
    // 依次更新
    public int update(T obj);
    // 批量更新
    public int updateBatch(List<T> list);
    // 依次插入或更新
    public int upsert(T obj);
    // 批量插入或更新
    public int upsertBatch(List<T> list);
    // 依次删除
    public int deleteById(String id);
    // 批量删除
    public int deleteBatchByIds(List<String> ids);
    // 清空
    public void truncate();

}
