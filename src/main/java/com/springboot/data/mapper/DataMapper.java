package com.springboot.data.mapper;

import com.springboot.data.common.pojo.Data;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DataMapper {
    public void clearData();
    public Data selectData(String id);
    public List<Data> batchSelectData(List<String> idList);
    public void insertData(Data data);
    public void batchInsertData(List<Data> dataList);
    public void updateData(Data data);

    public void batchUpdateData(List<Data> dataList);
    public void insertOrUpdateData(Data data);

    public void batchInsertOrUpdateData(List<Data> dataList);
    public void deleteData(String id);
    public void batchDeleteData(List<String> idList);
}
