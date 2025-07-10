package xyz.dreature.smit.service.impl;

import xyz.dreature.smit.common.util.BatchUtils;
import xyz.dreature.smit.mapper.BaseMapper;
import xyz.dreature.smit.service.DbService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

// 数据库服务
public class DbServiceImpl<T, ID extends Serializable> implements DbService<T, ID> {
    private BaseMapper<T, ID> baseMapper;
    private Function<String, ID> idconverter;

    public DbServiceImpl(BaseMapper<T, ID> baseMapper, Function<String, ID> idconverter) {
        this.baseMapper = baseMapper;
        this.idconverter = idconverter;
    }

    // 解析 ID
    public List<ID> parseIdsFromString(String ids) {
        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return idconverter.apply(s);
                    } catch (RuntimeException e) {
                        throw new IllegalArgumentException("无效的 ID 格式: " + s, e);
                    }
                })
                .collect(Collectors.toList());
    }

    // 查询总数
    public int countAll() {
        return baseMapper.countAll();
    }

    // 查询全表
    public List<T> selectAll() {
        return baseMapper.selectAll();
    }

    // 查询随机
    public List<T> selectRandom(int count) {
        return baseMapper.selectRandom(count);
    }

    // 查询页面
    public List<T> selectByPage(int offset, int limit) {
        return baseMapper.selectByPage(offset, limit);
    }

    // 单项查询
    public T selectById(ID id) {
        return baseMapper.selectById(id);
    }

    // 逐项查询
    public List<T> selectByIds(ID... ids) {
        return BatchUtils.mapEach(Arrays.asList(ids), baseMapper::selectById);
    }

    // 单批查询
    public List<T> selectBatchByIds(List<ID> ids) {
        return baseMapper.selectBatchByIds(ids);
    }

    // 分批查询
    public List<T> selectBatchByIds(List<ID> ids, int batchSize) {
        return BatchUtils.flatMapBatch(ids, batchSize, baseMapper::selectBatchByIds);
    }

    // 单项插入
    public int insert(T entity) {
        return baseMapper.insert(entity);
    }

    // 逐项插入
    public int insert(T... entities) {
        return BatchUtils.reduceEachToInt(Arrays.asList(entities), baseMapper::insert);
    }

    // 单批插入
    public int insertBatch(List<T> list) {
        return baseMapper.insertBatch(list);
    }

    // 分批插入
    public int insertBatch(List<T> list, int batchSize) {
        return BatchUtils.reduceBatchToInt(list, batchSize, baseMapper::insertBatch);
    }

    // 单项更新
    public int update(T entity) {
        return baseMapper.update(entity);
    }

    // 逐项更新
    public int update(T... entities) {
        return BatchUtils.reduceEachToInt(Arrays.asList(entities), baseMapper::update);
    }

    // 单批更新
    public int updateBatch(List<T> list) {
        return baseMapper.updateBatch(list);
    }

    // 分批更新
    public int updateBatch(List<T> list, int batchSize) {
        return BatchUtils.reduceBatchToInt(list, batchSize, baseMapper::updateBatch);
    }

    // 单项插入或更新
    public int upsert(T entity) {
        return baseMapper.upsert(entity);
    }

    // 逐项插入或更新
    public int upsert(T... entities) {
        return BatchUtils.reduceEachToInt(Arrays.asList(entities), baseMapper::upsert);
    }

    // 单批插入或更新
    public int upsertBatch(List<T> list) {
        return baseMapper.upsertBatch(list);
    }

    // 分批插入或更新
    public int upsertBatch(List<T> list, int batchSize) {
        return BatchUtils.reduceBatchToInt(list, batchSize, baseMapper::upsertBatch);
    }

    // 单项删除
    public int deleteById(ID id) {
        return baseMapper.deleteById(id);
    }

    // 逐项删除
    public int deleteByIds(ID... ids) {
        return BatchUtils.reduceEachToInt(Arrays.asList(ids), baseMapper::deleteById);
    }

    // 单批删除
    public int deleteBatchByIds(List<ID> ids) {
        return baseMapper.deleteBatchByIds(ids);
    }

    // 分批删除
    public int deleteBatchByIds(List<ID> ids, int batchSize) {
        return BatchUtils.reduceBatchToInt(ids, batchSize, baseMapper::deleteBatchByIds);
    }

    // 清空
    public void truncate() {
        baseMapper.truncate();
    }
}
