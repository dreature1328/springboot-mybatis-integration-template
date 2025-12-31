package xyz.dreature.smit.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.dreature.smit.common.model.entity.Data;

@Mapper
public interface DataMapper extends BaseMapper<Data, Long> {
    // ===== 业务扩展操作 =====
}
