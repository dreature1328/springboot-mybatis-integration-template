package xyz.dreature.smit.service;

import org.apache.ibatis.cursor.Cursor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

// 数据库服务接口
public interface DbService<T, ID extends Serializable> {
    // 获取服务键（用于注册）
    String getKey();

    // 查询总数
    int countAll();

    // 查询全部
    List<T> selectAll();

    // 查询全部（游标）
    Cursor<T> selectAllWithCursor();

    // 处理全部（游标）
    void processAllWithCursor(Consumer<T> processor);

    // 转换全部（游标）
    <R> List<R> transformAllWithCursor(Function<T, List<R>> transformer);

    // 查询随机
    List<T> selectRandom(int limit);

    // 查询页面
    List<T> selectByPage(int offset, int limit);

    // 条件查询
    List<T> selectByCondition(Map<String, Object> condition);

    // 单项查询
    T selectById(ID id);

    // 逐项查询
    List<T> selectByIds(ID... ids);

    // 单批查询
    List<T> selectBatchByIds(List<ID> ids);

    // 分批查询
    List<T> selectBatchByIds(List<ID> ids, int batchSize);

    // 单项插入
    int insert(T entity);

    // 逐项插入
    int insert(T... entities);

    // 单批插入
    int insertBatch(List<T> entities);

    // 分批插入
    int insertBatch(List<T> entities, int batchSize);

    // 单项更新
    int update(T entity);

    // 逐项更新
    int update(T... entities);

    // 单批更新
    int updateBatch(List<T> entities);

    // 分批更新
    int updateBatch(List<T> entities, int batchSize);

    // 单项插入或更新
    int upsert(T entity);

    // 逐项插入或更新
    int upsert(T... entities);

    // 单批插入或更新
    int upsertBatch(List<T> entities);

    // 分批插入或更新
    int upsertBatch(List<T> entities, int batchSize);

    // 单项删除
    int deleteById(ID id);

    // 逐项删除
    int deleteByIds(ID... ids);

    // 单批删除
    int deleteBatchByIds(List<ID> ids);

    // 分批删除
    int deleteBatchByIds(List<ID> ids, int batchSize);

    // 清空
    void truncate();
}
