# springboot-vault-examples
## `> spring-vault-approle-mysql`

## Project Diagram

![project-diagram](../documentation/spring-vault-approle-mysql.jpeg)

## Application

- ### movie-service

  - [`Spring Boot`](https://docs.spring.io/spring-boot/index.html) application that manages `movies`
  - It uses [`MySQL`](https://www.mysql.com/) database as storage
  - It uses [`Spring Vault`](https://docs.spring.io/spring-vault/reference/)
  - It uses [`Hikari`](https://github.com/brettwooldridge/HikariCP) JDBC connection pool
  - Credentials to access `MySQL` is generated dynamically by [`Vault`](https://www.vaultproject.io)
  - **Leases are renewed and rotated**
  - `AppRole` is the `Vault` authentication method used

## Prerequisite

Before running this example, make sure the environment is initialized (see [Initialize Environment](https://github.com/ivangfr/springboot-vault-examples#initialize-environment) section in the main README).

## Start movie-service

### Running with Maven Wrapper

- In a terminal, make sure you are inside the `springboot-vault-examples` root folder.

- Run the following command:
  ```bash
  ./mvnw clean spring-boot:run \
    --projects spring-vault-approle-mysql/movie-service \
    -Dspring-boot.run.jvmArguments="-Dserver.port=9082"
  ```

### Running as Docker Container

- In a terminal, make sure you are inside the `springboot-vault-examples` root folder.
  
- Build the Docker image:
  ```bash
  ./build-docker-images.sh spring-vault-approle-mysql
  ```
  | Environment Variable | Description                                               |
  |----------------------|-----------------------------------------------------------|
  | `VAULT_HOST`         | Specify host of the `Vault` to use (default `localhost`)  |
  | `VAULT_PORT`         | Specify port of the `Vault` to use (default `8200`)       |
  | `CONSUL_HOST`        | Specify host of the `Consul` to use (default `localhost`) |
  | `CONSUL_PORT`        | Specify port of the `Consul` to use (default `8500`)      |
  | `MYSQL_HOST`         | Specify host of the `MySQL` to use (default `localhost`)  |
  | `MYSQL_PORT`         | Specify port of the `MySQL` to use (default `3306`)       |

- Run the Docker container:
  ```bash
  docker run --rm --name movie-service -p 9082:8080 \
    -e VAULT_HOST=vault -e CONSUL_HOST=consul -e MYSQL_HOST=mysql \
    --network springboot-vault-examples \
    ivanfranchin/movie-service:1.0.0
  ```

## Using movie-service

You can access `movie-service` Swagger website at http://localhost:9082/swagger-ui.html

## Useful Links & Commands

- **Vault**

  > **Note**: In order to run some commands, you must have [`jq`](https://jqlang.org/) installed in your machine.

  - Open a new terminal
    
  - Set to `VAULT_ROOT_TOKEN` environment variable the value obtained while [initializing the environment](https://github.com/ivangfr/springboot-vault-examples#initialize-environment) described in the main README
    ```bash
    VAULT_ROOT_TOKEN=...
    ```

  - List of active leases for `database/creds/movie-role`
    ```bash
    curl -s -X LIST \
      -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
      http://localhost:8200/v1/sys/leases/lookup/database/creds/movie-role | jq .
    ```
     
    The response will be something like:
    ```json
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

  - See specific lease metadata
    ```bash
    curl -s -X PUT \
      -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
      -d '{ "lease_id": "database/creds/movie-role/CCV6h8U59TuGNNrvSUPNndYh" }' \
      http://localhost:8200/v1/sys/leases/lookup | jq .
    ```
     
    The response will be something like:
    ```json
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

  - Open a new terminal

  - Connect to `MySQL monitor` inside docker container
    ```bash
    docker exec -it -e MYSQL_PWD=secret mysql mysql -uroot
    ```
    > To exit `MySQL monitor`, type `exit`

    - List users
      ```bash
      SELECT User, Host FROM mysql.user;
      ```

    - Show running process
      ```bash
      SELECT * FROM information_schema.processlist ORDER BY user;
      ```

    - Log all queries
      ```bash
      SET GLOBAL general_log = 'ON';
      SET GLOBAL log_output = 'table';
    
      SELECT event_time, SUBSTRING(user_host,1,20) as user_host, thread_id, command_type, SUBSTRING(convert(argument using utf8),1,70) FROM mysql.general_log WHERE user_host LIKE 'v-approle-movie-%';
      ```

    - Create/Remove user
      ```bash
      CREATE USER 'newuser'@'%' IDENTIFIED BY 'password';
      GRANT ALL PRIVILEGES ON * . * TO 'newuser'@'%';
    
      DROP USER 'newuser'@'%';
      ```

- **Consul**

  `Consul` can be accessed at http://localhost:8500

## Shutdown

- Go to the terminal where the application is running and pressing `Ctrl+C`.
- Stop the services started using the `init-environment` script as explained in [Shutdown Environment](https://github.com/ivangfr/springboot-vault-examples#shutdown-environment) section of the main README.

## Cleanup

To remove the Docker image created by this example, go to a terminal and run the command below:
```bash
./remove-docker-images.sh spring-vault-approle-mysql
```
