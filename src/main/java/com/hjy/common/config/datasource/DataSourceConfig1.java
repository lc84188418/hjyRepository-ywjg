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
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

//表示这个类为一个配置类
@Configuration
//配置mybatis的接口类放的地方
@MapperScan(basePackages = {"com.hjy.hall.dao","com.hjy.list.dao","com.hjy.log.dao","com.hjy.synthetical.dao","com.hjy.system.dao","com.hjy.warning.dao"}, sqlSessionFactoryRef = "test1SqlSessionFactory")
public class DataSourceConfig1 {
    // 将这个对象放入Spring容器中
    @Bean(name = "test1DataSource")
    // 表示这个数据源是默认主数据源
    @Primary
    // 读取application.properties中的配置参数映射成为一个对象
    // prefix表示参数的前缀
    @ConfigurationProperties(prefix = "spring.datasource.oracle1")
    public DataSource getDateSource1() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "test1SqlSessionFactory")
    // 表示这个数据源是默认数据源
    @Primary
    // @Qualifier表示查找Spring容器中名字为test1DataSource的对象
    public SqlSessionFactory test1SqlSessionFactory(@Qualifier("test1DataSource") DataSource datasource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(datasource);
        VFS.addImplClass(SpringBootVFS.class);
        //mybatis的facroty需要加载SpringBoot独特的虚拟文件系统，才能识别类路
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        bean.setConfigLocation(resolver.getResource("classpath:mybatis-config.xml"));
        bean.setMapperLocations(
                // 设置mybatis的xml所在位置
                new PathMatchingResourcePatternResolver().getResources("classpath:mapper1/*.xml"));
//        bean.setTransactionFactory(new DataSourceTransactionManager());
        return bean.getObject();
    }
    /**
     * 配置事物管理器
     * @param
     * @return
     */
//    @Bean(name = "masterTransactionManager")
//    @Primary
//    public DataSourceTransactionManager masterTransactionManager() {
//        return new DataSourceTransactionManager(getDateSource1());
//    }
    //配置事务
    @Bean(name="test1TransactionManager")
    @Primary
    public DataSourceTransactionManager testTransactionManager(@Qualifier("test1DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("test1SqlSessionTemplate")
    // 表示这个数据源是默认数据源
    @Primary
    public SqlSessionTemplate test1sqlsessiontemplate(
            @Qualifier("test1SqlSessionFactory") SqlSessionFactory sessionfactory) {
        return new SqlSessionTemplate(sessionfactory);
    }
}
