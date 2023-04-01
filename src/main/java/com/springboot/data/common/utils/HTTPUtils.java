package com.springboot.data.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.net.ssl.*;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

    // 将请求头键值对添加到 HTTP 请求中
    public static void addHeadersToRequest(HttpURLConnection httpConn, Map<String, ?> headers) {
        if (headers != null) {
            for (Map.Entry<String, ?> entry : headers.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value.getClass().isArray()) {
                    // 如果是数组类型，则遍历数组并添加请求头
                    for (Object v : (Object[]) value) {
                        httpConn.setRequestProperty(key, v.toString());
                    }
                } else {
                    // 如果不是数组类型，则直接添加请求头
                    httpConn.setRequestProperty(key, value.toString());
                }
            }
        }
    }


    // 将请求参数拼接进 URL
    public static String concatParamsToURL(String staticURL, String paramsStr){
        return staticURL + paramsStr;
    }

    public static String concatParamsToURL(String staticURL, Map<String, ?> params) throws Exception {
        // staticURL 是字符串形式的静态 URL
        // params 键与值分别是参数名与参数值，URL 有重复同名参数时将多个值放进数组

        // 判断参数是否为空
        if (params.isEmpty()) {
            return staticURL;
        }

        StringBuilder sb = new StringBuilder(staticURL);

        // 判断 URL 中是否已经包含参数
        boolean hasParams = staticURL.indexOf("?") != -1;

        // 遍历参数
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            String key = entry.getKey(); // 参数名
            Object value = entry.getValue(); // 参数值

            // 判断参数值是否为数组
            if (value.getClass().isArray()) {
                // 如果是数组，遍历数组并添加参数
                for (Object v : (Object[]) value) {
                    sb.append(hasParams ? "&" : "?")
                            .append(URLEncoder.encode(key, "utf-8"))
                            .append("=")
                            .append(URLEncoder.encode(v.toString(), "utf-8"));
                    hasParams = true;
                }
            } else {
                // 如果不是数组，直接添加参数
                sb.append(hasParams ? "&" : "?")
                        .append(URLEncoder.encode(key, "utf-8"))
                        .append("=")
                        .append(URLEncoder.encode(value.toString(), "utf-8"));
                hasParams = true;
            }
        }

        return sb.toString();
    }

    // 发起 HTTP 请求并获取响应内容
    // 重载 requestHTTPContent()，相当于参数有默认值
    public static String requestHTTPContent(String strURL) throws Exception {
        return requestHTTPContent(strURL, "GET", null, null);
    }
    public static String requestHTTPContent(String strURL, String method) throws Exception {
        return requestHTTPContent(strURL, method, null, null);
    }
    public static String requestHTTPContent(String strURL, String method, Map<String, ?> headers) throws Exception {
        return requestHTTPContent(strURL, method, headers, null);
    }
    public static String requestHTTPContent(String strURL, String method, Map<String, ?> headers, Map<String, ?> params) throws Exception {
        // strURL 是 String 类型的 URL
        // method 是 String 类型的请求方法，为 "GET" 或 "POST"
        // headers 键与值分别是请求头名与请求头值，有重复同名请求头时将多个值放进数组
        // params 键与值分别是参数名与参数值，URL 有重复同名参数时将多个值放进数组

        // 忽略验证 https 中 SSL 证书
        disableSslVerification();

        // GET 方法下，query 参数拼接在 URL 字符串末尾
        if(method.equals("GET") && params != null) {
            strURL = concatParamsToURL(strURL, params);
        }

        System.out.println(strURL);

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
        if(method.equals("POST") && params!=null) {
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

        String contentType = httpConn.getContentType();
        String result = readResponseContent(httpConn);

        // 输出响应内容
        // outputResponseContent(result, contentType);

        return result;
    }

    // 异步 HTTP 请求
    public static CompletableFuture<String> asyncHTTPRequest(String strURL, String method, Map<String, ?> headers, Map<String, ?> params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return requestHTTPContent(strURL, method, headers, params);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // 读取响应内容
    private static String readResponseContent(HttpURLConnection httpConn) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        String line;
        StringBuffer buffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        return buffer.toString();
    }

    // 输出响应内容
    public static void outputResponseContent(String responseContent, String contentType) throws Exception {
        // contentType 是接收内容类型
        // 响应内容为 JSON 格式
        if (contentType.contains("application/json")) {
            // 解析 JSON 字符串为 JSON 对象
            JSONObject jsonObj = JSON.parseObject(responseContent);
            printJSON(jsonObj,0);
        }
        // 响应内容为 XML 格式
        else if (contentType.contains("application/xml")) {
            // 创建 DocumentBuilderFactory 对象
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 创建 DocumentBuilder 对象
            DocumentBuilder builder = factory.newDocumentBuilder();
            // 将 XML 解析成 Document 对象
            Document doc = builder.parse(new InputSource(new StringReader(responseContent)));
            printXML(doc);
        }
        // 响应内容为图像
        else if (contentType.contains("image/jpeg") || contentType.contains("image/png")) {
            // 将响应内容解码为图片
            byte[] imageBytes = responseContent.getBytes();
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);
            viewImage(new ImageIcon(image));
        }
        // 响应内容为 PDF 文档
        else if (contentType.contains("application/pdf")) {
            System.out.println("[PDF document]");
        }
        // 响应内容为二进制数据流
        else if (contentType.contains("application/octet-stream")) {
            System.out.println("[Binary data]");
        }
        // 响应内容为 HTML 格式、纯文本格式
        else if (contentType.contains("text/html") || contentType.contains("text/plain")) {
            System.out.println(responseContent);
        }
        // 响应内容为其他格式
        else {
            System.out.println(responseContent);
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

    // 浏览图像
    private static void viewImage(ImageIcon imageIcon) {
        JLabel label = new JLabel(imageIcon);
        JFrame frame = new JFrame("图片显示");
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
