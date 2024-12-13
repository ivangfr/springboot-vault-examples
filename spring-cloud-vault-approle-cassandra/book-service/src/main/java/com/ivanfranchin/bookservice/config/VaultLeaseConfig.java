package com.ivanfranchin.bookservice.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;

@Configuration
public class VaultLeaseConfig {

    private static final Logger log = LoggerFactory.getLogger(VaultLeaseConfig.class);

    private final SecretLeaseContainer leaseContainer;

    public VaultLeaseConfig(SecretLeaseContainer leaseContainer) {
        this.leaseContainer = leaseContainer;
    }

    @Value("${spring.cloud.vault.database.role}")
    private String databaseRole;

    @PostConstruct
    private void postConstruct() {
        final String vaultCredsPath = String.format("database/creds/%s", databaseRole);

        leaseContainer.addLeaseListener(event -> {
            log.info("==> Received event: {}", event);

            if (vaultCredsPath.equals(event.getSource().getPath())) {
                if (event instanceof SecretLeaseExpiredEvent && event.getSource().getMode() == RequestedSecret.Mode.RENEW) {
                    log.info("==> Replace RENEW lease by a ROTATE one.");
                    leaseContainer.requestRotatingSecret(vaultCredsPath);
                } else if (event instanceof SecretLeaseCreatedEvent secretLeaseCreatedEvent
                        && event.getSource().getMode() == RequestedSecret.Mode.ROTATE) {
                    String username = (String) secretLeaseCreatedEvent.getSecrets().get("username");
                    String password = (String) secretLeaseCreatedEvent.getSecrets().get("password");

                    log.info("==> Update System properties username & password");
                    System.setProperty("spring.datasource.username", username);
                    System.setProperty("spring.datasource.password", password);

                    log.info("==> spring.datasource.username: {}", username);

                    updateDataSource(username, password);
                }
            }
        });
    }

    private void updateDataSource(String username, String password) {
        // TODO
    }
}
