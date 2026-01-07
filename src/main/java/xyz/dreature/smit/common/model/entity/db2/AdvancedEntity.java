package xyz.dreature.smit.common.model.entity.db2;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

// 高级实体
public class AdvancedEntity {
    // ===== 字段 =====
    @NotNull(message = "ID 不能为空")
    @Min(value = 1, message = "ID 必须为正")
    @Max(value = Long.MAX_VALUE, message = "ID 范围受限")
    private Long id;              // 唯一标识符

    @NotBlank(message = "业务编码不能为空")
    @Size(min = 3, max = 32)
    private String code;       // 业务编码

    @NotBlank(message = "名称不能为空")
    @Size(min = 1, max = 100)
    private String name;    // 实体名称

    @NotNull
    @Min(0)
    @Max(10)
    private Integer status;

    // PostgreSQL 支持
    private Map<String, Object> attributes;      // 动态属性（JSONB）

    private String[] tags; // 标签数组（varchar[]）

    private LocalDateTime createdAt;           // 创建时间（自动生成）

    private LocalDateTime updatedAt;           // 更新时间（触发器更新）

    // 无参构造器
    public AdvancedEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 全参构造器
    public AdvancedEntity(Long id, String code, String name, Integer status, Map<String, Object> attributes, String[] tags, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.attributes = attributes;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 复制构造器
    public AdvancedEntity(AdvancedEntity advancedEntity) {
        this.id = advancedEntity.id;
        this.code = advancedEntity.code;
        this.name = advancedEntity.name;
        this.status = advancedEntity.status;
        this.attributes = advancedEntity.attributes;
        this.tags = advancedEntity.tags;
        this.createdAt = advancedEntity.createdAt;
        this.updatedAt = advancedEntity.updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "AdvancedEntity{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", attributes=" + attributes +
                ", tags=" + Arrays.toString(tags) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
