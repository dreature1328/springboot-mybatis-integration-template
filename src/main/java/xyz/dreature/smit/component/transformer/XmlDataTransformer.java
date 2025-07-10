package xyz.dreature.smit.component.transformer;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.common.util.XmlUtils;

import java.util.function.Function;

// XML-实体转换器
@Component
public class XmlDataTransformer extends XmlTransformer<Data> {

    public XmlDataTransformer() {
        // 使用匿名内部类
        super(new Function<Element, Data>() {
            @Override
            public Data apply(Element itemElement) {
                return new Data(
                        Long.parseLong(XmlUtils.getElementText(itemElement, "id")),
                        Integer.parseInt(XmlUtils.getElementText(itemElement, "numericValue")),
                        Double.parseDouble(XmlUtils.getElementText(itemElement, "decimalValue")),
                        XmlUtils.getElementText(itemElement, "textContent"),
                        Boolean.parseBoolean(XmlUtils.getElementText(itemElement, "activeFlag"))
                );
            }
        });
    }
}
