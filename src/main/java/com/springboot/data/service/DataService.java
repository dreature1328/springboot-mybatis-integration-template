package com.springboot.data.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.data.common.pojo.Data;
import com.springboot.data.common.utils.HTTPUtils;
import com.springboot.data.mapper.DataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.springboot.data.common.utils.HTTPUtils.asyncHTTPRequest;
import static com.springboot.data.common.utils.HTTPUtils.requestHTTPContent;

@Service
public class DataService {


    @Autowired
    private DataMapper dataMapper;

    // 请求头示例
    private static Map<String, String> headers = HTTPUtils.headers;
    static {
        final String key="";
        final String value="";
        // 添加自定义请求头，key 和 value 是你需要添加的头信息的键与值，如用于鉴权
        headers.put(key, value);

    }

    public <T> void pageHandle(List<T> list, int pageSize, Consumer<List<T>> handleFunction){
        int count = 0;
        while (!list.isEmpty()) {
            // 取出当前页数据
            List<T> subList = list.subList(0, Math.min(pageSize, list.size()));
            // 执行插入或更新操作
            handleFunction.accept(subList);
            // 统计插入或更新的记录数
            count += subList.size();
            // 从列表中移除已处理的数据
            list.subList(0, subList.size()).clear();
        }
    }

    public <T, R> List<R> pageHandle(List<T> list, int pageSize, Function<List<T>, List<R>> handleFunction) {
        List<R> resultList = new ArrayList<>();
        int count = 0;
        while (!list.isEmpty()) {
            // 取出当前页数据
            List<T> subList = list.subList(0, Math.min(pageSize, list.size()));
            // 执行查询操作
            List<R> subResultList = handleFunction.apply(subList);
            // 将结果添加到总结果列表中
            resultList.addAll(subResultList);
            // 统计查询的记录数
            count += subList.size();
            // 从列表中移除已处理的数据
            list.subList(0, subList.size()).clear();
        }
        return resultList;
    }

    // 集成
    public void integrateData(List<? extends Map<String,?>> paramList) throws Exception {

        // 同步请求并获取响应内容
        List<String> responses = requestData(paramList);

        // 依次加工数据，将响应内容加工成对象列表
        List<Data> dataList = processData(responses);

        // 将对象依次插入或更新进数据库
        insertOrUpdateData(dataList);

        return ;
    }

    // 优化集成
    public void integrateDataOptimized(List<? extends Map<String,?>> paramList){

        // 分页异步请求并获取响应内容
        List<String> responses = pageRequestData(paramList);

        // 流水线加工数据，将响应内容加工成对象列表
        List<Data> DataList = processData(responses);

        // 将对象分页插入或更新进数据库
        pageInsertOrUpdateData(DataList);

        return ;
    }

    // 分页优化集成
    public void pageIntegrateDataOptimized(List<? extends Map<String,?>> paramList){
        int pageSize = 300;
        pageHandle(paramList, pageSize, this::integrateDataOptimized);
    }

    // 单项查询
    public Data selectData(String id) {
        return dataMapper.selectData(id);
    }

    // 依次查询
    public List<Data> selectData(String... idArray) {
        List<Data> dataList = new ArrayList<>();
        for(String id : idArray){
            dataList.add(dataMapper.selectData(id));
        }
        return dataList;
    }

    // 批量查询
    public List<Data> batchSelectData(List<String> idList) {
        return dataMapper.batchSelectData(idList);
    }

    // 分页查询
    public List<Data> pageSelectData(List<String> idList) {
        int pageDataSize = 12000; // 页面数据量大小，即每页记录数 × 字段数，可自行设置
        int totalFields = Data.class.getDeclaredFields().length; // 总字段数，即数据表中的列数
        int pageSize = pageDataSize / totalFields; // 页面大小，即每页记录数

        return pageHandle(idList, pageSize, dataMapper::batchSelectData);
    }

    // 生成请求参数
    public List<Map<String, String>> generateDataParams() {
        // 总请求数
        int totalRequests = 1000;
        // 自己按需求生成自定义参数列表
        List<Map<String, String>> paramsList = new ArrayList<>();

        for(int i = 0; i < totalRequests; i++) {
            // key 和 value 是你需要添加的参数名与参数值
            String key = "";
            String value = "";
            paramsList.add(new HashMap<String, String>(){{
                put(key, value);
            }});
        }
        return paramsList;
    }

    // 单次请求
    public String requestData(Map<String, ?> params) throws Exception {
        String strURL = "http://www.example.com";
        String method = "GET";

        String response = requestHTTPContent(strURL, method, headers, params);
        return response;
    }

    // 依次请求
    public List<String> requestData(List<? extends Map<String,?>> paramList) throws Exception {
        List<String> responses = new ArrayList<>();
        for(Map<String, ?> params : paramList) {
            responses.add(requestData(params));
        }
        return responses;
    }

    // 批量异步请求
    public List<String> batchRequestData(List<? extends Map<String,?>> paramList) {
        String strURL = "http://www.example.com";
        String method = "GET";
        List<CompletableFuture<String>> futures = new ArrayList<>();

        // 添加异步请求任务
        for (Map<String, ?> params : paramList) {
            CompletableFuture<String> future = asyncHTTPRequest(strURL, method, headers, params);
            futures.add(future);
        }

        // 等待异步任务完成，超时时间为 30 分钟
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(1800, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("请求超时");
            e.printStackTrace();
            return Collections.emptyList();
        }

        // 将所有异步请求的结果获取为 List<String>
        List<String> responses = new ArrayList<>();
        for (CompletableFuture<String> future : futures) {
            try {
                responses.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return responses;
    }

    // 分页异步请求
    public List<String> pageRequestData(List<? extends Map<String,?>> paramList) {
        int pageSize = 300;
        return pageHandle(paramList, pageSize, this::batchRequestData);
    }

    // 单项加工
    public List<Data> processData(String response) {
        List<Data> dataList = new ArrayList<>();
        JSONObject jsonObj = JSON.parseObject(response);
        if (jsonObj != null) {
            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组
            if (jsonInfo != null) {
                for (int i = 0; i < jsonInfo.size(); i++) {// 遍历 JSON 数组依次取出 JSON 对象
                    JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);
                    dataList.add(new Data(

                            jsonDetailInfo.getString("id"),
                            jsonDetailInfo.getString("key1"),
                            jsonDetailInfo.getString("key2")
                    ));
                }
            }
        }
        return dataList;
    }

    // 依次加工
    public List<Data> processData(List<String> responses) {
        List<Data> dataList = new ArrayList<>();
        for (String response : responses) {
            dataList.addAll(processData(response));
        }
        return dataList;
    }

    // 流水线加工
    public List<Data> pielineProcessData(List<String> responses) {
        return responses.stream()
                .map(JSON::parseObject)
                .filter(Objects::nonNull)
                .flatMap(jsonObj -> jsonObj.getJSONArray("data").stream())
                .map(jsonDetailInfo -> new Data(

                        ((JSONObject) jsonDetailInfo).getString("id"),
                        ((JSONObject) jsonDetailInfo).getString("key1"),
                        ((JSONObject) jsonDetailInfo).getString("key2")
                ))
                .collect(Collectors.toList());
    }

    // 依次插入或更新
    public void insertOrUpdateData(List<Data> dataList) {
        for(Data data : dataList) {
            dataMapper.insertOrUpdateData(data);
        }
        return ;
    }

    // 分页插入或更新
    public void pageInsertOrUpdateData(List<Data> dataList) {
        int pageDataSize = 12000; // 页面数据量大小，即每页记录数 × 字段数，可自行设置
        int totalFields = Data.class.getDeclaredFields().length; // 总字段数，即数据表中的列数
        int pageSize = pageDataSize / totalFields; // 页面大小，即每页记录数

        pageHandle(dataList, pageSize, dataMapper::batchInsertOrUpdateData);
        return ;
    }

    // 清空
    public void clearData() {
        dataMapper.clearData();
        return ;
    }



}
