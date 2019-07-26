package com.mycompany.movieservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.annotation.VaultPropertySource.Renewal;
import org.springframework.vault.authentication.AppRoleAuthentication;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import java.net.URI;

@Slf4j
@Configuration
@VaultPropertySource(value = "database/creds/movie-role", propertyNamePrefix = "database.", renewal = Renewal.ROTATE)
public class VaultConfig extends AbstractVaultConfiguration {

    @Override
    public VaultEndpoint vaultEndpoint() {
        String uri = getEnvironment().getProperty("vault.uri");
        if (uri == null) {
            throw new IllegalStateException();
        }
        return VaultEndpoint.from(URI.create(uri));
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        String roleId = getEnvironment().getProperty("vault.app-role.role-id");
        if (roleId == null) {
            throw new IllegalStateException();
        }
        AppRoleAuthenticationOptions options = AppRoleAuthenticationOptions.builder()
                .roleId(AppRoleAuthenticationOptions.RoleId.provided(roleId))
                .build();

        return new AppRoleAuthentication(options, restOperations());
    }

}
