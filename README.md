# springboot-vault-examples

The goal of this project is to explore the capabilities of [`Vault`](https://www.vaultproject.io). To achieve this, we will develop applications that utilize `Vault` to store and retrieve secrets. `Vault` dynamically generates credentials for accessing databases and relies on [`Consul`](https://www.consul.io) as the backend. The authentication method employed in `Vault` is `AppRole`.

## Proof-of-Concepts & Articles

On [ivangfr.github.io](https://ivangfr.github.io), I have compiled my Proof-of-Concepts (PoCs) and articles. You can easily search for the technology you are interested in by using the filter. Who knows, perhaps I have already implemented a PoC or written an article about what you are looking for.

## Additional Readings

- \[**Medium**\] [**Using HashiCorp Vault & Spring Cloud Vault to handle Spring Boot App Key/Value Secrets**](https://medium.com/@ivangfr/using-hashicorp-vault-spring-cloud-vault-to-handle-spring-boot-app-key-value-secrets-926b81d0173b)
- \[**Medium**\] [**Using HashiCorp Vault & Spring Cloud Vault to obtain Dynamic MySQL Credentials**](https://medium.com/@ivangfr/using-hashicorp-vault-spring-cloud-vault-to-obtain-dynamic-mysql-credentials-5726f4fa53c2)
- \[**Medium**\] [**How to Rotate Expired Spring Cloud Vault Relational DB Credentials Without Restarting the App**](https://medium.com/@ivangfr/how-to-rotate-expired-spring-cloud-vault-relational-db-credentials-without-restarting-the-app-66976fbb4bbe)

## Lease Rotation

Many people encounter issues when using `Vault`, particularly with rotating the lease for backend databases. When a [`Spring Boot`](https://docs.spring.io/spring-boot/index.html) application requests a lease from `Vault` through the [`Spring Cloud Vault`](https://cloud.spring.io/spring-cloud-vault/reference/html/) library, the library **can automatically renew** the lease periodically (based on `default_lease_ttl`).

However, once the maximum lease expiration time (`max_lease_ttl`) is reached, the lease cannot be renewed, and a new lease is needed. In this case, the `Spring Cloud Vault` library **cannot rotate** the lease, which may leave the application unable to connect to the database.

To address this issue, we have developed solutions for applications using `Spring Cloud Vault` or [`Spring Vault`](https://docs.spring.io/spring-vault/reference/). Please see the examples below.

## Examples

| Example                                                                                                                                                                                  | Diagram                                                                             |
|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| [spring-cloud-vault-approle-mysql](https://github.com/ivangfr/springboot-vault-examples/tree/master/spring-cloud-vault-approle-mysql) **(with lease rotation)**                          | ![project-diagram](documentation/spring-cloud-vault-approle-mysql.jpeg)             |
| [spring-cloud-vault-approle-cassandra](https://github.com/ivangfr/springboot-vault-examples/tree/master/spring-cloud-vault-approle-cassandra)                                            | ![project-diagram](documentation/spring-cloud-vault-approle-cassandra.jpeg)         |
| [spring-vault-approle-mysql](https://github.com/ivangfr/springboot-vault-examples/tree/master/spring-vault-approle-mysql) **(with lease rotation)**                                      | ![project-diagram](documentation/spring-vault-approle-mysql.jpeg)                   |
| [spring-vault-approle-multi-datasources-mysql](https://github.com/ivangfr/springboot-vault-examples/tree/master/spring-vault-approle-multi-datasources-mysql) **(with lease rotation)**  | ![project-diagram](documentation/spring-vault-approle-multi-datasources-mysql.jpeg) |

## Prerequisites

- [`Java 21`](https://www.oracle.com/java/technologies/downloads/#java21) or higher.
- A containerization tool (e.g., [`Docker`](https://www.docker.com), [`Podman`](https://podman.io), etc.)

## Initialize Environment

Open a terminal and, inside the `springboot-vault-examples` root folder, run the following script:
```bash
./init-environment.sh
```

This script will:
- start `Consul`, `Vault`, `MySQL`, and `Cassandra` Docker containers.
- unseal `Vault` and enable `AppRole` in it.
- set up Database `roles` and `policies` in `Vault` for the applications so that they can access their databases using dynamically generated credentials.
- set up `KV Secrets` in `Vault` for the application.

## Shutdown Environment

To shut down the environment, go to a terminal and, inside the `springboot-vault-examples` root folder, run the script below:
```bash
./shutdown-environment.sh
```

## Cleanup

To remove all Docker images created by this project, go to a terminal and, inside the `springboot-vault-examples` root folder, run the following script:
```bash
./remove-docker-images.sh all
```
