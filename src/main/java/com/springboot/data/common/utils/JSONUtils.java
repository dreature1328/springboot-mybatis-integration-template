package com.springboot.data.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    // JSON 对象转 Java 对象
    public static <T> T JSONObjToObj(JSONObject jsonObj, Class<T> clazz){
        if(jsonObj == null) return null;
        else return jsonObj.toJavaObject(clazz);
    }
    // JSON 数组转 JSON 字符串
    public static String JSONArrToStr(JSONArray jsonArr){
        return JSON.toJSONString(jsonArr);
    }
    // JSON 数组转列表
    public static <T> List<T> JSONArrToList(JSONArray jsonArr, Class<T> clazz){
        if(jsonArr == null) return null;
        else return jsonArr.toJavaList(clazz);
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
    // 从 JSON 文件读取出 JSON 对象
    public static JSONObject readJSONFile(String filename) {
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
        return JSON.parseObject(jsonStr);
    }

    // 读取并输出 JSON 对象的键值对（含嵌套）
    public static void printJSON(JSONObject jsonObj, int level) {
        if (jsonObj != null) {
            for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String indent = String.join("", Collections.nCopies(level, "\t")); // 缩进
                if (value instanceof JSONObject) {
                    // 嵌套对象
                    System.out.println(indent + key + " = {");
                    printJSON((JSONObject) value, level + 1); // 增加嵌套层级
                    System.out.println(indent + "}");
                } else {
                    // 非嵌套对象
                    System.out.println(indent + key + " = " + value.toString());
                }
            }
        }
    }
    // 生成含有的随机值 JSON 对象
    public static JSONObject generateMockJSON(List<String> keys, int length) {
        // keys 是 JSON 中的键名，length 是每个键的随机生成值的长度
        // 调用示例如 generateMockJSON(Arrays.asList(new String[]{"id", "key1","key2"}),16)

        // baseStr 中每个字符是随机生成字符串中每个字符可能的取值
        String baseStr ="ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz0123456789";
        JSONObject jsonObj = new JSONObject();
        // 由 Random 生成随机数
        Random random = new Random();
        for (String key : keys) {
            StringBuffer buffer = new StringBuffer();
            for(int i = 0; i < length; i++){
                int number = random.nextInt(baseStr.length());
                buffer.append(baseStr.charAt(number));
            }
            jsonObj.put(key, buffer.toString());
        }
        return jsonObj;
    }
    // 将 JSON 对象写入文件
    public static void writeJSONFile(JSONObject jsonObject, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonObject.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}

