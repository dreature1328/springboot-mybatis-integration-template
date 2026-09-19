package xyz.dreature.smit.common.model.entity.db2;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Schema(description = "空间地理要素实体")
public class GeoEntity {

    @NotNull(message = "ID 不能为空")
    @Min(value = 1, message = "ID 必须为正")
    @Schema(description = "物理主键")
    private Long id;

    @NotBlank(message = "要素编码不能为空")
    @Size(min = 3, max = 32, message = "要素编码长度 3~32 位")
    @Schema(description = "要素业务编码（全局唯一）")
    private String featureId;

    @NotBlank(message = "要素名称不能为空")
    @Size(min = 1, max = 100, message = "要素名称长度 1~100 位")
    @Schema(description = "要素名称")
    private String featureName;

    @Schema(description = "空间几何（WKT 格式，例如 POINT(116.4 39.9)）")
    private String geom;

    @Schema(description = "几何类型（冗余，如 POINT / LINESTRING / POLYGON）")
    private String geomType;

    @Schema(description = "中心点（WKT Point 格式）")
    private String centerPoint;

    @Schema(description = "地理地址")
    private String address;

    @Schema(description = "创建时间（自动生成）")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间（自动更新）")
    private LocalDateTime updatedAt;

    // ===== 构造器 =====
    public GeoEntity() {
    }

    public GeoEntity(Long id, String featureId, String featureName, String geom,
                     String geomType, String centerPoint, String address,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.featureId = featureId;
        this.featureName = featureName;
        this.geom = geom;
        this.geomType = geomType;
        this.centerPoint = centerPoint;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ===== Getter / Setter =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    public String getGeomType() {
        return geomType;
    }

    public void setGeomType(String geomType) {
        this.geomType = geomType;
    }

    public String getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(String centerPoint) {
        this.centerPoint = centerPoint;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        return "GeoEntity{" +
                "id=" + id +
                ", featureId='" + featureId + '\'' +
                ", featureName='" + featureName + '\'' +
                ", geom='" + geom + '\'' +
                ", geomType='" + geomType + '\'' +
                ", centerPoint='" + centerPoint + '\'' +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
