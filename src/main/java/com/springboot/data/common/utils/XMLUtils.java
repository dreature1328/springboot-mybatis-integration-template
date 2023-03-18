package com.springboot.data.common.utils;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

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

public class XMLUtils {
    private static final DocumentBuilderFactory documentBuilderFactory;
    private static final TransformerFactory transformerFactory;
    private static final DocumentBuilder documentBuilder;

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

    /**
     * 自定义的 NodeList 实现类
     */
    private static class NodeListImpl implements NodeList {
        private final List<Node> nodes = new ArrayList<>();

        /**
         * 添加节点到 NodeList 中
         * @param node 要添加的节点
         */
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

    /**
     * 将 NodeList 转换为 List
     * @param nodeList 要转换的 NodeList 对象
     * @return 转换后的 List
     */
    public static List<Node> nodeListToList(NodeList nodeList) {
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            list.add(nodeList.item(i));
        }
        return list;
    }

    /**
     * 将 List 转换为 NodeList
     * @param list 要转换的 List 对象
     * @return 转换后的 NodeList
     */
    public static NodeList listToNodeList(List<Node> list) {
        NodeListImpl nodeList = new NodeListImpl();
        for (Node node : list) {
            nodeList.addItem(node);
        }
        return nodeList;
    }

    /**
     * 将 Node 转换为 Element
     * @param node 要转换的节点
     * @return 转换后的元素节点
     */
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

    /**
     * 将 Node 转换为 Attr
     * @param node 要转换的节点
     * @return 转换后的属性节点
     */
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

    /**
     * 将 Node 转换为 Text
     * @param node 要转换的节点
     * @return 转换后的文本节点
     */
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

    /**
     * 将 Element 转换为 Node
     * @param element 要转换的元素节点
     * @return 转换后的节点
     */
    public static Node elementToNode(Element element) {
        return element;
    }

    /**
     * 将 Attr 转换为 Node
     * @param attr 要转换的属性节点
     * @return 转换后的节点
     */
    public static Node attrToNode(Attr attr) {
        return attr;
    }

    /**
     * 将 Text 转换为 Node
     * @param text 要转换的文本节点
     * @return 转换后的节点
     */
    public static Node textToNode(Text text) {
        return text;
    }

    /**
     * 将 XML 字符串转换为 Document 对象
     * @param xmlStr XML 字符串
     * @return Document 对象
     */
    public static Document strToDoc(String xmlStr) {
        try {
            // 创建一个 InputSource 对象来包装 XML 字符串
            InputSource inputSource = new InputSource(new StringReader(xmlStr));
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

    /**
     * 将 Document 对象转换为 XML 字符串
     * @param document 要转换的 Document 对象
     * @return 转换后的 XML 字符串
     */
    public static String docToStr(Document document) {
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

    /**
     * 获取指定节点的所有子节点
     * @param node 父节点
     * @return 所有子节点列表
     */
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

    /**
     * 获取指定元素的所有子元素
     * @param element 父元素
     * @return 所有子元素列表
     */
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

    /**
     * 获取元素所有属性
     * @param element 元素节点
     * @return 属性列表
     */
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

    /**
     * 从指定文件名读取 XML 文件并返回 Document 对象
     * @param filename XML 文件名
     * @return Document 对象
     */
    public static Document readXMLFile(String filename) {
        // 创建一个文件对象
        File xmlFile = new File(filename);
        try {
            // 创建一个FileInputStream对象
            FileInputStream fis = new FileInputStream(xmlFile);
            // 解析XML文件并返回Document对象
            Document doc = documentBuilder.parse(fis);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 打印 XML 文档
     * @param doc XML文档对象
     */
    public static void printXML(Document doc) {
        Element root = doc.getDocumentElement();
        printXML(System.out, root, 0);
    }

    /**
     * 打印 XML 元素及其属性、子元素
     * @param element XML元素
     */
    public static void printXML(Element element) {
        printXML(System.out, element, 0);
    }

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

    /**
     * 根据缩进层数打印树干，也就是前置"|   "
     * @param ps 打印流
     * @param indent 缩进层数
     */
    private static void printTrunk(PrintStream ps, int indent) {
        for (int i = 0; i < indent; i++) {
            ps.print("|   ");
        }
    }
}
