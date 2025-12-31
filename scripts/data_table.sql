CREATE TABLE data_table (
    id BIGINT PRIMARY KEY COMMENT '唯一标识符',
    numeric_value INT COMMENT '整型数值',
    decimal_value DOUBLE COMMENT '浮点数值',
    text_content VARCHAR(255) COMMENT '文本内容',
    active_flag TINYINT(1) COMMENT '激活标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据存储表';