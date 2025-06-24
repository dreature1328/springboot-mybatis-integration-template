package dreature.smit.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.w3c.dom.Document;

import java.util.List;

public interface TransformService<T> {
    // 生成数据（测试用）
    List<T> generate(int count);

    // 单项转换
    List<T> transform(JsonNode jsonNode);

    // 单项转换
    List<T> transform(Document document);

    // 逐项转换
    List<T> transform(JsonNode... jsonNodes);

    // 逐项转换
    List<T> transform(Document... document);

    // 流式转换
    List<T> transformStream(List<JsonNode> jsonNodes);
}
