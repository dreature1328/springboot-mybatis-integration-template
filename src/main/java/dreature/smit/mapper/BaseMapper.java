package dreature.smit.mapper;

import java.util.List;

public interface BaseMapper<T> {
    // 查询总数
    public int countAll();

    // 查询全表
    List<T> findAll();

    // 查询 count 条
    List<T> findRandomN(int count);

    // 单项查询
    public List<T> selectById(String id);

    // 单批查询
    public List<T> selectBatchByIds(List<String> ids);

    // 单项插入
    public int insert(T obj);

    // 单批插入
    public int insertBatch(List<T> list);

    // 单项更新
    public int update(T obj);

    // 单批更新
    public int updateBatch(List<T> list);

    // 单项插入或更新
    public int upsert(T obj);

    // 单批插入或更新
    public int upsertBatch(List<T> list);

    // 单项删除
    public int deleteById(String id);

    // 单批删除
    public int deleteBatchByIds(List<String> ids);

    // 清空
    public void truncate();
}
