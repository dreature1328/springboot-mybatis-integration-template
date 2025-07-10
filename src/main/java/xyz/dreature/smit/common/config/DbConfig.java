package xyz.dreature.smit.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.mapper.DataMapper;
import xyz.dreature.smit.service.DbService;
import xyz.dreature.smit.service.impl.DbServiceImpl;

// 数据库服务配置
@Configuration
public class DbConfig {
    // 显式声明泛型 Bean，以解决泛型擦除导致的依赖注入失败的问题
    @Bean
    public DbService<Data, Long> dbService(DataMapper dataMapper) {
        return new DbServiceImpl<>(dataMapper, Long::parseLong);
    }
}
