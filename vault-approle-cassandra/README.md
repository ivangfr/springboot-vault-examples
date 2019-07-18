# `vault-approle-cassandra`

- [Spring-Boot](https://spring.io/projects/spring-boot) application that manages books, called `book-service`.
- It uses [`Cassandra`](https://cassandra.apache.org/) database as storage.
- Credentials to access `Cassandra` is generated dynamically by [`Vault`](https://www.vaultproject.io).
- `AppRole` is the `Vault` authentication method used.
- `Role Id` generated automatically by `Vault`.

**Note. before running this example, all the steps described at "Start Environment" in the main README should be
executed previously.**

## Setup Vault-Cassandra

Open one terminal and inside `springboot-vault-examples` root folder run
```
./setup-vault-cassandra.sh
```

The `ROLE_ID` is printed in the end of the script execution. Export it in the terminal that you will start `book-service`.

## Start book-service

### Running with Maven Wrapper

Inside `springboot-vault-examples` root folder, run the following command
```
./mvnw spring-boot:run --projects vault-approle-cassandra/book-service -Dspring-boot.run.jvmArguments="-Dserver.port=9081"
```

### Running as Docker Container

- Go to the `springboot-vault-examples` root folder and build the docker image
```
./mvnw package dockerfile:build -DskipTests --projects vault-approle-cassandra/book-service
```
| Environment Variable | Description                                                              |
| -------------------- | ------------------------------------------------------------------------ |
| `ROLE_ID`            | Specify the role id generated while running `./setup-vault-cassandra.sh` |
| `DATABASE_ROLE`      | Specify the database role used by the application (default `bookdb`)     |
| `VAULT_HOST`         | Specify host of the `Vault` to use (default `vault`)                     |
| `VAULT_PORT`         | Specify port of the `Vault` to use (default `8200`)                      |
| `CONSUL_HOST`        | Specify host of the `Consul` to use (default `consul`)                   |
| `CONSUL_PORT`        | Specify port of the `Consul` to use (default `8500`)                     |
| `CASSANDRA_HOST`     | Specify host of the `Cassandra` to use (default `cassandra`)             |
| `CASSANDRA_PORT`     | Specify port of the `Cassandra` to use (default `9042`)                  |

- Run the docker container
```
docker run -d --rm \
  --name book-service \
  --network springboot-vault-examples_default \
  -p 9081:8080 \
  -e ROLE_ID=$ROLE_ID \
  docker.mycompany.com/book-service:1.0.0
```
> Note. the command uses the default network created by docker-compose, `springboot-vault-examples_default`.
>
> In order to stop `book-service` docker container, run
> ```
> docker stop book-service 
> ```

## Using book-service

You can access `book-service` Swagger website at: http://localhost:9081/swagger-ui.html

## Useful Links/Commands

### Vault

**Note. In order to run some commands, you must have [`jq`](https://stedolan.github.io/jq) installed on you machine**

- List of active leases for `database/creds/bookdb`
```
curl -s -X LIST \
  -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
  http://localhost:8200/v1/sys/leases/lookup/database/creds/bookdb | jq .
```

The response will be something like
```
{
  "request_id": "14667c30-69e8-e199-b0d3-53f68256f526",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "keys": [
      "s02QwYLxySPrqLrzezWzFqqh"
    ]
  },
  "wrap_info": null,
  "warnings": null,
  "auth": null
}
```

- See specific lease metadata
```
curl -s -X PUT \
  -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
  -d '{ "lease_id": "database/creds/bookdb/s02QwYLxySPrqLrzezWzFqqh" }' \
  http://localhost:8200/v1/sys/leases/lookup | jq .
```

The response will be something like
```
{
  "request_id": "2e371181-f672-9776-b638-c269b6a8c494",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "expire_time": "2019-07-17T20:23:31.6899642Z",
    "id": "database/creds/bookdb/s02QwYLxySPrqLrzezWzFqqh",
    "issue_time": "2019-07-17T20:18:31.6899242Z",
    "last_renewal": null,
    "renewable": true,
    "ttl": 81
  },
  "wrap_info": null,
  "warnings": null,
  "auth": null
}
```

### Cassandra

Connect to `Cassandra` inside docker container and list books
```
docker exec -it cassandra cqlsh -ucassandra -pcassandra
USE mycompany;
SELECT * FROM books;
```

### Consul

Consul can be accessed at http://localhost:8500
