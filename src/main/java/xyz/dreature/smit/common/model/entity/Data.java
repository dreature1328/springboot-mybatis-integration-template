package xyz.dreature.smit.common.model.entity;

import javax.validation.constraints.*;

// 数据实体
public class Data {
    // ===== 字段 =====
    @NotNull(message = "ID 不能为空")
    @Min(value = 1, message = "ID 必须为正")
    @Max(value = Long.MAX_VALUE, message = "ID 范围受限")
    private Long id;              // 唯一标识符

    @NotNull(message = "整型字段不能为空")
    @Min(value = 0, message = "整型字段不能为负")
    @Max(value = Integer.MAX_VALUE, message = "整型字段范围受限")
    private Integer numericValue; // 整型数值

    @NotNull(message = "浮点字段不能为空")
    @DecimalMin(value = "0.0", message = "浮点字段不能为负")
    @Digits(integer = 15, fraction = 6, message = "浮点字段整数位不超过 15 位，小数位不超过 6 位")
    private Double decimalValue;  // 浮点数值

    @NotBlank(message = "文本字段不能为空")
    @Size(min = 1, max = 255, message = "文本字段范围受限")
    private String textContent;   // 文本内容

    @NotNull(message = "布尔字段不能为空")
    private Boolean activeFlag;   // 激活标志

    // ===== 构造方法 =====
    // 无参构造器
    public Data() {
    }

    // 全参构造器
    public Data(Long id, Integer numericValue, Double decimalValue, String textContent, Boolean activeFlag) {
        this.id = id;
        this.numericValue = numericValue;
        this.decimalValue = decimalValue;
        this.textContent = textContent;
        this.activeFlag = activeFlag;
    }

    // 复制构造器
    public Data(Data data) {
        this.id = data.getId();
        this.numericValue = data.getNumericValue();
        this.decimalValue = data.getDecimalValue();
        this.textContent = data.getTextContent();
        this.activeFlag = data.getActiveFlag();
    }

    // ===== Getter 与 Setter 方法 =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Integer numericValue) {
        this.numericValue = numericValue;
    }

    public Double getDecimalValue() {
        return decimalValue;
    }

    public void setDecimalValue(Double decimalValue) {
        this.decimalValue = decimalValue;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    // ===== 其他 =====
    // 字符串表示
    @Override
    public String toString() {
        return "Data{" + "id=" + id + ", numericValue=" + numericValue + ", decimalValue=" + decimalValue + ", textContent='" + textContent + '\'' + ", activeFlag=" + activeFlag + '}';
    }
}
