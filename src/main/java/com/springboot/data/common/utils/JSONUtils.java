package com.springboot.data.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.List;
import java.util.Map;

public class JSONUtils {
    // JSON 对象对应的字符串是用大括号 {} 包裹起来的，如 {"code":"200","msg":"success","data":null}
    // JSON 数组对应的字符串是用方括号 [] 包裹起来的，如 [{"x":"1","y":"2"},{"x":"3","y":"4"}]

    // JSON 字符串转 JSON 对象
    public static JSONObject strToJSONObj(String jsonStr){
        return JSON.parseObject(jsonStr);
    }
    // JSON 字符串转 JSON 数组
    public static JSONArray strToJSONArr(String jsonStr){
        return JSON.parseArray(jsonStr);
    }
    // JSON 字符串转 Java 对象
    public static <T> T strToObj(String jsonStr, Class<T> clazz){
        return JSON.parseObject(jsonStr, clazz);
    }
    // JSON 字符串转列表
    public static <T> List<T> strToList(String jsonStr, Class<T> clazz){
        return JSON.parseArray(jsonStr, clazz);
    }
    // JSON 对象转 JSON 字符串
    public static String JSONObjToStr(JSONObject obj){
        return JSON.toJSONString(obj);
    }
    // JSON 数组转 JSON 字符串
    public static String JSONArrToStr(JSONArray jsonArr){
        return JSON.toJSONString(jsonArr);
    }
    // JSON 数组转列表
    public static <T> List<T> JSONArrToList(JSONArray jsonArr, Class<T> clazz){
        return JSON.parseArray(jsonArr.toJSONString(), clazz);
    }
    // Java 对象转 JSON 字符串
    public static String objToStr(Object obj){
        return JSON.toJSONString(obj);
    }
    // Java 对象转 JSON 对象
    public static JSONObject objToJSONObj(Object obj){
        return (JSONObject) JSON.toJSON(obj);
    }
    // 列表转 JSON 数组
    public static JSONArray listToJSONArr(List list){
        return JSONArray.parseArray(JSON.toJSONString(list));
    }
    // 从本地 JSON 文件读取出 JSON 字符串
    public static String readJSONFile(String filename){
        // filename 是包括路径的文件名
        String jsonStr = "";
        File jsonFile = new File(filename);
        try {
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((ch = reader.read()) != -1){
                stringBuffer.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = stringBuffer.toString();
        } catch (FileNotFoundException e){
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }
    // 读取并输出 JSON 对象的键值对
    public static void outputJSONObj(JSONObject jsonObj) {
        if(jsonObj != null) for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

}

