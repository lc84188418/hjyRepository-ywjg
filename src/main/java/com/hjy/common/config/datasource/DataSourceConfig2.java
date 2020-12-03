package com.hjy.common.config.datasource;

import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.hjy.tbk.dao", sqlSessionFactoryRef = "test2SqlSessionFactory")
public class DataSourceConfig2 {
    @Bean(name = "test2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.oracle2")
    public DataSource getDateSource2() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "test2SqlSessionFactory")
    public SqlSessionFactory test2SqlSessionFactory(@Qualifier("test2DataSource") DataSource datasource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(datasource);
        VFS.addImplClass(SpringBootVFS.class);//mybatis的facroty需要加载SpringBoot独特的虚拟文件系统，才能识别类路
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        bean.setConfigLocation(resolver.getResource("classpath:mybatis-config.xml"));
        bean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mapper2/*.xml"));
        return bean.getObject();
    }

    @Bean("test2SqlSessionTemplate")
    public SqlSessionTemplate test2sqlsessiontemplate(
            @Qualifier("test2SqlSessionFactory") SqlSessionFactory sessionfactory) {
        return new SqlSessionTemplate(sessionfactory);
    }
}