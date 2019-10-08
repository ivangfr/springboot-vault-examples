# `spring-cloud-vault-approle-cassandra`

- [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) application that manages
`books`, called `book-service`
- It uses [`Cassandra`](https://cassandra.apache.org/) database as storage
- It uses [`Spring Cloud Vault`](https://cloud.spring.io/spring-cloud-vault/spring-cloud-vault.html)
- Credentials to access `Cassandra` is generated dynamically by [`Vault`](https://www.vaultproject.io)
- **Leases are renewed but NOT rotated**
- `AppRole` is the `Vault` authentication method used

> Note. before running this example, all the steps described at "Start Environment" in the main README should be
executed previously.

## Setup Vault-Cassandra

Open one terminal and inside `springboot-vault-examples` root folder run
```
./setup-spring-cloud-vault-approle-cassandra.sh
```

## Start book-service

### Running with Maven Wrapper

Inside `springboot-vault-examples` root folder, run the following command
```
./mvnw spring-boot:run --projects spring-cloud-vault-approle-cassandra/book-service -Dspring-boot.run.jvmArguments="-Dserver.port=9081"
```

### Running as Docker Container

Go to the `springboot-vault-examples` root folder and build the docker image
```
./mvnw package dockerfile:build -DskipTests --projects spring-cloud-vault-approle-cassandra/book-service
```
| Environment Variable | Description                                                  |
| -------------------- | -------------------------------------------------------------|
| `VAULT_HOST`         | Specify host of the `Vault` to use (default `vault`)         |
| `VAULT_PORT`         | Specify port of the `Vault` to use (default `8200`)          |
| `CONSUL_HOST`        | Specify host of the `Consul` to use (default `consul`)       |
| `CONSUL_PORT`        | Specify port of the `Consul` to use (default `8500`)         |
| `CASSANDRA_HOST`     | Specify host of the `Cassandra` to use (default `cassandra`) |
| `CASSANDRA_PORT`     | Specify port of the `Cassandra` to use (default `9042`)      |

Run the docker container
```
docker run -d --rm \
  --name book-service \
  --network springboot-vault-examples_default \
  -p 9081:8080 \
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

> Note. In order to run some commands, you must have [`jq`](https://stedolan.github.io/jq) installed on you machine

1. List of active leases for `database/creds/book-role`
```
curl -s -X LIST \
  -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
  http://localhost:8200/v1/sys/leases/lookup/database/creds/book-role | jq .
```

The response will be something like
```
{
  "request_id": "c56d60d7-6792-7822-3e4d-b51ef8bea4ce",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "keys": [
      "AsQLBqfNGNNNeeEFp2c6sQs9"
    ]
  },
  "wrap_info": null,
  "warnings": null,
  "auth": null
}
```

2. See specific lease metadata
```
`curl -s -X PUT \
  -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
  -d '{ "lease_id": "database/creds/book-role/AsQLBqfNGNNNeeEFp2c6sQs9" }' \
  http://localhost:8200/v1/sys/leases/lookup | jq .`
```

The response will be something like
```
{
  "request_id": "4b7ee3f4-f891-f34f-5d5c-22ef2600fb55",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "expire_time": "2019-07-26T20:23:21.5935893Z",
    "id": "database/creds/book-role/AsQLBqfNGNNNeeEFp2c6sQs9",
    "issue_time": "2019-07-26T20:21:21.5935713Z",
    "last_renewal": null,
    "renewable": true,
    "ttl": 70
  },
  "wrap_info": null,
  "warnings": null,
  "auth": null
}
```

### Cassandra

- Connect to `Cassandra` inside docker container and list books
```
docker exec -it cassandra cqlsh -ucassandra -pcassandra
USE mycompany;
SELECT * FROM books;
```

- List users
```
LIST USERS;
```

### Consul

Consul can be accessed at http://localhost:8500
