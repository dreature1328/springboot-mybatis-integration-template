package dreature.smit.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import dreature.smit.common.util.BatchUtils;
import dreature.smit.entity.Data;
import dreature.smit.service.TransformService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class DataTransformServiceImpl extends BaseServiceImpl<Data> implements TransformService<Data> {
    // ----- 数据转换 -----
    // 生成数据（测试用）
    public List<Data> generate(int count) {
        // 此处以 UUID 作为 ID，长度为 16 的随机字符串作为属性值为例
        List<Data> dataList = new ArrayList<>(count);
        String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        int length = 16;

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < count; i++) {
            String id = UUID.randomUUID().toString();

            sb.setLength(0);
            for (int j = 0; j < length; j++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            String attr1 = sb.toString();

            sb.setLength(0);
            for (int j = 0; j < length; j++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            String attr2 = sb.toString();

            dataList.add(new Data(id, attr1, attr2));
        }
        return dataList;
    }

    // JSON 节点映射实体类
    public Data parseItemNode(JsonNode itemNode) {
        return new Data(
                itemNode.path("id").asText(),
                itemNode.path("key1").asText(),
                itemNode.path("key2").asText()
        );
    }
    
    // XML 元素映射实体类
    private Data parseItemElement(Element itemElement) {
        return new Data(
                itemElement.getElementsByTagName("id").item(0).getTextContent(),
                itemElement.getElementsByTagName("key1").item(0).getTextContent(),
                itemElement.getElementsByTagName("key2").item(0).getTextContent()
        );
    }

    // 单项转换
    public List<Data> transform(JsonNode jsonNode) {
        List<Data> result = new ArrayList<>();
        JsonNode arrayNode = jsonNode.path("data");

        if (arrayNode.isArray()) {
            for (JsonNode itemNode : arrayNode) {
                result.add(parseItemNode(itemNode));
            }
        }

        return result;
    }

    // 单项转换
    public List<Data> transform(Document document) {
        List<Data> result = new ArrayList<>();
        NodeList dataNodes = document.getElementsByTagName("data");
        Element dataElement = (Element) dataNodes.item(0);
        NodeList itemNodes = dataElement.getElementsByTagName("item");

        for (int i = 0; i < itemNodes.getLength(); i++) {
            Node itemNode = itemNodes.item(i);

            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) itemNode;
                result.add(parseItemElement(itemElement));
            }
        }
        return result;
    }

    // 逐项转换
    public List<Data> transform(JsonNode... jsonNodes) {
        return BatchUtils.mapEach(new ArrayList<>(Arrays.asList(jsonNodes)), this::transform);
    }

    // 逐项转换
    public List<Data> transform(Document... documents) {
        return BatchUtils.mapEach(new ArrayList<>(Arrays.asList(documents)), this::transform);
    }

    // 流式转换
    public List<Data> transformStream(List<JsonNode> jsonNodes) {
        return jsonNodes.stream()
                .filter(Objects::nonNull)
                .flatMap(jsonNode -> {
                    JsonNode arrayNode = jsonNode.path("data");
                    return arrayNode.isArray()
                            ? StreamSupport.stream(arrayNode.spliterator(), false)
                            : Stream.empty();
                })
                .map(this::parseItemNode)
                .collect(Collectors.toList());
    }



}
