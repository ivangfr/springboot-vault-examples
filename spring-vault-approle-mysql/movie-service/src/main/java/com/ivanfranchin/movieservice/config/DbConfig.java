package com.ivanfranchin.movieservice.config;

import com.ivanfranchin.movieservice.movie.model.Movie;
import com.ivanfranchin.movieservice.movie.MovieRepository;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = MovieRepository.class)
public class DbConfig {

    private static final Logger log = LoggerFactory.getLogger(DbConfig.class);

    private final Environment environment;

    public DbConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @ConfigurationProperties("datasource")
    DataSource dataSource() {
        String username = environment.getProperty("datasource.username");
        log.info("==> datasource.username: {}", username);

        // jdbcUrl, username and password are set implicitly in the "create" below
        return DataSourceBuilder.create().build();
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                DataSource dataSource) {
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
