package xyz.dreature.smit.mapper.db2;

import org.apache.ibatis.annotations.Mapper;
import xyz.dreature.smit.common.model.entity.db2.AdvancedEntity;
import xyz.dreature.smit.mapper.base.BaseMapper;

@Mapper
public interface AdvancedMapper extends BaseMapper<AdvancedEntity, Long> {
    // ===== 业务扩展操作 =====
}
