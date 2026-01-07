package xyz.dreature.smit.component.transformer.impl;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import xyz.dreature.smit.common.model.entity.db2.AdvancedEntity;
import xyz.dreature.smit.common.util.XmlUtils;
import xyz.dreature.smit.component.transformer.XmlTransformer;

import java.util.function.Function;

// XML-高级实体转换器
@Component
public class XmlAdvancedTransformer extends XmlTransformer<AdvancedEntity> {
    public XmlAdvancedTransformer() {
        super(new Function<Element, AdvancedEntity>() {
            @Override
            public AdvancedEntity apply(Element itemElement) {
                return new AdvancedEntity(
                        // 解析常规字段
                        XmlUtils.getLong(itemElement, "id"),
                        XmlUtils.getString(itemElement, "code"),
                        XmlUtils.getString(itemElement, "name"),
                        XmlUtils.getInt(itemElement, "status"),
                        // 解析 JSON 字段
                        XmlUtils.getMap(itemElement, "attributes"),
                        // 解析数组字段
                        XmlUtils.getStringArray(itemElement, "tags", "tag"),
                        // 解析时间字段
                        XmlUtils.getDateTime(itemElement, "createdAt"),
                        XmlUtils.getDateTime(itemElement, "updatedAt")
                );
            }
        });
    }
}
