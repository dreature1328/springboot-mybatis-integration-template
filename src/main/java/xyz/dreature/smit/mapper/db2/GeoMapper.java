package xyz.dreature.smit.mapper.db2;

import org.apache.ibatis.annotations.Mapper;
import xyz.dreature.smit.common.model.entity.db2.GeoEntity;
import xyz.dreature.smit.mapper.base.BaseMapper;

@Mapper
public interface GeoMapper extends BaseMapper<GeoEntity, Long> {
    // ===== 业务扩展操作 =====
}
