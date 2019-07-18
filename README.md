# `springboot-vault-examples`

The goal of this project is play with [`Vault`](https://www.vaultproject.io). For it, we will implement some
applications that rely on `Vault` to store/retrieve secrets. The credentials to access databases are generated
dynamically by [`Vault`](https://www.vaultproject.io) that uses [`Consul`](https://www.consul.io) as backend.

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
./unseal-vault-enable-approle.sh
```

- Copy the environment variable shown in the end of the script execution and export it into every terminal it is needed.
```
export VAULT_ROOT_TOKEN=...
```

## Examples

### [# vault-approle-mysql](https://github.com/ivangfr/springboot-vault-examples/tree/master/vault-approle-mysql)

### [# vault-approle-cassandra](https://github.com/ivangfr/springboot-vault-examples/tree/master/vault-approle-cassandra)

## Shutdown

To stop and remove containers, networks and volumes
```
docker-compose down -v
```
