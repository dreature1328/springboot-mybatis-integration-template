-- 启用 PostGIS 扩展
CREATE EXTENSION IF NOT EXISTS postgis;

DROP TABLE IF EXISTS geo_table CASCADE;

CREATE TABLE geo_table (
    -- 常规字段
    id BIGSERIAL PRIMARY KEY,
    feature_id VARCHAR(32) NOT NULL,
    feature_name VARCHAR(100) NOT NULL,

    -- 空间几何字段
    geom GEOMETRY(Geometry, 4326),   -- 4326 表示 WGS84 经纬度坐标系

    -- 几何类型
    geom_type VARCHAR(20),

    -- 中心点
    center_point GEOMETRY(Point, 4326),

    -- 地址
    address TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_feature_id UNIQUE (feature_id)
);
