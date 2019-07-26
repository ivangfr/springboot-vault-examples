# `spring-vault-approle-mysql`

- [Spring-Boot](https://spring.io/projects/spring-boot) application that manages students, called `movie-service`.
- It uses [`MySQL`](https://www.mysql.com/) database as storage.
- It uses [`Spring Vault`](https://docs.spring.io/spring-vault/docs/2.1.3.RELEASE/reference/html/#_document_structure)
- Credentials to access `MySQL` is generated dynamically by [`Vault`](https://www.vaultproject.io).
- `AppRole` is the `Vault` authentication method used.
- `Role Id` generated automatically by `Vault`.

**Note. before running this example, all the steps described at "Start Environment" in the main README should be
executed previously.**

## Setup Vault-MySQL

Open one terminal and inside `springboot-vault-examples` root folder run
```
./setup-spring-vault-approle-mysql.sh
```

The `ROLE_ID` is printed in the end of the script execution. Export it in the terminal that you will start `movie-service`.

## Start student-service

### Running with Maven Wrapper

Inside `springboot-vault-examples` root folder, run the following command
```
./mvnw spring-boot:run --projects spring-vault-approle-mysql/movie-service -Dspring-boot.run.jvmArguments="-Dserver.port=9082"
```

### Running as Docker Container

- Go to the `springboot-vault-examples` root folder and build the docker image
```
./mvnw package dockerfile:build -DskipTests --projects spring-vault-approle-mysql/movie-service
```
| Environment Variable | Description                                                              |
| -------------------- | ------------------------------------------------------------------------ |
| `ROLE_ID`            | Specify the role id generated while running setup script                 |
| `DATABASE_ROLE`      | Specify the database role used by the application (default `movie-role`) |
| `VAULT_HOST`         | Specify host of the `Vault` to use (default `vault`)                     |
| `VAULT_PORT`         | Specify port of the `Vault` to use (default `8200`)                      |
| `CONSUL_HOST`        | Specify host of the `Consul` to use (default `consul`)                   |
| `CONSUL_PORT`        | Specify port of the `Consul` to use (default `8500`)                     |
| `MYSQL_HOST`         | Specify host of the `MySQL` to use (default `mysql`)                     |
| `MYSQL_PORT`         | Specify port of the `MySQL` to use (default `3306`)                      |

- Run the docker container
```
docker run -d --rm \
  --name movie-service \
  --network springboot-vault-examples_default \
  -p 9082:8080 \
  -e ROLE_ID=$ROLE_ID \
  docker.mycompany.com/movie-service:1.0.0
```
> Note. the command uses the default network created by docker-compose, `springboot-vault-examples_default`.
>
> In order to stop `movie-service` docker container, run
> ```
> docker stop movie-service 
> ```

## Using movie-service

You can access `movie-service` Swagger website at: http://localhost:9082/swagger-ui.html

## Useful Links/Commands

### Vault

**Note. In order to run some commands, you must have [`jq`](https://stedolan.github.io/jq) installed on you machine**

1. List of active leases for `database/creds/movie-role`
```
curl -s -X LIST \
  -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
  http://localhost:8200/v1/sys/leases/lookup/database/creds/movie-role | jq .
```

The response will be something like
```
{
  "request_id": "5dbe3871-fc4d-a727-d77f-a4c9495e4d1a",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "keys": [
      "CCV6h8U59TuGNNrvSUPNndYh"
    ]
  },
  "wrap_info": null,
  "warnings": null,
  "auth": null
}
```

2. See specific lease metadata
```
curl -s -X PUT \
  -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
  -d '{ "lease_id": "database/creds/movie-role/CCV6h8U59TuGNNrvSUPNndYh" }' \
  http://localhost:8200/v1/sys/leases/lookup | jq .
```

The response will be something like
```
{
  "request_id": "38c6c6fb-74f1-dd31-e89e-7e8a00f0f1f4",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "expire_time": "2019-07-26T20:19:04.1710843Z",
    "id": "database/creds/movie-role/CCV6h8U59TuGNNrvSUPNndYh",
    "issue_time": "2019-07-26T20:17:04.1710687Z",
    "last_renewal": null,
    "renewable": true,
    "ttl": 66
  },
  "wrap_info": null,
  "warnings": null,
  "auth": null
}
```

### MySQL

- Connect to `MySQL` inside docker container
```
docker exec -it mysql mysql -uroot -psecret
```

- List users
```
SELECT User, Host, password_expired, password_last_changed, account_locked FROM mysql.user;
```

- Show running process
```
SELECT * FROM information_schema.processlist;
```

- Log all queries
```
SET GLOBAL general_log = 'ON';
SET global log_output = 'table';

SELECT event_time, SUBSTRING(user_host,1,20) as user_host, thread_id, command_type, SUBSTRING(argument,1,70) FROM mysql.general_log WHERE user_host LIKE 'v-approle-movie-role-%';
```

- Create/Remove user
```
CREATE USER 'newuser'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON * . * TO 'newuser'@'%';

DROP USER 'newuser'@'%';
```
