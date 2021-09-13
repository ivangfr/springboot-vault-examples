package com.mycompany.restaurantservice.dish.config;

import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DishVaultLeaseConfig {

    @Value("${datasource.dish.vault-creds-path}")
    private String vaultCredsPath;

    private final ApplicationContext applicationContext;
    private final Environment environment;
    private final SecretLeaseContainer leaseContainer;

    @PostConstruct
    private void postConstruct() {
        leaseContainer.addLeaseListener(event -> {
            if (vaultCredsPath.equals(event.getSource().getPath())) {
                log.info("==> Received event: {}", event);

                if (event instanceof SecretLeaseCreatedEvent) {
                    String username = environment.getProperty("datasource.dish.username");
                    String password = environment.getProperty("datasource.dish.password");

                    log.info("==> datasource.dish.username: {}", username);

                    updateDataSource(username, password);
                }
            }
        });
    }

    private void updateDataSource(String username, String password) {
        HikariDataSource hikariDataSource = (HikariDataSource) applicationContext.getBean("dishDataSource");

        log.info("==> Soft evict database connections");
        HikariPoolMXBean hikariPoolMXBean = hikariDataSource.getHikariPoolMXBean();
        if (hikariPoolMXBean != null) {
            hikariPoolMXBean.softEvictConnections();
        }

        log.info("==> Update database credentials");
        HikariConfigMXBean hikariConfigMXBean = hikariDataSource.getHikariConfigMXBean();
        hikariConfigMXBean.setUsername(username);
        hikariConfigMXBean.setPassword(password);
    }
}
