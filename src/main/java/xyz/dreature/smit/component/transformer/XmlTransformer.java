package xyz.dreature.smit.component.transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import xyz.dreature.smit.common.model.context.Context;
import xyz.dreature.smit.common.util.XmlUtils;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
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
        return Document.class.getSimpleName() + "->" +
                ((Class) ((ParameterizedType) getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0]).getSimpleName();
    }

    // 单项转换
    @Override
    public List<T> transform(Context context, Document document) {
        if (document == null) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>();
        NodeList dataNodes = document.getElementsByTagName("data");

        if (dataNodes.getLength() == 0) {
            return Collections.emptyList();
        }

        Element dataElement = (Element) dataNodes.item(0);
        List<Element> itemElements = XmlUtils.getChildElements(dataElement, "item");

        for (Element itemElement : itemElements) {
            result.add(parseItem(itemElement));
        }
        return result;
    }

    // 流式转换
    @Override
    public List<T> transformStream(Context context, List<Document> documents) {
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

                    // 只获取直接子元素
                    List<Element> itemElements = XmlUtils.getChildElements(dataElement, "item");
                    return itemElements.stream();
                })
                .map(this::parseItem)
                .collect(Collectors.toList());
    }

    // XML 元素映射实体类
    public T parseItem(Element itemElement) {
        return itemParser.apply(itemElement);
    }
}
