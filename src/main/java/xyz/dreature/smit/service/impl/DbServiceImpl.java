package xyz.dreature.smit.service.impl;

import org.springframework.transaction.annotation.Transactional;
import xyz.dreature.smit.common.util.BatchUtils;
import xyz.dreature.smit.mapper.BaseMapper;
import xyz.dreature.smit.service.DbService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

// 数据库服务
@Transactional
public class DbServiceImpl<T, ID extends Serializable> implements DbService<T, ID> {
    // ORM 映射器
    private final BaseMapper<T, ID> mapper;

    public DbServiceImpl(BaseMapper<T, ID> mapper) {
        this.mapper = mapper;
    }

    // 查询总数
    @Override
    public int countAll() {
        return mapper.countAll();
    }

    // 查询全表
    @Override
    public List<T> selectAll() {
        return mapper.selectAll();
    }

    // 查询随机
    @Override
    public List<T> selectRandom(int count) {
        return mapper.selectRandom(count);
    }

    // 查询页面
    @Override
    public List<T> selectByPage(int offset, int limit) {
        return mapper.selectByPage(offset, limit);
    }

    // 单项查询
    @Override
    public T selectById(ID id) {
        return mapper.selectById(id);
    }

    // 逐项查询
    @Override
    public List<T> selectByIds(ID... ids) {
        return BatchUtils.mapEach(Arrays.asList(ids), mapper::selectById);
    }

    // 单批查询
    @Override
    public List<T> selectBatchByIds(List<ID> ids) {
        return mapper.selectBatchByIds(ids);
    }

    // 分批查询
    @Override
    public List<T> selectBatchByIds(List<ID> ids, int batchSize) {
        return BatchUtils.flatMapBatch(ids, batchSize, mapper::selectBatchByIds);
    }

    // 单项插入
    @Override
    public int insert(T entity) {
        return mapper.insert(entity);
    }

    // 逐项插入
    @Override
    public int insert(T... entities) {
        return BatchUtils.reduceEachToInt(Arrays.asList(entities), mapper::insert);
    }

    // 单批插入
    @Override
    public int insertBatch(List<T> list) {
        return mapper.insertBatch(list);
    }

    // 分批插入
    @Override
    public int insertBatch(List<T> list, int batchSize) {
        return BatchUtils.reduceBatchToInt(list, batchSize, mapper::insertBatch);
    }

    // 单项更新
    @Override
    public int update(T entity) {
        return mapper.update(entity);
    }

    // 逐项更新
    @Override
    public int update(T... entities) {
        return BatchUtils.reduceEachToInt(Arrays.asList(entities), mapper::update);
    }

    // 单批更新
    @Override
    public int updateBatch(List<T> list) {
        return mapper.updateBatch(list);
    }

    // 分批更新
    @Override
    public int updateBatch(List<T> list, int batchSize) {
        return BatchUtils.reduceBatchToInt(list, batchSize, mapper::updateBatch);
    }

    // 单项插入或更新
    @Override
    public int upsert(T entity) {
        return mapper.upsert(entity);
    }

    // 逐项插入或更新
    @Override
    public int upsert(T... entities) {
        return BatchUtils.reduceEachToInt(Arrays.asList(entities), mapper::upsert);
    }

    // 单批插入或更新
    @Override
    public int upsertBatch(List<T> list) {
        return mapper.upsertBatch(list);
    }

    // 分批插入或更新
    @Override
    public int upsertBatch(List<T> list, int batchSize) {
        return BatchUtils.reduceBatchToInt(list, batchSize, mapper::upsertBatch);
    }

    // 单项删除
    @Override
    public int deleteById(ID id) {
        return mapper.deleteById(id);
    }

    // 逐项删除
    @Override
    public int deleteByIds(ID... ids) {
        return BatchUtils.reduceEachToInt(Arrays.asList(ids), mapper::deleteById);
    }

    // 单批删除
    @Override
    public int deleteBatchByIds(List<ID> ids) {
        return mapper.deleteBatchByIds(ids);
    }

    // 分批删除
    @Override
    public int deleteBatchByIds(List<ID> ids, int batchSize) {
        return BatchUtils.reduceBatchToInt(ids, batchSize, mapper::deleteBatchByIds);
    }

    // 清空
    @Override
    public void truncate() {
        mapper.truncate();
    }
}
