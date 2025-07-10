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
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {
    public static final DocumentBuilderFactory documentBuilderFactory;
    public static final TransformerFactory transformerFactory;
    public static final DocumentBuilder documentBuilder;

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

    // 将 NodeList 转换为 List、
    public static List<Node> nodeListToList(NodeList nodeList) {
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            list.add(nodeList.item(i));
        }
        return list;
    }

    // 将 List 转换为 NodeList
    public static NodeList listToNodeList(List<Node> list) {
        NodeListImpl nodeList = new NodeListImpl();
        for (Node node : list) {
            nodeList.addItem(node);
        }
        return nodeList;
    }

    // 将 Node 转换为 Element
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

    // 将 Node 转换为 Attr
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

    // 将 Node 转换为 Text
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

    // 将 XML 字符串转换为 Document 对象
    public static Document stringToDocument(String xmlString) {
        try {
            // 创建一个 InputSource 对象来包装 XML 字符串
            InputSource inputSource = new InputSource(new StringReader(xmlString));
            // 解析 XML 字符串并返回 Document 对象
            Document document = documentBuilder.parse(inputSource);
            // 将文档标准化
            document.normalize();
            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将 Document 对象转换为 XML 字符串
    public static String documentToString(Document document) {
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

    // 获取元素所有子元素
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

    // 获取元素所有属性
    public static List<Attr> getElementAttrs(Element element) {
        List<Attr> attributes = new ArrayList<>();
        // 获取元素的属性集合
        NamedNodeMap attributeMap = element.getAttributes();
        // 遍历属性集合
        for (int i = 0; i < attributeMap.getLength(); i++) {
            Node attributeNode = attributeMap.item(i);
            // 判断节点类型是否为属性节点
            if (attributeNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                // 将属性节点添加到列表中
                attributes.add((Attr) attributeNode);
            }
        }
        return attributes;
    }

    public static String getElementText(Element element) {
        return element.getTextContent();
    }

    // 获取元素所有同名标签下的所有文本
    public static String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent().trim();
        }
        return "";  // 找不到元素时返回空字符串
    }

    // 从指定文件名读取 XML 文件并返回 Document 对象
    public static Document parseFile(String filename) {
        // 创建一个文件对象
        File xmlFile = new File(filename);
        try {
            // 创建一个FileInputStream对象
            FileInputStream fis = new FileInputStream(xmlFile);
            // 解析XML文件并返回Document对象
            Document doc = documentBuilder.parse(fis);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException | SAXException e) {
            throw new RuntimeException("XML 解析失败", e);
        }
    }

    public static <T extends Node> T castNode(Node node, Class<T> nodeType) {
        if (nodeType.isInstance(node)) {
            return nodeType.cast(node);
        }
        throw new IllegalArgumentException();
    }

    // 打印 XML
    public static void printXml(Document doc) {
        Element root = doc.getDocumentElement();
        printXml(root, 0);
    }

    // 打印 XML
    private static void printXml(Element element, int indent) {
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
                printXml((Element) child, indent + 1);
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getNodeValue().trim();
                if (!text.isEmpty()) {
                    printTrunk(indent + 1);
                    System.out.printf("└─ 文本: %s\n", text);
                }
            }
        }
    }

    // 打印 XML
    private static void printTrunk(int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print("|   ");
        }
    }

    // 自定义 NodeList 实现类
    private static class NodeListImpl implements NodeList {
        private final List<Node> nodes = new ArrayList<>();

        private void addItem(Node node) {
            nodes.add(node);
        }

        @Override
        public Node item(int index) {
            return nodes.get(index);
        }

        @Override
        public int getLength() {
            return nodes.size();
        }
    }

}
