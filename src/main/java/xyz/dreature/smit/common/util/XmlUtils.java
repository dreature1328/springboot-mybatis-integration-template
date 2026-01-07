package xyz.dreature.smit.common.util;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XmlUtils {
    public static final DocumentBuilderFactory documentBuilderFactory;
    public static final TransformerFactory transformerFactory;
    public static final DocumentBuilder documentBuilder;

    // 日期格式化器
    public static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    static {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setValidating(false);
        transformerFactory = TransformerFactory.newInstance();
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("无法创建 DOM 解析器", e);
        }
    }

    // ===== 文档获取 =====
    // 解析 XML 文件
    public static Document parseFile(String filename) {
        File file = new File(filename);
        return parseFile(file);
    }

    // 解析 XML 文件
    public static Document parseFile(File file) {
        try (InputStream is = new FileInputStream(file)) {
            Document document = documentBuilder.parse(is);
            document.normalize();
            return document;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException | SAXException e) {
            throw new RuntimeException("XML 解析失败", e);
        }
    }

    // 解析 XML 字符串
    public static Document parseString(String xml) {
        try {
            Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));
            document.normalize();
            return document;
        } catch (IOException | SAXException e) {
            throw new RuntimeException("XML 解析失败", e);
        }
    }

    // ===== 节点获取 =====
    // 获取指定节点的所有子节点
    public static List<Node> getChildNodes(Node node) {
        List<Node> childNodes = new ArrayList<>();
        // 获取所有子节点
        NodeList nodeList = node.getChildNodes();
        // 遍历所有子节点
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            childNodes.add(childNode);
        }
        return childNodes;
    }

    // 获取指定元素的所有子元素
    public static List<Element> getChildElements(Element element) {
        List<Element> childElements = new ArrayList<>();
        // 获取所有子节点
        NodeList nodeList = element.getChildNodes();
        // 遍历所有子节点
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            // 判断节点类型是否为元素节点
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                // 将元素节点添加到列表中
                childElements.add((Element) childNode);
            }
        }
        return childElements;
    }

    // 获取指定元素指定名称的子元素
    public static List<Element> getChildElements(Element element, String tagName) {
        List<Element> children = new ArrayList<>();
        NodeList nodeList = element.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if ("*".equals(tagName) || node.getNodeName().equals(tagName)) {
                    children.add((Element) node);
                }
            }
        }
        return children;
    }

    // 获取指定元素指定名称的首个子元素
    public static Element getFirstChildElement(Element element, String tagName) {
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE &&
                    node.getNodeName().equals(tagName)) {
                return (Element) node;
            }
        }
        return null;
    }

    // 检查标签是否存在
    public static boolean hasTag(Element element, String tagName) {
        return getFirstChildElement(element, tagName) != null;
    }

    // 获取指定元素的所有属性
    public static List<Attr> getAttributes(Element element) {
        List<Attr> attributes = new ArrayList<>();
        NamedNodeMap attributeMap = element.getAttributes();

        for (int i = 0; i < attributeMap.getLength(); i++) {
            Node attributeNode = attributeMap.item(i);
            // 判断节点类型是否为属性节点
            if (attributeNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                attributes.add((Attr) attributeNode);
            }
        }
        return attributes;
    }

    // 获取指定元素的所有属性
    public static Map<String, String> getAttributesAsMap(Element element) {
        Map<String, String> attributes = new LinkedHashMap<>();
        NamedNodeMap attributeMap = element.getAttributes();

        for (int i = 0; i < attributeMap.getLength(); i++) {
            Attr attr = (Attr) attributeMap.item(i);
            attributes.put(attr.getName(), attr.getValue());
        }

        return attributes;
    }

    // ===== 节点转换 =====
    // 将节点转换为元素
    public static Element nodeToElement(Node node) {
        try {
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                throw new IllegalArgumentException("该节点不是元素节点");
            }
            return (Element) node;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将节点转换为属性
    public static Attr nodeToAttr(Node node) {
        try {
            if (node.getNodeType() != Node.ATTRIBUTE_NODE) {
                throw new IllegalArgumentException("该节点不是属性节点");
            }
            return (Attr) node;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将节点转化为文本
    public static Text nodeToText(Node node) {
        try {
            if (node.getNodeType() != Node.TEXT_NODE) {
                throw new IllegalArgumentException("该节点不是文本节点");
            }
            return (Text) node;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===== 值获取 =====
    // 获取指定元素的文本
    private static String getText(Element element, String tagName) {
        if (element == null) {
            return null;
        }
        Element child = getFirstChildElement(element, tagName);
        return child != null ? child.getTextContent() : null;
    }

    // 获取字符串（无默认值）
    public static String getString(Element element, String tagName) {
        String text = getText(element, tagName);
        return text != null ? text.trim() : null;
    }

    // 获取字符串（有默认值）
    public static String getString(Element element, String tagName, String defaultValue) {
        String text = getText(element, tagName);
        return text != null ? text.trim() : defaultValue;
    }

    // 获取布尔值（无默认值）
    public static boolean getBool(Element element, String tagName) {
        String text = getText(element, tagName);
        if (text == null || text.trim().isEmpty()) {
            throw new RuntimeException("标签 " + tagName + " 不存在或为空");
        }

        String trimmed = text.trim().toLowerCase();
        if ("true".equals(trimmed) || "1".equals(trimmed) || "yes".equals(trimmed)) {
            return true;
        } else if ("false".equals(trimmed) || "0".equals(trimmed) || "no".equals(trimmed)) {
            return false;
        }

        throw new RuntimeException("标签 " + tagName + " 无法解析为布尔值: " + text);
    }

    // 获取布尔值（有默认值）
    public static boolean getBool(Element element, String tagName, boolean defaultValue) {
        String text = getText(element, tagName);
        if (text == null || text.trim().isEmpty()) {
            return defaultValue;
        }

        String trimmed = text.trim().toLowerCase();
        if ("true".equals(trimmed) || "1".equals(trimmed) || "yes".equals(trimmed)) {
            return true;
        } else if ("false".equals(trimmed) || "0".equals(trimmed) || "no".equals(trimmed)) {
            return false;
        }

        return defaultValue;
    }

    // 获取整型（无默认值）
    public static int getInt(Element element, String tagName) {
        String text = getText(element, tagName);
        if (text == null || text.trim().isEmpty()) {
            throw new RuntimeException("标签 " + tagName + " 不存在或为空");
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("标签 " + tagName + " 无法解析为整数: " + text, e);
        }
    }

    // 获取整型（有默认值）
    public static int getInt(Element element, String tagName, int defaultValue) {
        String text = getText(element, tagName);
        if (text == null || text.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // 获取长整型（无默认值）
    public static long getLong(Element element, String tagName) {
        String text = getText(element, tagName);
        if (text == null || text.trim().isEmpty()) {
            throw new RuntimeException("标签 " + tagName + " 不存在或为空");
        }
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("标签 " + tagName + " 无法解析为长整数: " + text, e);
        }
    }

    // 获取长整型（有默认值）
    public static long getLong(Element element, String tagName, long defaultValue) {
        String text = getText(element, tagName);
        if (text == null || text.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // 获取双精度浮点数（无默认值）
    public static double getDouble(Element element, String tagName) {
        String text = getText(element, tagName);
        if (text == null || text.trim().isEmpty()) {
            throw new RuntimeException("标签 " + tagName + " 不存在或为空");
        }
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("标签 " + tagName + " 无法解析为双精度浮点数: " + text, e);
        }
    }

    // 获取双精度浮点数（有默认值）
    public static double getDouble(Element element, String tagName, double defaultValue) {
        String text = getText(element, tagName);
        if (text == null || text.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // 获取日期（无默认值）
    public static LocalDate getDate(Element element, String tagName) {
        String text = getText(element, tagName);
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(text.trim(), ISO_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // 获取日期（有默认值）
    public static LocalDate getDate(Element element, String tagName, LocalDate defaultValue) {
        LocalDate result = getDate(element, tagName);
        return result != null ? result : defaultValue;
    }

    // 获取日期时间（无默认值）
    public static LocalDateTime getDateTime(Element element, String tagName) {
        String text = getText(element, tagName);
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(text.trim(), ISO_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // 获取日期时间（有默认值）
    public static LocalDateTime getDateTime(Element element, String tagName, LocalDateTime defaultValue) {
        LocalDateTime result = getDateTime(element, tagName);
        return result != null ? result : defaultValue;
    }

    // 获取 Map（无默认值）
    public static Map<String, Object> getMap(Element element, String tagName) {
        Element child = getFirstChildElement(element, tagName);
        if (child == null) {
            return null;
        }
        return parseMap(child);
    }

    // 获取 Map（有默认值）
    public static Map<String, Object> getMap(Element element, String tagName, Map<String, Object> defaultValue) {
        Map<String, Object> result = getMap(element, tagName);
        return result != null ? result : defaultValue;
    }

    // 获取字符串数组（无默认值）
    public static String[] getStringArray(Element element, String tagName, String itemTagName) {
        Element child = getFirstChildElement(element, tagName);
        if (child == null) {
            return null;
        }
        return parseStringArray(child, itemTagName);
    }

    // 获取字符串数组（有默认值）
    public static String[] getStringArray(Element element, String tagName, String itemTagName, String[] defaultValue) {
        String[] result = getStringArray(element, tagName, itemTagName);
        return result != null ? result : defaultValue;
    }

    // 获取整型数组（无默认值）
    public static int[] getIntArray(Element element, String tagName, String itemTagName) {
        Element child = getFirstChildElement(element, tagName);
        if (child == null) {
            return null;
        }
        return parseIntArray(child, itemTagName);
    }

    // 获取整型数组（有默认值）
    public static int[] getIntArray(Element element, String tagName, String itemTagName, int[] defaultValue) {
        int[] result = getIntArray(element, tagName, itemTagName);
        return result != null ? result : defaultValue;
    }

    // 解析键值对
    private static Map<String, Object> parseMap(Element element) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 按元素名分组
        Map<String, List<Element>> elementsByName = getChildElements(element, "*").stream()
                .collect(Collectors.groupingBy(Element::getNodeName));

        // 处理每个元素
        elementsByName.forEach((name, elements) -> {
            if (elements.size() == 1) {
                result.put(name, parseElementValue(elements.get(0)));
            } else {
                List<Object> list = elements.stream()
                        .map(XmlUtils::parseElementValue)
                        .collect(Collectors.toList());
                result.put(name, list);
            }
        });

        return result;
    }

    // 解析元素值（递归）
    private static Object parseElementValue(Element element) {
        // 检查是否有子元素
        List<Element> children = getChildElements(element, "*");

        if (!children.isEmpty()) {
            // 有子元素，递归解析为 Map
            return parseMap(element);
        } else {
            // 没有子元素，解析文本内容
            return parseTextValue(element.getTextContent());
        }
    }

    // 智能解析文本值
    private static Object parseTextValue(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String trimmed = text.trim();

        // 尝试布尔值
        if ("true".equalsIgnoreCase(trimmed) || "false".equalsIgnoreCase(trimmed)) {
            return Boolean.parseBoolean(trimmed.toLowerCase());
        }

        // 尝试整数
        try {
            if (trimmed.matches("-?\\d+")) {
                return Integer.parseInt(trimmed);
            }
        } catch (NumberFormatException ignored) {
        }

        // 尝试长整数
        try {
            if (trimmed.matches("-?\\d+")) {
                return Long.parseLong(trimmed);
            }
        } catch (NumberFormatException ignored) {
        }

        // 尝试浮点数
        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException ignored) {
        }

        // 返回字符串
        return trimmed;
    }

    // 解析字符串数组
    private static String[] parseStringArray(Element parent, String itemTagName) {
        List<String> list = getChildElements(parent, itemTagName).stream()
                .map(Element::getTextContent)
                .filter(text -> text != null && !text.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toList());
        return list.toArray(new String[0]);
    }

    // 解析整数数组
    private static int[] parseIntArray(Element parent, String itemTagName) {
        List<Integer> list = new ArrayList<>();
        for (Element child : getChildElements(parent, itemTagName)) {
            try {
                String text = child.getTextContent();
                if (text != null && !text.trim().isEmpty()) {
                    list.add(Integer.parseInt(text.trim()));
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    // ===== 输出 =====
    // 转换文档为字符串
    public static String toString(Document document) {
        String xmlStr = "";
        try {
            // 创建一个新的转换器对象
            Transformer transformer = transformerFactory.newTransformer();
            // 设置转换器属性
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            // 创建一个字符输出流
            StringWriter writer = new StringWriter();
            // 将 Document 对象转换为 XML 字符串，并写入字符输出流
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            // 将字符输出流转换为字符串并赋值给 xmlStr
            xmlStr = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlStr;
    }

    // 打印文档
    public static void print(Document doc) {
        Element root = doc.getDocumentElement();
        print(root, 0);
    }

    // 打印元素
    public static void print(Element element, int indent) {
        printTrunk(indent);
        System.out.printf("├─ 元素: %s\n", element.getNodeName());

        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attribute = (Attr) attributes.item(i);
            printTrunk(indent + 1);
            System.out.printf("├─ 属性: %s = %s\n", attribute.getName(), attribute.getValue());
        }

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                print((Element) child, indent + 1);
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getNodeValue().trim();
                if (!text.isEmpty()) {
                    printTrunk(indent + 1);
                    System.out.printf("└─ 文本: %s\n", text);
                }
            }
        }
    }

    // 打印结构
    public static void printTrunk(int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print("|   ");
        }
    }
}
