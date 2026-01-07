package xyz.dreature.smit.component.transformer.impl;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import xyz.dreature.smit.common.model.entity.db1.StandardEntity;
import xyz.dreature.smit.common.util.XmlUtils;
import xyz.dreature.smit.component.transformer.XmlTransformer;

import java.util.function.Function;

// XML-标准实体转换器
@Component
public class XmlStandardTransformer extends XmlTransformer<StandardEntity> {
    public XmlStandardTransformer() {
        // 使用匿名内部类
        super(new Function<Element, StandardEntity>() {
            @Override
            public StandardEntity apply(Element itemElement) {
                return new StandardEntity(
                        // 解析常规字段
                        XmlUtils.getLong(itemElement, "id"),
                        XmlUtils.getInt(itemElement, "numericValue"),
                        XmlUtils.getDouble(itemElement, "decimalValue"),
                        XmlUtils.getString(itemElement, "textContent"),
                        XmlUtils.getBool(itemElement, "activeFlag")
                );
            }
        });
    }
}
