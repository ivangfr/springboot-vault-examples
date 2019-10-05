package com.mycompany.movieservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DatasourseConfig {

    private final Environment environment;

    @Bean
    @ConfigurationProperties(prefix = "datasource")
    DataSource dataSource() {
        String username = environment.getProperty("datasource.username");
        String password = environment.getProperty("datasource.password");

        log.info("==> datasource.username: {}", username);

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }

}
