CREATE TABLE standard_table (
    id BIGINT PRIMARY KEY,
    numeric_value INT,
    decimal_value DOUBLE,
    text_content VARCHAR(255),
    active_flag TINYINT(1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基础实体表';

ALTER TABLE standard_table MODIFY COLUMN id BIGINT COMMENT 'ID';
ALTER TABLE standard_table MODIFY COLUMN numeric_value INT COMMENT '整型数值';
ALTER TABLE standard_table MODIFY COLUMN decimal_value DOUBLE COMMENT '浮点数值';
ALTER TABLE standard_table MODIFY COLUMN text_content VARCHAR(255) COMMENT '文本内容';
ALTER TABLE standard_table MODIFY COLUMN active_flag TINYINT(1) COMMENT '激活标志';