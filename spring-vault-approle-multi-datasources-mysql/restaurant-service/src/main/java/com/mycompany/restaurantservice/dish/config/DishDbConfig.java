package com.mycompany.restaurantservice.dish.config;

import com.mycompany.restaurantservice.dish.model.Dish;
import com.mycompany.restaurantservice.dish.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
        entityManagerFactoryRef = "dishEntityManagerFactory",
        transactionManagerRef = "dishTransactionManager",
        basePackageClasses = DishRepository.class
)
public class DishDbConfig {

    private final Environment environment;

    @Bean(name = "dishDataSource")
    @ConfigurationProperties(prefix = "datasource.dish")
    DataSource dataSource() {
        String username = environment.getProperty("datasource.dish.username");
        String password = environment.getProperty("datasource.dish.password");

        log.info("==> datasource.dish.username: {}", username);

        return DataSourceBuilder.create().username(username).password(password).build();
    }

    @Bean(name = "dishEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("dishDataSource") DataSource dataSource) {
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
