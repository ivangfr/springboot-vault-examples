# `springboot-vault-examples`

The goal of this project is play with [`Vault`](https://www.vaultproject.io). For it, we will implement some
applications that rely on `Vault` to store/retrieve secrets. The credentials to access databases are generated
dynamically by [`Vault`](https://www.vaultproject.io) that uses [`Consul`](https://www.consul.io) as backend.

## Lease Rotation

One of the problem faced by many people when using `Vault` is about rotating the lease obtained for some backend database.
When a `Spring-Boot` application requests a lease for `Vault` using, for instance, the library [`Spring Cloud Vault`](https://cloud.spring.io/spring-cloud-vault/spring-cloud-vault.html),
the library itself is able to automatically renew the lease from time to time (`default_lease_ttl`). However, when
the maximum expiration time of a lease is reached (`max_lease_ttl`), it means that the lease cannot be renewed anymore
and a new lease is required. In this situation, `Spring Cloud Vault` library cannot rotated it, leaving the application
without connection to database. In order to solve this problem, I have implemented some solutions for applications that
uses `Spring Cloud Vault` or [`Spring Vault`](https://docs.spring.io/spring-vault/docs/2.1.3.RELEASE/reference/html/#_document_structure).
Please, have a look at the examples below.  

## Start Environment

- Open one terminal and inside `springboot-vault-examples` root folder run
```
docker-compose up -d
```

- Wait a little bit until the containers are `Up (healthy)`. In order to check the status of the containers run the command
```
docker-compose ps
```

- Unseal `Vault` and enable `AppRole` by running the script below
```
./unseal-vault-enable-approle-databases.sh
```

- Copy the environment variable shown in the end of the script execution and export it into every terminal it is needed.
```
export VAULT_ROOT_TOKEN=...
```

## Examples

### [# spring-cloud-vault-approle-mysql](https://github.com/ivangfr/springboot-vault-examples/tree/master/spring-cloud-vault-approle-mysql) **(with lease rotation)**

### [# spring-cloud-vault-approle-cassandra](https://github.com/ivangfr/springboot-vault-examples/tree/master/spring-cloud-vault-approle-cassandra)

### [# spring-vault-approle-mysql](https://github.com/ivangfr/springboot-vault-examples/tree/master/spring-vault-approle-mysql) **(with lease rotation)**

## Shutdown

To stop and remove containers, networks and volumes
```
docker-compose down -v
```

## References

- https://github.com/spring-cloud/spring-cloud-vault/issues/85
