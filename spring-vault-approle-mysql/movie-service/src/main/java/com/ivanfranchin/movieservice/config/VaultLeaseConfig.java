package com.ivanfranchin.movieservice.config;

import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;

@Configuration
public class VaultLeaseConfig {

    private static final Logger log = LoggerFactory.getLogger(VaultLeaseConfig.class);

    private final ApplicationContext applicationContext;
    private final Environment environment;
    private final SecretLeaseContainer leaseContainer;

    public VaultLeaseConfig(ApplicationContext applicationContext, Environment environment, SecretLeaseContainer leaseContainer) {
        this.applicationContext = applicationContext;
        this.environment = environment;
        this.leaseContainer = leaseContainer;
    }

    @Value("${datasource.vault-creds-path}")
    private String vaultCredsPath;

    @PostConstruct
    private void postConstruct() {
        leaseContainer.addLeaseListener(event -> {
            log.info("==> Received event: {}", event);

            if (event instanceof SecretLeaseCreatedEvent && vaultCredsPath.equals(event.getSource().getPath())) {
                String username = environment.getProperty("datasource.username");
                String password = environment.getProperty("datasource.password");

                log.info("==> datasource.username: {}", username);

                updateDataSource(username, password);
            }
        });
    }

    private void updateDataSource(String username, String password) {
        HikariDataSource hikariDataSource = (HikariDataSource) applicationContext.getBean("dataSource");

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
