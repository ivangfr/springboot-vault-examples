package com.mycompany.movieservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DatasourceConfig {

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Bean
    @ConfigurationProperties(prefix = "datasource")
    DataSource getDataSource() {
        log.info("==> username: {}", username);
        log.info("==> password: {}", password);

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }

}
