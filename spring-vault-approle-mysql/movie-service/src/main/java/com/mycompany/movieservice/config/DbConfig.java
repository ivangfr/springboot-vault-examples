package com.mycompany.movieservice.config;

import com.mycompany.movieservice.model.Movie;
import com.mycompany.movieservice.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackageClasses = MovieRepository.class
)
public class DbConfig {

    private final Environment environment;

    @Bean
    @ConfigurationProperties(prefix = "datasource")
    DataSource dataSource() {
        String url = environment.getProperty("datasource.jdbc-url");
        String username = environment.getProperty("datasource.username");
        String password = environment.getProperty("datasource.password");

        log.info("==> datasource.jdbc-url: {}", url);
        log.info("==> datasource.username: {}", username);

        return DataSourceBuilder.create().url(url).username(username).password(password).build();
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, DataSource dataSource) {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.show-sql", environment.getProperty("spring.jpa.show-sql"));

        return builder.dataSource(dataSource)
                .packages(Movie.class)
                .persistenceUnit("movie")
                .properties(properties)
                .build();
    }

    @Bean
    PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
