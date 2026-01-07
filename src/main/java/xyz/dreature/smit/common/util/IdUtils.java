package xyz.dreature.smit.common.util;

import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IdUtils {
    // ===== ID 解析 =====
    public static <T> List<T> parseIds(String idStr, String delimiter, Function<String, T> parser) {
        if (StringUtils.isBlank(idStr)) {
            return Collections.emptyList();
        }
        return Arrays.stream(idStr.split(delimiter))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(parser)
                .collect(Collectors.toList());
    }


    public static <T> List<T> parseIds(String idStr, Function<String, T> parser) {
        return parseIds(idStr, ",", parser);
    }

    public static List<Short> parseShortIds(String idStr) {
        return parseIds(idStr, Short::parseShort);
    }

    public static List<Integer> parseIntIds(String idStr) {
        return parseIds(idStr, Integer::parseInt);
    }

    public static List<Long> parseLongIds(String idStr) {
        return parseIds(idStr, Long::parseLong);
    }

    public static List<String> parseStringIds(String idStr) {
        return parseIds(idStr, String::trim);
    }

    // ===== ID 拼接 =====
    public static String joinIds(Collection<?> ids, String delimiter) {
        if (ids == null || ids.isEmpty()) return "";
        return ids.stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
    }

    public static String joinIds(Collection<?> ids) {
        return joinIds(ids, ",");
    }


}
