package xyz.dreature.smit.mapper.db1;

import org.apache.ibatis.annotations.Mapper;
import xyz.dreature.smit.common.model.entity.db1.StandardEntity;
import xyz.dreature.smit.mapper.BaseMapper;

@Mapper
public interface StandardMapper extends BaseMapper<StandardEntity, Long> {
    // ===== 业务扩展操作 =====
}
