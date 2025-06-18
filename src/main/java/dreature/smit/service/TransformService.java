package dreature.smit.service;

import java.util.List;

public interface TransformService<T> {
    // 单项转换
    List<T> transform(String response);

    // 逐项转换
    List<T> transform(String... responses);

    // 流水线转换
    List<T> transformPipeline(List<String> responses);
}
