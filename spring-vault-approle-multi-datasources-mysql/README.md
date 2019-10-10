# `spring-vault-approle-multi-datasources-mysql`

- [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) application that manages `dishes`
and `customers`, called `restaurant-service`
- It uses [`MySQL`](https://www.mysql.com/) database as storage
- It connects to two `MySQL` instances. One stores `dishes` information and another `customers`
- It uses [`Spring Vault`](https://docs.spring.io/spring-vault/docs/2.1.3.RELEASE/reference/html/#_document_structure)
- It uses [`Hikari`](https://github.com/brettwooldridge/HikariCP) JDBC connection pool
- Credentials to access `MySQL` is generated dynamically by [`Vault`](https://www.vaultproject.io)
- **Leases are renewed and rotated**
- `AppRole` is the `Vault` authentication method used

> Note. before running this example, all the steps described at "Start Environment" in the main README should be
executed previously.

## Setup Vault-MySQL

Open one terminal and inside `springboot-vault-examples` root folder run
```
./setup-spring-vault-approle-multi-datasources-mysql.sh
```

## Start restaurant-service

### Running with Maven Wrapper

Inside `springboot-vault-examples` root folder, run the following command
```
./mvnw spring-boot:run --projects spring-vault-approle-multi-datasources-mysql/restaurant-service -Dspring-boot.run.jvmArguments="-Dserver.port=9083"
```

### Running as Docker Container

- Go to the `springboot-vault-examples` root folder and build the docker image
```
./mvnw package dockerfile:build -DskipTests --projects spring-vault-approle-multi-datasources-mysql/restaurant-service
```
| Environment Variable  | Description                                              |
| --------------------- | ---------------------------------------------------------|
| `VAULT_HOST`          | Specify host of the `Vault` to use (default `vault`)     |
| `VAULT_PORT`          | Specify port of the `Vault` to use (default `8200`)      |
| `CONSUL_HOST`         | Specify host of the `Consul` to use (default `consul`)   |
| `CONSUL_PORT`         | Specify port of the `Consul` to use (default `8500`)     |
| `CUSTOMER_MYSQL_HOST` | Specify host of the `MySQL` to use (default `mysql`)     |
| `CUSTOMER_MYSQL_PORT` | Specify port of the `MySQL` to use (default `3306`)      |
| `DISH_MYSQL_HOST`     | Specify host of the `MySQL` to use (default `mysql-2`)   |
| `DISH_MYSQL_PORT`     | Specify port of the `MySQL` to use (default `3307`)      |

- Run the docker container
```
docker run -d --rm \
  --name restaurant-service \
  --network springboot-vault-examples_default \
  -p 9083:8080 \
  docker.mycompany.com/restaurant-service:1.0.0
```
> Note. the command uses the default network created by docker-compose, `springboot-vault-examples_default`.
>
> In order to stop `restaurant-service` docker container, run
> ```
> docker stop restaurant-service 
> ```

## Using restaurant-service

You can access `restaurant-service` Swagger website at: http://localhost:9083/swagger-ui.html

## Useful Links/Commands

### Vault

> Note. In order to run some commands, you must have [`jq`](https://stedolan.github.io/jq) installed on you machine

1. List of active leases for `database/creds/customer-role` (the same can be done for `database/creds/dish-role`)
```
curl -s -X LIST \
  -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
  http://localhost:8200/v1/sys/leases/lookup/database/creds/customer-role | jq .
```

The response will be something like
```
{
  "request_id": "16d2ec27-bb75-29a4-a01d-489c4f7a2a34",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "keys": [
      "4pUp7cHODXpdtsXadzgsL5aZ"
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
  -d '{ "lease_id": "database/creds/customer-role/4pUp7cHODXpdtsXadzgsL5aZ" }' \
  http://localhost:8200/v1/sys/leases/lookup | jq .
```

The response will be something like
```
{
  "request_id": "c020ab73-84db-406f-2dac-d169d7f2db51",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "expire_time": "2019-10-05T22:22:32.4210957Z",
    "id": "database/creds/customer-role/4pUp7cHODXpdtsXadzgsL5aZ",
    "issue_time": "2019-10-05T22:19:32.3713442Z",
    "last_renewal": "2019-10-05T22:20:32.4211219Z",
    "renewable": true,
    "ttl": 78
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
-- OR --
docker exec -it mysql-2 mysql -uroot -psecret
```

- List users
```
SELECT User, Host FROM mysql.user;
```

- Show running process
```
SELECT * FROM information_schema.processlist ORDER BY user;
```

- Log all queries
```
SET GLOBAL general_log = 'ON';
SET global log_output = 'table';

SELECT event_time, SUBSTRING(user_host,1,20) as user_host, thread_id, command_type, SUBSTRING(argument,1,70) FROM mysql.general_log WHERE user_host LIKE 'v-approle-movie-%';
```

- Create/Remove user
```
CREATE USER 'newuser'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON * . * TO 'newuser'@'%';

DROP USER 'newuser'@'%';
```

### Consul

Consul can be accessed at http://localhost:8500