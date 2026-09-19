package xyz.dreature.smit.component.transformer.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.entity.db2.GeoEntity;
import xyz.dreature.smit.component.transformer.JsonTransformer;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// JSON-几何实体转换器
@Component
public class JsonGeoTransformer extends JsonTransformer<GeoEntity> {
    @Autowired
    private ObjectMapper objectMapper;
    private DateTimeFormatter dateTimeFormatter;

    public JsonGeoTransformer() {
        // 为解决父类型构造函数 super() 必须首置的问题，此处暂时传 null，成员初始化交由 @PostConstruct 方法完成
        super(null);
    }

    @PostConstruct
    public void init() {
        this.dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        this.itemParser = createItemParser();
    }

    private Function<JsonNode, GeoEntity> createItemParser() {
        return itemNode -> {
            // 基础字段
            Long id = itemNode.path("id").asLong();
            JsonNode properties = itemNode.path("properties");
            String featureId = properties.path("feature_id").asText(null);
            String featureName = properties.path("feature_name").asText(null);

            // 几何字段：从 geometry 节点转换为 WKT
            JsonNode geometryNode = itemNode.path("geometry");
            String geomWkt = geoJsonToWkt(geometryNode);

            // 几何类型：优先从 properties.geom_type 取，否则从 geometry.type
            String geomType = itemNode.path("geom_type").asText(null);
            if (geomType == null && geometryNode != null && !geometryNode.isMissingNode()) {
                geomType = geometryNode.path("type").asText(null);
            }

            // 中心点：从 properties.center_point 取 [lng, lat] 数组，转为 WKT Point
            JsonNode centerNode = properties.path("center_point");
            String centerPointWkt = centerPointArrayToWkt(centerNode);

            // 地址
            String address = properties.path("address").asText(null);

            // 时间字段
            LocalDateTime createdAt = null;
            LocalDateTime updatedAt = null;
            String createdAtStr = properties.path("created_at").asText(null);
            if (createdAtStr != null) {
                createdAt = LocalDateTime.parse(createdAtStr, dateTimeFormatter);
            }
            String updatedAtStr = properties.path("updated_at").asText(null);
            if (updatedAtStr != null) {
                updatedAt = LocalDateTime.parse(updatedAtStr, dateTimeFormatter);
            }

            // 构造 GeoEntity（使用最新的构造器）
            return new GeoEntity(
                    id,
                    featureId,
                    featureName,
                    geomWkt,
                    geomType,
                    centerPointWkt,
                    address,
                    createdAt,
                    updatedAt
            );
        };
    }

    private String geoJsonToWkt(JsonNode geometryNode) {
        if (geometryNode == null || geometryNode.isNull()) {
            return null;
        }
        String type = geometryNode.path("type").asText();
        JsonNode coordsNode = geometryNode.path("coordinates");

        if (type == null || coordsNode.isMissingNode()) {
            return null;
        }

        StringBuilder wkt = new StringBuilder();
        switch (type) {
            case "Point": {
                double lng = coordsNode.get(0).asDouble();
                double lat = coordsNode.get(1).asDouble();
                wkt.append("POINT (").append(lng).append(" ").append(lat).append(")");
                break;
            }
            case "LineString": {
                wkt.append("LINESTRING (");
                List<String> points = new ArrayList<>();
                for (JsonNode p : coordsNode) {
                    double lng = p.get(0).asDouble();
                    double lat = p.get(1).asDouble();
                    points.add(lng + " " + lat);
                }
                wkt.append(String.join(", ", points));
                wkt.append(")");
                break;
            }
            case "Polygon": {
                wkt.append("POLYGON (");
                List<String> rings = new ArrayList<>();
                for (JsonNode ringNode : coordsNode) {
                    List<String> pts = new ArrayList<>();
                    for (JsonNode p : ringNode) {
                        double lng = p.get(0).asDouble();
                        double lat = p.get(1).asDouble();
                        pts.add(lng + " " + lat);
                    }
                    rings.add("(" + String.join(", ", pts) + ")");
                }
                wkt.append(String.join(", ", rings));
                wkt.append(")");
                break;
            }
            default:
                return null;
        }
        return wkt.toString();
    }

    // [lng, lat] 数组转换为 WKT Point 字符串
    private String centerPointArrayToWkt(JsonNode centerNode) {
        if (centerNode == null || centerNode.isNull() || !centerNode.isArray() || centerNode.size() < 2) {
            return null;
        }
        double lng = centerNode.get(0).asDouble();
        double lat = centerNode.get(1).asDouble();
        return "POINT (" + lng + " " + lat + ")";
    }
}
