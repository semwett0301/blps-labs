package com.example.code.config;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import org.postgresql.xa.PGXADataSource;

@Configuration
public class DataSourceConfiguration {
    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;
    @Bean(name = "pgDataSourceResource")
    @Primary
    public PoolingDataSource setupPoolingDataSource() {
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("pgDataSourceResource");
        pds.setClassName(PGXADataSource.class.getName());
        pds.setMaxPoolSize(50);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", username);
        pds.getDriverProperties().put("password", password);
        pds.getDriverProperties().put("url", url);

        pds.setAutomaticEnlistingEnabled(true);
        pds.init();
        return pds;
    }

    @Bean(name = "entityManagerFactory")
    @Primary
    @DependsOn({"transactionManager", "pgDataSourceResource"})
    public LocalContainerEntityManagerFactoryBean primaryMySqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .jta(true)
                .persistenceUnit("pgUnit")
                .packages("com.example.code.model.entities")
                .build();
    }
}
