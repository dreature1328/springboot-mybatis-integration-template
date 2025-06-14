package dreature.smit.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.w3c.dom.Document;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class HttpUtils {

    // 默认请求头
    public static Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
                // 设置接收内容类型
        headers.put("Accept", "application/json");
        // 设置发送内容类型
        headers.put("Content-Type", "application/json;charset=UTF-8");
        // 设置字符集
        headers.put("charset", "UTF-8");
        // 设置访问者系统引擎版本、浏览器信息的字段信息，此处伪装成用户通过浏览器访问
        headers.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        return headers;
    }

    // 禁用 SSL 验证
    public static void disableSslVerification() {
        try {
            // 创建不验证证书链的 TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
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

    // 添加请求头到连接
    public static void addHeadersToRequest(HttpsURLConnection connection, Map<String, ?> headers) {
        if (headers == null) return;

        for (Map.Entry<String, ?> entry : headers.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Iterable) {
                for (Object item : (Iterable<?>) value) {
                    connection.addRequestProperty(key, item.toString());
                }
            } else if (value.getClass().isArray()) {
                for (Object item : (Object[]) value) {
                    connection.addRequestProperty(key, item.toString());
                }
            } else {
                connection.setRequestProperty(key, value.toString());
            }
        }
    }

    // 构建带参数的 URL
    public static String buildUrlWithParams(String baseUrl, String params){
        return baseUrl + params;
    }

    public static String buildUrlWithParams(String baseUrl, Map<String, ?> params) throws IOException {
        // baseUrl 是基础的静态 URL
        // params 键与值分别是参数名与参数值，URL 有重复同名参数时将多个值放进数组

        if (params == null || params.isEmpty()) {
            return baseUrl;
        }

        StringBuilder urlBuilder = new StringBuilder(baseUrl);

        // 判断 Url 中是否已经包含参数
        boolean isFirstParam = !baseUrl.contains("?");

        // 遍历参数
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            String key = entry.getKey(); // 参数名
            Object value = entry.getValue(); // 参数值

            if (value instanceof Iterable) {
                for (Object item : (Iterable<?>) value) {
                    appendParam(urlBuilder, key, item, isFirstParam);
                    isFirstParam = false;
                }
            } else if (value.getClass().isArray()) {
                for (Object item : (Object[]) value) {
                    appendParam(urlBuilder, key, item, isFirstParam);
                    isFirstParam = false;
                }
            } else {
                appendParam(urlBuilder, key, value, isFirstParam);
                isFirstParam = false;
            }
        }

        return urlBuilder.toString();
    }

    // 拼接参数
    private static void appendParam(StringBuilder builder, String key, Object value, boolean isFirstParam)
            throws UnsupportedEncodingException {
        builder.append(isFirstParam ? "?" : "&")
                .append(key)
                .append("=")
                .append(URLEncoder.encode(value.toString(), "UTF-8"));
    }

    // 发起 HTTP 请求并获取响应内容
    // 重载 sendHttpRequest()，相当于参数有默认值
    public static String sendHttpRequest(String strUrl) throws Exception {
        return sendHttpRequest(strUrl, "GET", null, null);
    }
    public static String sendHttpRequest(String strUrl, String method) throws Exception {
        return sendHttpRequest(strUrl, method, null, null);
    }
    public static String sendHttpRequest(String strUrl, String method, Map<String, ?> headers) throws Exception {
        return sendHttpRequest(strUrl, method, headers, null);
    }
    public static String sendHttpRequest(String url, String method, Map<String, ?> headers, Map<String, ?> params) throws Exception {
        // strUrl 是 String 类型的 Url
        // method 是 String 类型的请求方法，为 "GET" 或 "POST"
        // headers 键与值分别是请求头名与请求头值，有重复同名请求头时将多个值放进数组
        // params 键与值分别是参数名与参数值，Url 有重复同名参数时将多个值放进数组

        // 忽略验证 https 中 SSL 证书
        disableSslVerification();

        // GET 方法下，query 参数拼接在 Url 字符串末尾
        if(method.equals("GET") && params != null) {
            url = buildUrlWithParams(url, params);
        }

        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);



        // 请求时是否使用缓存
        connection.setUseCaches(false);

        // POST 方法请求必须设置下面两项
        // 设置是否从 HttpUrlConnection 的对象写
        connection.setDoOutput(true);
        // 设置是否从 HttpUrlConnection 的对象读入
        connection.setDoInput(true);

        // 设置请求头
        addHeadersToRequest(connection, headers != null ? headers : getDefaultHeaders());

        // 此处默认 POST 方法发送的内容就是 JSON 形式的 body 参数，可以自行更改
        if(method.equalsIgnoreCase("POST") && params != null) {
            // 发送请求
            OutputStream out = new DataOutputStream(connection.getOutputStream());
            // getBytes() 作用为根据参数给定的编码方式，将一个字符串转化为一个字节数组
            out.write(JsonUtils.DEFAULT_MAPPER.writeValueAsString(params).getBytes("UTF-8"));
            out.flush();
        }
        else{
            //发送请求
            connection.connect();
        }


        String result = readResponseBody(connection);

//        // 输出响应内容
//         String contentType = connect.getContentType();
//         printResponseContent(result, contentType);

        return result;
    }

    // 异步 HTTP 请求
    public static CompletableFuture<String> sendAsyncHttpRequest(String url, String method, Map<String, ?> headers, Map<String, ?> params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendHttpRequest(url, method, headers, params);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    // 读取响应体
    private static String readResponseBody(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuffer buffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        return buffer.toString();
    }

    // 输出响应内容
    public static void printResponseContent(String content, String contentType) throws Exception {
        // contentType 是接收内容类型
        // 响应内容为 JSON 格式
        if (contentType.contains("application/json")) {
            // 解析 JSON 字符串为 JSON 对象
            JsonNode jsonNode = JsonUtils.DEFAULT_MAPPER.readTree(content);
            JsonUtils.printJson(jsonNode,0);
        }
        // 响应内容为 XML 格式
        else if (contentType.contains("application/xml")) {
            // 将 XML 字符串解析成 Document 对象
            Document doc = XmlUtils.stringToDocument(content);
            XmlUtils.printXml(doc);
        }
        // 响应内容为 HTML 格式、纯文本格式
        else if (contentType.contains("text/html") || contentType.contains("text/plain")) {
            System.out.println(content);
        }
        // 响应内容为其他格式
        else {
            System.out.println(content);
        }
    }
}
