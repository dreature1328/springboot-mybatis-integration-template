package com.springboot.data.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.net.ssl.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class HTTPUtils {

    // 请求头示例
    public static Map<String, String> headers = new HashMap<String, String>() {{
        // 设置接收内容类型
        put("Accept", "application/json");
        // 设置发送内容类型
        put("Content-Type", "application/json;charset=UTF-8");
        // 设置字符集
        put("charset", "UTF-8");
        // 设置访问者系统引擎版本、浏览器信息的字段信息，此处伪装成用户通过浏览器访问
        put("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
    }};

    // 禁用 SSL 验证
    public static void disableSslVerification() {
        try {
            // 创建不验证证书链的 TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // 安装 TrustManager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // 创建验证所有主机名的 HostnameVerifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // 安装 HostnameVerifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    // 将请求头键值对添加到 HTTP 请求中
    public static void addHeadersToRequest(HttpURLConnection httpConn, Map headers){
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

    // 将请求参数拼接进 URL
    public static String concatParamsToURL(String staticURL, String paramsStr) throws Exception {
        return staticURL + paramsStr;
    }
    public static String concatParamsToURL(String staticURL, Map params) throws Exception {
        // staticURL 是字符串形式的静态 URL
        // params 是 Map<Object, Object> 或 Map<Object, Object[]> 类型的请求参数，键与值分别是参数名与参数值，URL 有重复同名参数时将多个值放进数组

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

    // 发起 HTTP 请求并获取响应内容
    // 重载 getResponseContent()，相当于参数有默认值
    public static String getResponseContent(String strURL) throws Exception {
        return getResponseContent(strURL, "GET", null, null);
    }
    public static String getResponseContent(String strURL, String method) throws Exception {
        return getResponseContent(strURL, method, null, null);
    }
    public static String getResponseContent(String strURL, String method, Map headers) throws Exception {
        return getResponseContent(strURL, method, headers, null);
    }
    public static String getResponseContent(String strURL, String method, Map headers, Map params) throws Exception {
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
        // outputResponseContent(res, "JSON");

        return res;
    }

    // 异步 HTTP 请求
    public static CompletableFuture<String> asyncHttpRequest(String strURL, String method, Map<String, String> headers, Map<String, String> params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getResponseContent(strURL, method, headers, params);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // 打印响应内容
    public static void printResponseContent(String responseContent, String accept) throws ParserConfigurationException, IOException, SAXException {
        // accept 是接收内容类型，如"JSON"、"XML"、"HTML"、"Text"等，此处自定义输出方法
        switch (accept) {
            case "JSON":
                // 解析 JSON 字符串为 JSON 对象
                JSONObject jsonObj = JSON.parseObject(responseContent);
                printJSON(jsonObj,0);
                break;
            case "XML":
            case "HTML":
                // 创建 DocumentBuilderFactory 对象
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                // 创建 DocumentBuilder 对象
                DocumentBuilder builder = factory.newDocumentBuilder();
                // 将 XML 解析成 Document 对象
                Document doc = builder.parse(new InputSource(new StringReader(responseContent)));
                printXML(doc);
                break;
            // 可以根据需要添加其他类型的解析和输出
            case "TEXT":
            default:
                System.out.println(responseContent);
                break;
        }
    }

    // 打印 JSON
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


    // 打印 XML
    public static void printXML(Document doc) {
        Element root = doc.getDocumentElement();
        printXML(System.out, root, 0);
    }

    // 打印 XML
    private static void printXML(PrintStream ps, Element element, int indent) {
        printTrunk(ps, indent);
        ps.printf("├─ 元素: %s\n", element.getNodeName());

        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attribute = (Attr) attributes.item(i);
            printTrunk(ps, indent + 1);
            ps.printf("├─ 属性: %s = %s\n", attribute.getName(), attribute.getValue());
        }

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                printXML(ps, (Element) child, indent + 1);
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getNodeValue().trim();
                if (!text.isEmpty()) {
                    printTrunk(ps, indent + 1);
                    ps.printf("└─ 文本: %s\n", text);
                }
            }
        }
    }

    // 打印 XML
    private static void printTrunk(PrintStream ps, int indent) {
        for (int i = 0; i < indent; i++) {
            ps.print("|   ");
        }
    }
}