# springboot-vault-examples

The goal of this project is play with [`Vault`](https://www.vaultproject.io). For it, we will implement some applications that rely on `Vault` to store/retrieve secrets. The credentials to access databases are generated dynamically by [`Vault`](https://www.vaultproject.io) that uses [`Consul`](https://www.consul.io) as backend.

## Lease Rotation

One of the problems faced by many people when using `Vault` is about rotating the lease obtained for some backend databases. When a [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) application requests a lease from `Vault` using, for instance, the library [`Spring Cloud Vault`](https://cloud.spring.io/spring-cloud-vault/spring-cloud-vault.html), the library itself **is able to automatically renew** the lease from time to time (`default_lease_ttl`).

However, when the maximum expiration time of a lease is reached (`max_lease_ttl`), it means that the lease cannot be renewed anymore, and a new lease is required. In this situation, `Spring Cloud Vault` library **cannot rotate** it, leaving the application without connection to database.

In order to solve this problem, I have implemented some solutions for applications that use `Spring Cloud Vault` or [`Spring Vault`](https://docs.spring.io/spring-vault/docs/2.1.3.RELEASE/reference/html/#_document_structure). Please, have a look at the examples below.  

## Examples

- ### [spring-cloud-vault-approle-mysql](https://github.com/ivangfr/springboot-vault-examples/tree/master/spring-cloud-vault-approle-mysql#springboot-vault-examples) **(with lease rotation)**
- ### [spring-cloud-vault-approle-cassandra](https://github.com/ivangfr/springboot-vault-examples/tree/master/spring-cloud-vault-approle-cassandra#springboot-vault-examples)
- ### [spring-vault-approle-mysql](https://github.com/ivangfr/springboot-vault-examples/tree/master/spring-vault-approle-mysql#springboot-vault-examples) **(with lease rotation)**
- ### [spring-vault-approle-multi-datasources-mysql](https://github.com/ivangfr/springboot-vault-examples/tree/master/spring-vault-approle-multi-datasources-mysql#springboot-vault-examples) **(with lease rotation)**

## Prerequisites

- [`Java 11+`](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [`Docker`](https://www.docker.com/)
- [`Docker-Compose`](https://docs.docker.com/compose/install/)

## Start Environment

- Open one terminal and inside `springboot-vault-examples` root folder run
  ```
  docker-compose up -d
  ```

- Wait a bit until the containers are `Up (healthy)`. In order to check the status of the containers run
  ```
  docker-compose ps
  ```

- Once all containers are up and running, run the script below to unseal `Vault` and enable `AppRole`
  ```
  ./unseal-vault-enable-approle-databases.sh
  ```

- At the end of the script execution, the `VAULT_ROOT_TOKEN` value will be shown. Export it into every terminal it's needed
  ```
  export VAULT_ROOT_TOKEN=...
  ```

## Shutdown

To stop and remove docker-compose containers, network and volumes, go to a terminal and, inside `springboot-vault-examples` root folder, run the command below
```
docker-compose down -v
```

## Cleanup

To remove the Docker images created by this project, go to a terminal and, inside `springboot-vault-examples` root folder, run the following script
```
./remove-docker-images.sh
```

## References

- https://github.com/spring-cloud/spring-cloud-vault/issues/85
- https://blog.ruanbekker.com/blog/2019/05/07/use-the-vault-api-to-provision-app-keys-and-create-kv-pairs/
