# springboot-vault-examples
## `> spring-vault-approle-mysql`

## Application

- **movie-service**

  - [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) application that manages `movies`
  - It uses [`MySQL`](https://www.mysql.com/) database as storage
  - It uses [`Spring Vault`](https://docs.spring.io/spring-vault/docs/2.1.3.RELEASE/reference/html/#_document_structure)
  - It uses [`Hikari`](https://github.com/brettwooldridge/HikariCP) JDBC connection pool
  - Credentials to access `MySQL` is generated dynamically by [`Vault`](https://www.vaultproject.io)
  - **Leases are renewed and rotated**
  - `AppRole` is the `Vault` authentication method used

## Prerequisite

Before running this example, all the steps described at [Start Environment](https://github.com/ivangfr/springboot-vault-examples#start-environment) section in the main README should be previously executed.

## Setup Vault-MySQL

- Open a terminal and make sure you are inside `springboot-vault-examples` root folder

- Run the following script
  ```
  ./setup-spring-vault-approle-mysql.sh
  ```

## Start movie-service

### Running with Maven Wrapper

- In a terminal, make sure you are inside `springboot-vault-examples` root folder

- Run the following command
  ```
  ./mvnw clean spring-boot:run \
    --projects spring-vault-approle-mysql/movie-service \
    -Dspring-boot.run.jvmArguments="-Dserver.port=9082"
  ```

### Running as Docker Container

- Go to the `springboot-vault-examples` root folder and build the docker image
  ```
  ./mvnw clean compile jib:dockerBuild -DskipTests --projects spring-vault-approle-mysql/movie-service
  ```
  | Environment Variable | Description                                               |
  | -------------------- | --------------------------------------------------------- |
  | `VAULT_HOST`         | Specify host of the `Vault` to use (default `localhost`)  |
  | `VAULT_PORT`         | Specify port of the `Vault` to use (default `8200`)       |
  | `CONSUL_HOST`        | Specify host of the `Consul` to use (default `localhost`) |
  | `CONSUL_PORT`        | Specify port of the `Consul` to use (default `8500`)      |
  | `MYSQL_HOST`         | Specify host of the `MySQL` to use (default `localhost`)  |
  | `MYSQL_PORT`         | Specify port of the `MySQL` to use (default `3306`)       |

- Run the docker container
  ```
  docker run -d --rm --name movie-service -p 9082:8080 \
    -e VAULT_HOST=vault -e CONSUL_HOST=consul -e MYSQL_HOST=mysql \
    --network springboot-vault-examples_default \
    docker.mycompany.com/movie-service:1.0.0
  ```

## Using movie-service

You can access `movie-service` Swagger website at: http://localhost:9082/swagger-ui.html

## Useful Links & Commands

- **Vault**

  > **Note:** In order to run some commands, you must have [`jq`](https://stedolan.github.io/jq) installed on you machine

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

  1. See specific lease metadata
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

- **MySQL**

  - Connect to `MySQL` inside docker container
    ```
    docker exec -it mysql mysql -uroot -psecret
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

- **Consul**

  `Consul` can be accessed at http://localhost:8500

## Shutdown

- Stop application
  - If the application was started with `Maven`, go to the terminals where it is running and press `Ctrl+C`
  - If the application was started as a Docker container, run the command below
    ```
    docker stop movie-service
    ```
    
- Stop docker-compose containers by following the instruction in [Shutdown](https://github.com/ivangfr/springboot-vault-examples#shutdown) section in the main README.