package com.example.projectbluebatch.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.projectbluebatch.repository",
        entityManagerFactoryRef = "dataEntityManager",
        transactionManagerRef = "dataTransactionManager"
)
public class DataDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-data")
    public HikariDataSource dataDBSource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/project_blue?rewriteBatchedStatements=true");
        dataSource.setUsername("${DB_USERNAME}");
        dataSource.setPassword("${DB_PASSWORD}");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean dataEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataDBSource());
        em.setPackagesToScan("com.example.projectbluebatch.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");  // 추가: MySQL에 맞는 Hibernate Dialect 설정
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager dataTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(dataEntityManager().getObject());

        return transactionManager;
    }
}
