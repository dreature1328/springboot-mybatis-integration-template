package xyz.dreature.smit.component.loader;

import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.common.util.BatchUtils;

import java.io.Serializable;
import java.util.List;

// 加载器接口
public interface Loader<T, ID extends Serializable> {
    // 单项加载
    int load(EtlContext context, T targetData);

    // 逐项加载 / 单批加载
    default int loadBatch(EtlContext context, List<T> targetData) {
        // 默认实现，当某个场景不存在批量优化策略时，则以逐项加载（循环调用单项加载）代替
        return BatchUtils.reduceEachToInt(targetData, each -> load(context, each));
    }
}
