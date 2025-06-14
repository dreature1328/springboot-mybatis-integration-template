package dreature.smit.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class BatchUtils {
    // 依次处理（无返回值）
    public static <T> void processEach(List<T> list, Consumer<T> processor) {
        if (list == null || list.isEmpty()) return;
        for (T item : list) {
            processor.accept(item);
        }
    }

    // 依次处理（整数聚合）
    public static <T> int reduceEach(List<T> list, ToIntFunction<T> mapper) {
        int result = 0;
        for (T item : list) {
            result += mapper.applyAsInt(item);
        }
        return result;
    }

    // 依次处理（对象生成）
    public static <T, R> List<R> mapEach(List<T> list, Function<T, R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(mapper.apply(item));
        }
        return result;
    }

    // 分页处理（无返回值）
    public static <T> void processPage(List<T> list, int pageSize, Consumer<List<T>> processor){
        while (!list.isEmpty()) {
            List<T> subList = list.subList(0, Math.min(pageSize, list.size()));
            processor.accept(subList);
            list.subList(0, subList.size()).clear();
        }
    }

    // 分页处理（整数聚合）
    public static <T> int reducePage(List<T> list, int pageSize, ToIntFunction<List<T>> mapper) {
        int result = 0;
        while (!list.isEmpty()) {
            List<T> subList = list.subList(0, Math.min(pageSize, list.size()));
            int subResult = mapper.applyAsInt(subList);
            result += subResult;
            list.subList(0, subList.size()).clear();
        }
        return result;
    }

    // 分页处理（对象生成）
    public static <T, R> List<R> mapPage(List<T> list, int pageSize, Function<List<T>, List<R>> mapper) {
        List<R> result = new ArrayList<>();
        while (!list.isEmpty()) {
            List<T> subList = list.subList(0, Math.min(pageSize, list.size()));
            List<R> subResult = mapper.apply(subList);
            result.addAll(subResult);
            list.subList(0, subList.size()).clear();
        }
        return result;
    }
}
