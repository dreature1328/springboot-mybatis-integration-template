CREATE TABLE standard_table (
    id BIGINT PRIMARY KEY,
    numeric_value INT,
    decimal_value DOUBLE,
    text_content VARCHAR(255),
    active_flag TINYINT(1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据存储表';


COMMENT ON COLUMN standard_table.id IS 'ID';
COMMENT ON COLUMN standard_table.numeric_value IS '整型数值';
COMMENT ON COLUMN standard_table.decimal_value IS '浮点数值';
COMMENT ON COLUMN standard_table.text_content IS '文本内容';
COMMENT ON COLUMN standard_table.active_flag IS '激活标志';
