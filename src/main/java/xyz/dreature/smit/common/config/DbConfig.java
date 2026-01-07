package xyz.dreature.smit.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import xyz.dreature.smit.common.model.entity.db1.StandardEntity;
import xyz.dreature.smit.common.model.entity.db2.AdvancedEntity;
import xyz.dreature.smit.mapper.db1.StandardMapper;
import xyz.dreature.smit.mapper.db2.AdvancedMapper;
import xyz.dreature.smit.service.DbService;
import xyz.dreature.smit.service.impl.DbServiceImpl;

import javax.sql.DataSource;

// 数据库配置
@Configuration
public class DbConfig {
    // ===== 数据源配置参数 =====
    @Value("${mybatis.db1.typeAliasesPackage}")
    private String typeAliasesPackage1;

    @Value("${mybatis.db1.mapperLocations}")
    private String mapperLocations1;

    @Value("${mybatis.db2.typeAliasesPackage}")
    private String typeAliasesPackage2;

    @Value("${mybatis.db2.mapperLocations}")
    private String mapperLocations2;

    @Value("${mybatis.db1.configuration.mapUnderscoreToCamelCase}")
    private Boolean mapUnderscoreToCamelCase1;

    @Value("${mybatis.db1.configuration.cacheEnabled}")
    private Boolean cacheEnabled1;

    @Value("${mybatis.db2.configuration.mapUnderscoreToCamelCase}")
    private Boolean mapUnderscoreToCamelCase2;

    @Value("${mybatis.db2.configuration.cacheEnabled}")
    private Boolean cacheEnabled2;

    // ===== MyBatis 会话工厂、模板、事务管理器配置 =====
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSource dataSource1() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory1(@Qualifier("dataSource1") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage(typeAliasesPackage1);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(mapperLocations1));

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(mapUnderscoreToCamelCase1);
        configuration.setCacheEnabled(cacheEnabled1);
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }

    @Bean
    @Primary
    public SqlSessionTemplate sqlSessionTemplate1(@Qualifier("sqlSessionFactory1") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    @Primary
    public DataSourceTransactionManager transactionManager1(@Qualifier("dataSource1") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    public DataSource dataSource2() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory2(@Qualifier("dataSource2") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage(typeAliasesPackage2);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(mapperLocations2));

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(mapUnderscoreToCamelCase2);
        configuration.setCacheEnabled(cacheEnabled2);
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate2(@Qualifier("sqlSessionFactory2") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public DataSourceTransactionManager transactionManager2(@Qualifier("dataSource2") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // ===== 数据库服务定义 =====
    // 指定服务键（用于注册）与映射器
    @Bean
    @Primary
    @Lazy
    public DbService<StandardEntity, Long> standardDbService(StandardMapper standardMapper) {
        return new DbServiceImpl<>("db1", standardMapper);
    }

    @Bean
    @Lazy
    public DbService<AdvancedEntity, Long> advancedDbService(AdvancedMapper advancedMapper) {
        return new DbServiceImpl<>("db2", advancedMapper);
    }

    // ===== MyBatis 映射器扫描配置 =====
    // 指定包扫描路径与会话工厂
    @Configuration
    @MapperScan(
            basePackages = "xyz.dreature.smit.mapper.db1",
            sqlSessionFactoryRef = "sqlSessionFactory1"
    )
    static class Db1MapperScanConfig {
    }

    @Configuration
    @MapperScan(
            basePackages = "xyz.dreature.smit.mapper.db2",
            sqlSessionFactoryRef = "sqlSessionFactory2"
    )
    static class Db2MapperScanConfig {
    }
}
