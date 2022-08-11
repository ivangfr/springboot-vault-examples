package com.ivanfranchin.movieservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.annotation.VaultPropertySource.Renewal;
import org.springframework.vault.authentication.AppRoleAuthentication;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import java.net.URI;

@Configuration
@VaultPropertySource(
        value = "${datasource.vault-creds-path}",
        propertyNamePrefix = "datasource.",
        renewal = Renewal.ROTATE)
@VaultPropertySource(
        value = "${app.vault-kv-secret-path}",
        propertyNamePrefix = "secret.movie-service.")
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
