package com.springboot.data.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.data.common.pojo.Data;
import com.springboot.data.mapper.DataMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class DataService {

    @Autowired
    private DataMapper dataMapper;

    // 请求头示例
    private Map<String, String> headers = new HashMap<String, String>(){{
        // 设置接收内容类型
        put("Accept","application/json");
        // 设置发送内容类型
        put("Content-Type","application/json;charset=UTF-8");
        // 设置字符集
        put("charset", "UTF-8");
        // 设置访问者系统引擎版本、浏览器信息的字段信息，此处伪装成用户通过浏览器访问
        put("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
    }};
    private static void disableSslVerification() {
        try
        {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
    public void addHeadersToRequest(HttpURLConnection httpConn, Map headers){
        // headers 是 Map<Object, Object> 或 Map<Object, Object[]> 类型的请求头，键与值分别是请求头名与请求头值，有重复同名请求头时将多个值放进数组

        Set headersSet = headers.entrySet();
        Iterator<Map.Entry> it = headersSet.iterator();

        if (it.hasNext()) { // 请求头键值对不为空
            // 若值为数组（请求头含有同名重复的）
            if (it.next().getValue().getClass().isArray()) {
                for(Map.Entry<Object, Object[]> entry : (Set<Map.Entry<Object, Object[]>>)headersSet){
                    for (Object value : entry.getValue()) {
                        httpConn.setRequestProperty(entry.getKey().toString(), value.toString());
                    }
                }
            }
            // 若值不为数组（请求头不含有同名重复的）
            else {
                for(Map.Entry<Object, Object> entry:(Set<Map.Entry<Object, Object>>)headersSet){
                    httpConn.setRequestProperty(entry.getKey().toString(), entry.getValue().toString());
                }
            }
        }
        return ;
    }
    public String concatParamsToURL(String staticURL, String paramsStr) throws Exception {
        return staticURL + paramsStr;
    }
    public String concatParamsToURL(String staticURL, Map params) throws Exception {
        // staticURL 是字符串形式的静态 URL
        // params 是 Map<Object, Object> 或 Map<Object, Object[]> 类型的请求参数，键与值分别是参数名与参数值，URL 有重复同名参数时将多个值放进数组
        // 将请求参数拼接进 URL
        Set paramsSet = params.entrySet();
        Iterator<Map.Entry> it = paramsSet.iterator();
        String strURL = staticURL;

        if (it.hasNext()) { // 参数键值对不为空
            int paramIndex = 0;
            // 若值为数组（URL 含有同名重复参数）
            if (it.next().getValue().getClass().isArray()) {
                for (Map.Entry<Object, Object[]> entry : (Set<Map.Entry<Object, Object[]>>)paramsSet) {
                    for (Object value : entry.getValue()) {
                        if (paramIndex == 0 && strURL.indexOf("?") == -1) strURL += "?";
                        else strURL += "&";
                        // 为了避免中文乱码等问题，将参数值进行转码再拼接进 URL
                        strURL += URLEncoder.encode(entry.getKey().toString(), "utf-8") + "=" + URLEncoder.encode(value.toString(), "utf-8");
                        paramIndex++;
                    }
                }
            }
            // 若值不为数组（URL 不含有同名重复参数）
            else {
                for (Map.Entry<Object, Object> entry : (Set<Map.Entry<Object, Object>>)paramsSet) {
                    if (paramIndex == 0 && strURL.indexOf("?") == -1) strURL += "?";
                    else strURL += "&";
                    // 为了避免中文乱码等问题，将参数值进行转码再拼接进 URL
                    strURL += URLEncoder.encode(entry.getKey().toString(), "utf-8") + "=" + URLEncoder.encode(entry.getValue().toString(), "utf-8");
                    paramIndex++;
                }
            }
        }
        return strURL;
    }
    // 重载 getResponseContent()，相当于参数有默认值
    public String getResponseContent(String strURL) throws Exception {
        return getResponseContent(strURL, "GET", null, null);
    }
    public String getResponseContent(String strURL, String method) throws Exception {
        return getResponseContent(strURL, method, null, null);
    }
    public String getResponseContent(String strURL, String method, Map headers) throws Exception {
        return getResponseContent(strURL, method, headers, null);
    }
    public String getResponseContent(String strURL, String method, Map headers, Map params) throws Exception {
        // strURL 是 String 类型的 URL
        // method 是 String 类型的请求方法，为 "GET" 或 "POST"
        // headers 是 Map<Object, Object> 或 Map<Object, Object[]> 类型的请求头，键与值分别是请求头名与请求头值，有重复同名请求头时将多个值放进数组
        // params 是 Map<Object, Object> 或 Map<Object, Object[]> 类型的请求参数，键与值分别是参数名与参数值，URL 有重复同名参数时将多个值放进数组

        // 忽略验证 https 中 SSL 证书
        disableSslVerification();

        // GET 方法下，query 参数拼接在 URL 字符串末尾
        if(method == "GET" && params!=null && !params.isEmpty()){
            strURL = concatParamsToURL(strURL, params);
        }

        URL url = new URL(strURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod(method);

        // 添加 HTTP 请求头
        addHeadersToRequest(httpConn, headers);

        // 请求时是否使用缓存
        httpConn.setUseCaches(false);

        // POST 方法请求必须设置下面两项
        // 设置是否从 HttpUrlConnection 的对象写
        httpConn.setDoOutput(true);
        // 设置是否从 HttpUrlConnection 的对象读入
        httpConn.setDoInput(true);

        // 此处默认 POST 方法发送的内容就是 JSON 形式的 body 参数，可以自行更改
        if(method == "POST" && params!=null && !params.isEmpty()){
            // 发送请求
            OutputStream out = new DataOutputStream(httpConn.getOutputStream());
            // getBytes() 作用为根据参数给定的编码方式，将一个字符串转化为一个字节数组
            out.write(JSON.toJSONString(params).getBytes("UTF-8"));
            out.flush();
        }
        else{
            //发送请求
            httpConn.connect();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

        // 循环读取流
        String line;
        StringBuffer buffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        httpConn.disconnect();
        String res = buffer.toString();

        // 输出响应内容
        outputResponseContent(res, "JSON");

        return res;
    }

    // 将输出函数单独拎出来
    public void outputResponseContent(String responseContent, String accept){
        // accept 是接收内容类型，如"JSON"、"XML"、"HTML"、"Text"等，此处自定义输出方法
        if(accept == "JSON"){
            JSONObject jsonObj = JSON.parseObject(responseContent);
            if(jsonObj != null) for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }
        }
        else{
            System.out.println(responseContent);
        }
    }
    // 发起请求
    public String requestData(Map params) throws Exception {
        String strURL = "";
        String method = "GET";
        final String key="";
        final String value="";
        // 添加自定义请求头，key 和 value 是你需要添加的头信息的键与值，如用于鉴权
        Map<String, String> headers = new HashMap<String, String>(){{
            put(key, value);
        }};
        return getResponseContent(strURL, method, headers, params);
//        return JSONUtils.readJSONFile("script/mock_data.json");
    }

    // 依次查询
    public Data selectData(String id){
        return dataMapper.selectData(id);
    }

    // 批量查询
    public List<Data> batchSelectData(String ids){
        return dataMapper.batchSelectData(Arrays.asList(ids.split(",")));
    }

    // 分页查询
    public List<Data> pageSelectData(String ids){
        List<Data> dataList = new ArrayList<>();
        List<String> idList = Arrays.asList(ids.split(","));
        int idSize = idList.size();

        int pageSize = 12000/1; // pageSize 给定页面大小，页面大小为 12000/参数种类
        int pageNum = (idSize % pageSize == 0 ? idSize / pageSize : idSize / pageSize + 1); //页数向上取整

        int start, end;
        for(int i = 1; i <= pageNum; i++){
            start = (i - 1) * pageSize;
            if(i == pageNum) end = idSize;
            else end = i * pageSize;
            dataList.addAll(dataMapper.batchSelectData(idList.subList(start, end)));
        }
        return dataList;
    }

    // 依次插入
    public void insertData() throws Exception {
        // 添加自定义参数，key 和 value 是你需要添加的参数名与参数值
        final String key="";
        final String value="";
        String jsonStr = requestData(new HashMap<String, String>() {{
            put(key, value);
        }});
        JSONObject jsonObj = JSON.parseObject(jsonStr);// 将 JSON 字符串解析成 JSON 对象
        if (jsonObj != null){
            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组
            if (jsonInfo != null) for (int i = 0; i < jsonInfo.size(); i++) {// 遍历 JSON 数组依次取出 JSON 对象
                JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);
                dataMapper.insertData(new Data(
                        jsonDetailInfo.getString("id"),
                        jsonDetailInfo.getString("key1"),
                        jsonDetailInfo.getString("key2")
                ));
            }
        }
        return ;
    }

    // 批量插入
    public void batchInsertData() throws Exception {
        // 添加自定义参数，key 和 value 是你需要添加的参数名与参数值
        final String key="";
        final String value="";
        String jsonStr = requestData(new HashMap<String, String>() {{
            put(key, value);
        }});
        List<Data> dataList = new ArrayList<>();
        JSONObject jsonObj = JSON.parseObject(jsonStr);// 将 JSON 字符串解析成 JSON 对象
        if (jsonObj != null){
            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组
            if (jsonInfo != null) for (int i = 0; i < jsonInfo.size(); i++) {// 遍历 JSON 数组依次取出 JSON 对象
                JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);
                dataList.add(new Data(
                        jsonDetailInfo.getString("id"),
                        jsonDetailInfo.getString("key1"),
                        jsonDetailInfo.getString("key2")
                ));           }
        }
        dataMapper.batchInsertData(dataList);
        return ;
    }

    // 分页插入
    public void pageInsertData() throws Exception {
        // 添加自定义参数，key 和 value 是你需要添加的参数名与参数值
        final String key="";
        final String value="";
        String jsonStr = requestData(new HashMap<String, String>() {{
            put(key, value);
        }});
        int pageSize = 12000/Data.class.getDeclaredFields().length; // 页面大小，此处意为每次分页写入的数据量定在 12000
        int curSize = 0; // 当前大小，当达到页面大小后就重置为 0
        int cumSize = 0; // 累积大小，一直累积，不进行重置
        List<Data> dataList = new ArrayList<>();
        JSONObject jsonObj = JSON.parseObject(jsonStr);// 将 JSON 字符串解析成 JSON 对象
        if (jsonObj != null){
            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组
            if (jsonInfo != null) for (int i = 0; i < jsonInfo.size(); i++) {// 遍历 JSON 数组依次取出 JSON 对象
                JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);
                dataList.add(new Data(
                        jsonDetailInfo.getString("id"),
                        jsonDetailInfo.getString("key1"),
                        jsonDetailInfo.getString("key2")
                ));
                curSize ++;
                cumSize ++;
                if(curSize == pageSize){
                    dataMapper.batchInsertData(dataList);
                    dataList.clear();
                    curSize = 0;
                }
            }
        }
        if(cumSize > 0) dataMapper.batchInsertData(dataList);
        return ;
    }

    // 依次更新
    public void updateData() throws Exception {
        // 添加自定义参数，key 和 value 是你需要添加的参数名与参数值
        final String key="";
        final String value="";
        String jsonStr = requestData(new HashMap<String, String>() {{
            put(key, value);
        }});
        JSONObject jsonObj = JSON.parseObject(jsonStr);// 将 JSON 字符串解析成 JSON 对象
        if (jsonObj != null){
            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组
            if (jsonInfo != null) for (int i = 0; i < jsonInfo.size(); i++) {// 遍历 JSON 数组依次取出 JSON 对象
                JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);
                dataMapper.updateData(new Data(
                        jsonDetailInfo.getString("id"),
                        jsonDetailInfo.getString("key1"),
                        jsonDetailInfo.getString("key2")
                ));
            }
        }
        return ;
    }

    // 批量更新
    public void batchUpdateData() throws Exception {
        // 添加自定义参数，key 和 value 是你需要添加的参数名与参数值
        final String key="";
        final String value="";
        String jsonStr = requestData(new HashMap<String, String>() {{
            put(key, value);
        }});
        List<Data> dataList = new ArrayList<>();
        JSONObject jsonObj = JSON.parseObject(jsonStr);// 将 JSON 字符串解析成 JSON 对象
        if (jsonObj != null){
            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组
            if (jsonInfo != null) for (int i = 0; i < jsonInfo.size(); i++) {// 遍历 JSON 数组依次取出 JSON 对象
                JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);
                dataList.add(new Data(
                        jsonDetailInfo.getString("id"),
                        jsonDetailInfo.getString("key1"),
                        jsonDetailInfo.getString("key2")
                ));           }
        }
        dataMapper.batchUpdateData(dataList);
        return ;
    }

    // 分页更新
    public void pageUpdateData() throws Exception {
        // 添加自定义参数，key 和 value 是你需要添加的参数名与参数值
        final String key="";
        final String value="";
        String jsonStr = requestData(new HashMap<String, String>() {{
            put(key, value);
        }});
        int pageSize = 12000/Data.class.getDeclaredFields().length; // 页面大小，此处意为每次分页写入的数据量定在 12000
        int curSize = 0; // 当前大小，当达到页面大小后就重置为 0
        int cumSize = 0; // 累积大小，一直累积，不进行重置
        List<Data> dataList = new ArrayList<>();
        JSONObject jsonObj = JSON.parseObject(jsonStr);// 将 JSON 字符串解析成 JSON 对象
        if (jsonObj != null){
            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组
            if (jsonInfo != null) for (int i = 0; i < jsonInfo.size(); i++) {// 遍历 JSON 数组依次取出 JSON 对象
                JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);
                dataList.add(new Data(
                        jsonDetailInfo.getString("id"),
                        jsonDetailInfo.getString("key1"),
                        jsonDetailInfo.getString("key2")
                ));
                curSize ++;
                cumSize ++;
                if(curSize == pageSize){
                    dataMapper.batchUpdateData(dataList);
                    dataList.clear();
                    curSize = 0;
                }
            }
        }
        if(cumSize > 0) dataMapper.batchUpdateData(dataList);
        return ;
    }

    // 依次插入或更新
    public void insertOrUpdateData() throws Exception {
        // 添加自定义参数，key 和 value 是你需要添加的参数名与参数值
        final String key="";
        final String value="";
        String jsonStr = requestData(new HashMap<String, String>() {{
            put(key, value);
        }});
        JSONObject jsonObj = JSON.parseObject(jsonStr);// 将 JSON 字符串解析成 JSON 对象
        if (jsonObj != null){
            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组
            if (jsonInfo != null) for (int i = 0; i < jsonInfo.size(); i++) {// 遍历 JSON 数组依次取出 JSON 对象
                JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);
                dataMapper.insertOrUpdateData(new Data(
                        jsonDetailInfo.getString("id"),
                        jsonDetailInfo.getString("key1"),
                        jsonDetailInfo.getString("key2")
                ));
            }
        }
        return ;
    }

    // 批量插入或更新
    public void batchInsertOrUpdateData() throws Exception {
        // 添加自定义参数，key 和 value 是你需要添加的参数名与参数值
        final String key="";
        final String value="";
        String jsonStr = requestData(new HashMap<String, String>() {{
            put(key, value);
        }});
        List<Data> dataList = new ArrayList<>();
        JSONObject jsonObj = JSON.parseObject(jsonStr);// 将 JSON 字符串解析成 JSON 对象
        if (jsonObj != null){
            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组
            if (jsonInfo != null) for (int i = 0; i < jsonInfo.size(); i++) {// 遍历 JSON 数组依次取出 JSON 对象
                JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);
                dataList.add(new Data(
                        jsonDetailInfo.getString("id"),
                        jsonDetailInfo.getString("key1"),
                        jsonDetailInfo.getString("key2")
                ));           }
        }
        dataMapper.batchInsertOrUpdateData(dataList);
        return ;
    }

    // 分页插入或更新
    public void pageInsertOrUpdateData() throws Exception {
        // 添加自定义参数，key 和 value 是你需要添加的参数名与参数值
        final String key="";
        final String value="";
        String jsonStr = requestData(new HashMap<String, String>() {{
            put(key, value);
        }});
        int pageSize = 12000/Data.class.getDeclaredFields().length; // 页面大小，此处意为每次分页写入的数据量定在 12000
        int curSize = 0; // 当前大小，当达到页面大小后就重置为 0
        int cumSize = 0; // 累积大小，一直累积，不进行重置
        List<Data> dataList = new ArrayList<>();
        JSONObject jsonObj = JSON.parseObject(jsonStr);// 将 JSON 字符串解析成 JSON 对象
        if (jsonObj != null){
            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组
            if (jsonInfo != null) for (int i = 0; i < jsonInfo.size(); i++) {// 遍历 JSON 数组依次取出 JSON 对象
                JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);
                dataList.add(new Data(
                        jsonDetailInfo.getString("id"),
                        jsonDetailInfo.getString("key1"),
                        jsonDetailInfo.getString("key2")
                ));
                curSize ++;
                cumSize ++;
                if(curSize == pageSize){
                    dataMapper.batchInsertOrUpdateData(dataList);
                    dataList.clear();
                    curSize = 0;
                }
            }
        }
        if(cumSize > 0) dataMapper.batchInsertOrUpdateData(dataList);
        return ;
    }

    // 依次删除
    public void deleteData(String id){
        dataMapper.deleteData(id);
        return ;
    }

    // 批量删除
    public void batchDeleteData(String ids){
        dataMapper.batchDeleteData(Arrays.asList(ids.split(",")));
        return ;
    }

    // 依次删除
    public void pageDeleteData(String ids){
        List<String> idList = Arrays.asList(ids.split(","));
        int idSize = idList.size();

        int pageSize = 12000/1; // pageSize 给定页面大小，页面大小为 12000/参数种类
        int pageNum = (idSize % pageSize == 0 ? idSize / pageSize : idSize / pageSize + 1); //页数向上取整

        int start, end;
        for(int i = 1; i <= pageNum; i++){
            start = (i - 1) * pageSize;
            if(i == pageNum) end = idSize;
            else end = i * pageSize;
            dataMapper.batchDeleteData(idList.subList(start, end));
        }
        return ;
    }

    // 清空
    public void clearData(){
        dataMapper.clearData();
        return ;
    }

    // 定期执行，首次发起请求后每隔一段固定的时间就执行一次
    public void performTaskPeriodically(long secondPeriod) throws InterruptedException {
        System.out.println("已安排定期任务，每"+ secondPeriod + "秒更新所有表");
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    // 将要定期执行的任务（函数调用）写在此处

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // 第一次任务延迟时间
        long delay = 2000;
        // 任务执行频率(secondPeriod 单位为秒，period 单位为毫秒)
        long period = secondPeriod * 1000;
        // 开始调度
        timer.schedule(timerTask, delay, period);
        // 指定首次运行时间
        // timer.schedule(timerTask, DateUtils.addSeconds(new Date(), 5), period);

        // 在某段时间内让任务按频率执行，此后结束进程，Long.MAX_VALUE 可以近似理解为只要不受外力结束进程，就永久按频率执行
        Thread.sleep(Long.MAX_VALUE);

        // 终止并移除任务
        timer.cancel();
        timer.purge();
    }

    // 定时执行，每天在指定的时间点执行
    public void performTaskOnSchedule(long secondPeriod) throws InterruptedException {

        // 设置首次执行时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // 当天的 23:00:00 首次执行，
        calendar.set(year, month, day, 23, 00, 00);
        Date date = calendar.getTime();
        Timer timer = new Timer();

        // 任务执行频率
        long period = secondPeriod * 1000;

        System.out.println("已安排定时任务，计划在"+ date + "更新所有表");

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    // 将要定时执行的任务（函数调用）写在此处

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // 指定首次运行时间，并每隔 period 再次执行（默认每隔一天）
        timer.schedule(timerTask, DateUtils.addSeconds(date, 5), period);

        // 在某段时间内让任务按频率执行，此后结束进程，Long.MAX_VALUE 可以近似理解为只要不受外力结束进程，就永久按频率执行
        Thread.sleep(Long.MAX_VALUE);

        // 终止并移除任务
        timer.cancel();
        timer.purge();
    }
}
