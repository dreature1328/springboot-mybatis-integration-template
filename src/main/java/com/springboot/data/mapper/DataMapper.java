package com.springboot.data.mapper;

import com.springboot.data.common.pojo.Data;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DataMapper {
    // 依次查询
    public Data selectData(String id);
    // 批量查询
    public List<Data> batchSelectData(List<String> idList);
    // 依次插入或更新
    public void insertOrUpdateData(Data data);
    // 批量插入或更新
    public void batchInsertOrUpdateData(List<Data> dataList);
    // 清空
    public void clearData();
}
