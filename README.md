# `springboot-vault-examples`

The goal of this project is play with [`Vault`](https://www.vaultproject.io). For it, we will implement some
examples. The credentials to access databases are generated dynamically by [`Vault`](https://www.vaultproject.io)
that uses [`Consul`](https://www.consul.io) as backend. `AppRole` is the `Vault` authentication method used. 

## Start Environment

- Open one terminal and inside `springboot-mysql-vault` root folder run
```
docker-compose up -d
```

- Wait a little bit until the containers are `Up (healthy)`. In order to check the status of the containers run the command
```
docker-compose ps
```

- Unseal `Vault` by running the script below
```
./unseal-vault.sh
```

- Copy the environment variables shown at the end of the script execution and export them into every terminal you
open to run the examples of this project.
```
export VAULT_ROOT_TOKEN=...
export ROLE_ID=...
```

## Examples

### [# springboot-vault-mysql](https://github.com/ivangfr/springboot-vault-examples/tree/master/stringboot-vault-mysql)

## Shutdown

To stop and remove containers, networks and volumes
```
docker-compose down -v
```
