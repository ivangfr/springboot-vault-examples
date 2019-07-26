# `spring-cloud-vault-approle-mysql`

- [Spring-Boot](https://spring.io/projects/spring-boot) application that manages students, called `student-service`.
- It uses [`MySQL`](https://www.mysql.com/) database as storage.
- It uses [`Spring Cloud Vault`](https://cloud.spring.io/spring-cloud-vault/spring-cloud-vault.html)
- Credentials to access `MySQL` is generated dynamically by [`Vault`](https://www.vaultproject.io).
- `AppRole` is the `Vault` authentication method used.
- `Role Id` generated automatically by `Vault`.

**Note. before running this example, all the steps described at "Start Environment" in the main README should be
executed previously.**

## Setup Vault-MySQL

Open one terminal and inside `springboot-vault-examples` root folder run
```
./setup-spring-cloud-vault-approle-mysql.sh
```

The `ROLE_ID` is printed in the end of the script execution. Export it in the terminal that you will start `student-service`.

## Start student-service

### Running with Maven Wrapper

Inside `springboot-vault-examples` root folder, run the following command
```
./mvnw spring-boot:run --projects spring-cloud-vault-approle-mysql/student-service -Dspring-boot.run.jvmArguments="-Dserver.port=9080"
```

### Running as Docker Container

Go to the `springboot-vault-examples` root folder and build the docker image
```
./mvnw package dockerfile:build -DskipTests --projects spring-cloud-vault-approle-mysql/student-service
```
| Environment Variable | Description                                                                  |
| -------------------- | ---------------------------------------------------------------------------- |
| `ROLE_ID`            | Specify the role id generated while running setup script                     |
| `DATABASE_ROLE`      | Specify the database role used by the application (default `student-role`)   |
| `VAULT_HOST`         | Specify host of the `Vault` to use (default `vault`)                         |
| `VAULT_PORT`         | Specify port of the `Vault` to use (default `8200`)                          |
| `CONSUL_HOST`        | Specify host of the `Consul` to use (default `consul`)                       |
| `CONSUL_PORT`        | Specify port of the `Consul` to use (default `8500`)                         |
| `MYSQL_HOST`         | Specify host of the `MySQL` to use (default `mysql`)                         |
| `MYSQL_PORT`         | Specify port of the `MySQL` to use (default `3306`)                          |

Run the docker container
```
docker run -d --rm \
  --name student-service \
  --network springboot-vault-examples_default \
  -p 9080:8080 \
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

You can access `student-service` Swagger website at: http://localhost:9080/swagger-ui.html

## Useful Links/Commands

### Vault

**Note. In order to run some commands, you must have [`jq`](https://stedolan.github.io/jq) installed on you machine**

1. List of active leases for `database/creds/student-role`
```
curl -s -X LIST \
  -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
  http://localhost:8200/v1/sys/leases/lookup/database/creds/student-role | jq .
```

The response will be something like
```
{
  "request_id": "ef6db810-2431-3d86-41fe-edd72b01356c",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "keys": [
      "2RXJje2dzEOgClBVAO4O9dbx"
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
  -d '{ "lease_id": "database/creds/student-role/2RXJje2dzEOgClBVAO4O9dbx" }' \
  http://localhost:8200/v1/sys/leases/lookup | jq .
```

The response will be something like
```
{
  "request_id": "51c08c58-4151-50e7-a757-f9e7f23addb5",
  "lease_id": "",
  "renewable": false,
  "lease_duration": 0,
  "data": {
    "expire_time": "2019-07-26T20:20:41.2821972Z",
    "id": "database/creds/student-role/2RXJje2dzEOgClBVAO4O9dbx",
    "issue_time": "2019-07-26T20:18:41.2821799Z",
    "last_renewal": null,
    "renewable": true,
    "ttl": 28
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

SELECT event_time, SUBSTRING(user_host,1,20) as user_host, thread_id, command_type, SUBSTRING(argument,1,70) FROM mysql.general_log WHERE user_host LIKE 'v-approle-student-role-%';
```

- Create/Remove user
```
CREATE USER 'newuser'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON * . * TO 'newuser'@'%';

DROP USER 'newuser'@'%';
```

### Consul

Consul can be accessed at http://localhost:8500
