package com.ivanfranchin.restaurantservice.dish.config;

import com.ivanfranchin.restaurantservice.dish.DishRepository;
import com.ivanfranchin.restaurantservice.dish.model.Dish;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
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
@EnableJpaRepositories(
        entityManagerFactoryRef = "dishEntityManagerFactory",
        transactionManagerRef = "dishTransactionManager",
        basePackageClasses = DishRepository.class
)
public class DishDbConfig {

    private static final Logger log = LoggerFactory.getLogger(DishDbConfig.class);

    private final Environment environment;

    public DishDbConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "dishDataSource")
    @ConfigurationProperties("datasource.dish")
    DataSource dataSource() {
        String username = environment.getProperty("datasource.dish.username");
        log.info("==> datasource.dish.username: {}", username);

        // jdbcUrl, username and password are set implicitly in the "create" below
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dishEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                @Qualifier("dishDataSource") DataSource dataSource) {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.show-sql", environment.getProperty("spring.jpa.show-sql"));

        return builder.dataSource(dataSource)
                .packages(Dish.class)
                .persistenceUnit("dish")
                .properties(properties)
                .build();
    }

    @Bean(name = "dishTransactionManager")
    PlatformTransactionManager transactionManager(
            @Qualifier("dishEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
