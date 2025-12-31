package xyz.dreature.smit.component.transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xyz.dreature.smit.common.model.context.EtlContext;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// XML 转换器
public abstract class XmlTransformer<T> implements Transformer<Document, T> {
    protected Function<Element, T> itemParser;

    public XmlTransformer(Function<Element, T> itemParser) {
        this.itemParser = itemParser;
    }

    // 转换器键
    @Override
    public String getKey() {
        return Document.class.getName() + "->" +
                ((Class) ((ParameterizedType) getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0]).getName();
    }

    // 单项转换
    @Override
    public List<T> transform(EtlContext context, Document document) {
        if (document == null) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>();
        NodeList dataNodes = document.getElementsByTagName("data");

        if (dataNodes.getLength() == 0) {
            return Collections.emptyList();
        }

        Element dataElement = (Element) dataNodes.item(0);
        NodeList itemNodes = dataElement.getElementsByTagName("item");

        for (int i = 0; i < itemNodes.getLength(); i++) {
            Node itemNode = itemNodes.item(i);
            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) itemNode;
                result.add(parseItem(itemElement));
            }
        }
        return result;
    }

    // 流式转换
    @Override
    public List<T> transformStream(EtlContext context, List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }

        return documents.parallelStream()
                .filter(Objects::nonNull)
                .flatMap(document -> {
                    NodeList dataNodes = document.getElementsByTagName("data");
                    if (dataNodes.getLength() == 0) {
                        return Stream.empty();
                    }
                    Element dataElement = (Element) dataNodes.item(0);
                    NodeList itemNodes = dataElement.getElementsByTagName("item");
                    return IntStream.range(0, itemNodes.getLength())
                            .mapToObj(itemNodes::item)
                            .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                            .map(node -> (Element) node);
                })
                .map(this::parseItem)
                .collect(Collectors.toList());
    }

    // XML 元素映射实体类
    public T parseItem(Element itemElement) {
        return itemParser.apply(itemElement);
    }
}
