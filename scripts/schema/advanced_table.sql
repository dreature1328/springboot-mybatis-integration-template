CREATE TABLE advanced_table (
    -- 常规字段
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(32) NOT NULL,
    name VARCHAR(100) NOT NULL,
    status INTEGER NOT NULL DEFAULT 1,

    -- JSON 字段（存储动态属性）
    attributes JSONB,
    
    -- 数组字段（存储标签）
    tags TEXT[] DEFAULT '{}',

    -- 时间字段
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- 约束
    CONSTRAINT uq_code UNIQUE (code),
    CONSTRAINT chk_status_range CHECK (status >= 0 AND status <= 10)
);

COMMENT ON COLUMN advanced_table.id IS '主键 ID';
COMMENT ON COLUMN advanced_table.code IS '业务编码';
COMMENT ON COLUMN advanced_table.name IS '名称';
COMMENT ON COLUMN advanced_table.status IS '状态';
COMMENT ON COLUMN advanced_table.attributes IS '动态属性';
COMMENT ON COLUMN advanced_table.tags IS '标签数组';
COMMENT ON COLUMN advanced_table.created_at IS '创建时间';
COMMENT ON COLUMN advanced_table.updated_at IS '更新时间';
