package xyz.dreature.smit.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public class HttpUtils {
    // ===== 常量 / 配置 =====
    public static final int BUFFER_SIZE = 4096;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    // 创建默认请求头
    public static Map<String, String> createDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        // 设置接收内容类型
        headers.put("Accept", "application/json");
        // 设置发送内容类型
        headers.put("Content-Type", "application/json;charset=" + DEFAULT_CHARSET.name());
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
            }};

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

    // ===== 执行流程 =====
    // 同步执行流程（发起 HTTP 请求 + 解析响应内容）
    public static <T> T execute(String url, String method, Map<String, ?> headers, Map<String, ?> requestParams, Class<T> targetType) throws IOException {
        // url 是 String 类型的 Url
        // method 是 String 类型的请求方法，为 "GET" 或 "POST"
        // headers 键与值分别是请求头名与请求头值，有重复同名请求头时将多个值放进数组
        // requestParams 键与值分别是参数名与参数值，Url 有重复同名参数时将多个值放进数组

        HttpURLConnection connection = null;
        try {
            // 忽略验证 HTTPS 中 SSL 证书
            disableSslVerification();

            // 请求方法统一大写
            method = method.toUpperCase();

            // GET 方法下，参数拼接在 URL 末尾
            if ("GET".equals(method)) {
                url = buildUrlWithQueryParams(url, requestParams);
            }

            connection = (HttpURLConnection) new URL(url).openConnection();

            // 设置请求方法
            connection.setRequestMethod(method);

            // 设置请求头
            applyHeaders(connection, headers);

            // 请求时是否使用缓存
            connection.setUseCaches(false);

            // 此处默认 POST、PUT、PATCH 方法发送 JSON 形式参数的请求体，可自行更改
            if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
                connection.setDoOutput(true);
                writeRequestBody(connection, requestParams);
            } else {
                connection.setDoOutput(false);
                connection.connect();
            }

            return parseResponse(connection, targetType);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // 同步执行流程并返回字节数组（byte[]）
    public static byte[] executeAsBytes(String url, String method, Map<String, ?> headers, Map<String, ?> requestParams) throws IOException {
        return execute(url, method, headers, requestParams, byte[].class);
    }

    // 同步执行流程并返回字符串（String）
    public static String executeAsString(String url, String method, Map<String, ?> headers, Map<String, ?> requestParams) throws IOException {
        return execute(url, method, headers, requestParams, String.class);
    }

    // 同步执行流程并返回 JSON（JsonNode）
    public static JsonNode executeAsJson(String url, String method, Map<String, ?> headers, Map<String, ?> requestParams) throws IOException {
        return execute(url, method, headers, requestParams, JsonNode.class);
    }

    // 同步执行流程并返回 XML（Document）
    public static Document executeAsXml(String url, String method, Map<String, ?> headers, Map<String, ?> requestParams) throws IOException {
        return execute(url, method, headers, requestParams, Document.class);
    }

    // 异步执行流程（发起 HTTP 请求 + 解析响应内容 + 默认线程池）
    public static <T> CompletableFuture<T> executeAsync(String url, String method, Map<String, ?> headers, Map<String, ?> requestParams, Class<T> targetType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(url, method, headers, requestParams, targetType);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    // 异步执行流程（发起 HTTP 请求 + 解析响应内容 + 自定义线程池）
    public static <T> CompletableFuture<T> executeAsync(String url, String method, Map<String, ?> headers, Map<String, ?> requestParams, Class<T> targetType, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(url, method, headers, requestParams, targetType);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    // 异步执行流程并返回字节数组（byte[]）
    public static CompletableFuture<byte[]> executeAsyncAsBytes(String url, String method, Map<String, ?> headers, Map<String, ?> requestParams) throws IOException {
        return executeAsync(url, method, headers, requestParams, byte[].class);
    }

    // 异步执行流程并返回字符串（String）
    public static CompletableFuture<String> executeAsyncAsString(String url, String method, Map<String, ?> headers, Map<String, ?> requestParams) throws IOException {
        return executeAsync(url, method, headers, requestParams, String.class);
    }

    // 异步执行流程并返回 JSON（JsonNode）
    public static CompletableFuture<JsonNode> executeAsyncAsJson(String url, String method, Map<String, ?> headers, Map<String, ?> requestParams) throws IOException {
        return executeAsync(url, method, headers, requestParams, JsonNode.class);
    }

    // 异步执行流程并返回 XML（Document）
    public static CompletableFuture<Document> executeAsyncAsXml(String url, String method, Map<String, ?> headers, Map<String, ?> requestParams) throws IOException {
        return executeAsync(url, method, headers, requestParams, Document.class);
    }

    // ===== 构建请求 =====
    public static String buildUrlWithQueryParams(String baseUrl, Map<String, ?> queryParams) throws IOException {
        // baseUrl 是基础的静态 URL
        // requestParams 键与值分别是参数名与参数值，URL 有重复同名参数时将多个值放进数组

        if (queryParams == null || queryParams.isEmpty()) return baseUrl;

        StringBuilder urlBuilder = new StringBuilder(baseUrl);

        // 判断 URL 中是否已经包含参数
        boolean isFirstParam = !baseUrl.contains("?");

        // 遍历参数
        for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
            String key = entry.getKey(); // 参数名
            Object value = entry.getValue(); // 参数值

            if (value instanceof Iterable) {
                for (Object item : (Iterable<?>) value) {
                    appendQueryParam(urlBuilder, key, item, isFirstParam);
                    isFirstParam = false;
                }
            } else if (value.getClass().isArray()) {
                for (Object item : (Object[]) value) {
                    appendQueryParam(urlBuilder, key, item, isFirstParam);
                    isFirstParam = false;
                }
            } else {
                appendQueryParam(urlBuilder, key, value, isFirstParam);
                isFirstParam = false;
            }
        }

        return urlBuilder.toString();
    }

    // 拼接参数
    public static void appendQueryParam(StringBuilder builder, String key, Object value, boolean isFirstParam)
            throws UnsupportedEncodingException {
        builder.append(isFirstParam ? "?" : "&")
                .append(URLEncoder.encode(key, DEFAULT_CHARSET.name()))
                .append("=")
                .append(URLEncoder.encode(value != null ? value.toString() : "", DEFAULT_CHARSET.name()));
    }

    // 添加请求头
    public static void applyHeaders(HttpURLConnection connection, Map<String, ?> headers) {
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

    // 写入请求体（JSON 形式）
    public static void writeRequestBody(
            HttpURLConnection connection,
            Map<String, ?> bodyParams
    ) throws IOException {
        try (OutputStream out = connection.getOutputStream()) {
            if (bodyParams == null || bodyParams.isEmpty()) return;
            out.write(JsonUtils.DEFAULT_MAPPER.writeValueAsString(bodyParams).getBytes(DEFAULT_CHARSET));
        }
    }

    // ===== 响应处理 =====
    // 读取响应内容（字节数组）
    public static byte[] readResponseBytes(HttpURLConnection connection) throws IOException {
        try (InputStream input = connection.getInputStream();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }
    }

    // 解析响应为结构化类型
    public static <T> T parseResponse(HttpURLConnection connection, Class<T> targetType) throws IOException {
        byte[] responseBytes = readResponseBytes(connection);
        String contentType = connection.getContentType().toLowerCase();

        // 基于预定义类型解析
        if (targetType == byte[].class) {
            return targetType.cast(responseBytes);
        } else if (targetType == String.class) {
            return targetType.cast(parseString(responseBytes));
        } else if (JsonNode.class.isAssignableFrom(targetType)) {
            return targetType.cast(parseJson(responseBytes));
        } else if (Document.class.isAssignableFrom(targetType)) {
            return targetType.cast(parseXml(responseBytes));
        } else if (targetType == Object.class && contentType != null) {
            // 无明确预定义类型时，基于内容类型推测
            if (contentType.contains("json")) {
                return targetType.cast(parseJson(responseBytes));
            } else if (contentType.contains("xml")) {
                return targetType.cast(parseXml(responseBytes));
            } else if (contentType.contains("text")) {
                return targetType.cast(parseString(responseBytes));
            }
        }

        // 最终回退逻辑
        throw new IOException("不受支持的类型解析");
    }

    // 解析字节数组为字符串（String）（默认编码）
    public static String parseString(byte[] bytes) {
        return parseString(bytes, DEFAULT_CHARSET);
    }

    // 解析字节数组为字符串（String）（指定编码）
    public static String parseString(byte[] bytes, Charset charset) {
        return new String(bytes, charset);
    }

    // 解析字节数组为 JSON（JsonNode）
    public static JsonNode parseJson(byte[] bytes) throws IOException {
        try {
            return JsonUtils.DEFAULT_MAPPER.readTree(bytes);
        } catch (JsonProcessingException e) {
            throw new IOException("JSON 解析失败", e);
        }
    }

    // 解析字节数组为 XML（Document）
    public static Document parseXml(byte[] bytes) throws IOException {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
            return XmlUtils.documentBuilder.parse(stream);
        } catch (SAXException e) {
            throw new IOException("XML 解析失败", e);
        }
    }
}
