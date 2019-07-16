# `springboot-vault-mysql`

The goal of this project is to implement a [Spring-Boot](https://spring.io/projects/spring-boot) application that
manage students, called `student-service`. `student-service` uses [`MySQL`](https://www.mysql.com/) database as storage.
The credentials to access `MySQL` is generated dynamically by [`Vault`](https://www.vaultproject.io).

## Setup Vault-MySQL

> Note: before running the script below, the services present in `docker-compose.yml` file must be up and running and
> `Vault` must be unsealed as explained in the main `README`.

Open one terminal and inside `springboot-mysql-vault` root folder run
```
./setup-vault-mysql.sh
```

## Start student-service

### Running with Maven Wrapper

Inside `springboot-mysql-vault` root folder, run the following command
```
./mvnw clean spring-boot:run --projects springboot-vault-mysql/student-service
```

### Running as Docker Container

- Go to the `springboot-mysql-vault` root folder and build the docker image
```
./mvnw clean package dockerfile:build -DskipTests --projects springboot-vault-mysql/student-service
```
| Environment Variable | Description                                                             |
| -------------------- | ----------------------------------------------------------------------- |
| `ROLE_ID`            | Specify the role id generated while unsealing `Vault`                   |
| `DATABASE_ROLE`      | Specify the database role used by the application (default `studentdb`) |
| `VAULT_ADDR`         | Specify host of the `Vault` to use (default `vault`)                    |
| `VAULT_PORT`         | Specify port of the `Vault` to use (default `8200`)                     |
| `CONSUL_ADDR`        | Specify host of the `Consul` to use (default `consul`)                  |
| `CONSUL_PORT`        | Specify port of the `Consul` to use (default `8500`)                    |
| `MYSQL_ADDR`         | Specify host of the `MySQL` to use (default `mysql`)                    |
| `MYSQL_PORT`         | Specify port of the `MySQL` to use (default `3306`)                     |

- Run the docker container
```
docker run -d --rm \
  --name student-service \
  --network springboot-vault-examples_default \
  -p 8080:8080 \
  -e ROLE_ID=$ROLE_ID \
  docker.mycompany.com/student-service:1.0.0
```
> Note. the command uses the default network created by docker-compose, `springboot-vault-examples_default`.
>
> In order to stop `student-service` docker container, run
> ```
> docker stop student-service 
> ```

## Using student-service

You can access `student-service` Swagger website at: http://localhost:8080/swagger-ui.html

## Useful Links/Commands

### Vault

***Note. In order to run some commands, you must have [`jq`](https://stedolan.github.io/jq) installed on you machine***

- List of active leases for `database/creds/studentdb`
```
curl -s -X LIST \
  -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
  http://localhost:8200/v1/sys/leases/lookup/database/creds/studentdb | jq .
```

The response will be something like
```
{
  "request_id": "2d84638d-1093-b70c-3beb-231dc06ad09e",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "keys": [
      "BnxvLgVP9ocRwghEr2Xm5Yn1"
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
  -d '{ "lease_id": "database/creds/studentdb/BnxvLgVP9ocRwghEr2Xm5Yn1" }' \
  http://localhost:8200/v1/sys/leases/lookup | jq .
```

The response will be something like
```
{
  "request_id": "4ea6625c-474d-87d4-b467-6955fa119216",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "expire_time": "2019-07-16T21:49:55.1350002Z",
    "id": "database/creds/studentdb/BnxvLgVP9ocRwghEr2Xm5Yn1",
    "issue_time": "2019-07-16T21:44:55.1349843Z",
    "last_renewal": null,
    "renewable": true,
    "ttl": 214
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

SELECT event_time, SUBSTRING(user_host,1,20) as user_host, thread_id, command_type, SUBSTRING(argument,1,70) FROM mysql.general_log WHERE user_host LIKE 'v-approle-studentdb-%';
```

- Create/Remove user
```
CREATE USER 'newuser'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON * . * TO 'newuser'@'%';

DROP USER 'newuser'@'%';
```

## Shutdown

To stop and remove containers, networks and volumes
```
docker-compose down -v
```