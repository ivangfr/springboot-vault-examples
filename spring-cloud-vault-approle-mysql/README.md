# springboot-vault-examples
## `> spring-cloud-vault-approle-mysql`

## Project Diagram

![project-diagram](../documentation/spring-cloud-vault-approle-mysql.jpeg)

## Application

- ### student-service

  - [`Spring Boot`](https://docs.spring.io/spring-boot/index.html) application that manages `students`
  - It uses [`MySQL`](https://www.mysql.com/) database as storage
  - It uses [`Spring Cloud Vault`](https://cloud.spring.io/spring-cloud-vault/reference/html/)
  - It uses [`Hikari`](https://github.com/brettwooldridge/HikariCP) JDBC connection pool
  - Credentials to access `MySQL` is generated dynamically by [`Vault`](https://www.vaultproject.io)
  - **Leases are renewed and rotated**
  - `AppRole` is the `Vault` authentication method used

## Prerequisite

Before running this example, make sure the environment is initialized (see [Initialize Environment](https://github.com/ivangfr/springboot-vault-examples#initialize-environment) section in the main README).

## Start student-service

### Running with Maven Wrapper

- In a terminal, make sure you are inside the `springboot-vault-examples` root folder;

- Run the following command:
  ```
  ./mvnw clean spring-boot:run \
    --projects spring-cloud-vault-approle-mysql/student-service \
    -Dspring-boot.run.jvmArguments="-Dserver.port=9080"
  ```

### Running as Docker Container

- In a terminal, make sure you are inside the `springboot-vault-examples` root folder;
  
- Build the Docker image:
  ```
  ./build-docker-images.sh spring-cloud-vault-approle-mysql
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
  ```
  docker run --rm --name student-service -p 9080:8080 \
    -e VAULT_HOST=vault -e CONSUL_HOST=consul -e MYSQL_HOST=mysql \
    --network springboot-vault-examples \
    ivanfranchin/student-service:1.0.0
  ```

## Using student-service

You can access `student-service` Swagger website at http://localhost:9080/swagger-ui.html

## Useful Links & Commands

- **Vault**

  > **Note**: In order to run some commands, you must have [`jq`](https://jqlang.github.io/jq/) installed in your machine.

  - Open a new terminal
    
  - Set to `VAULT_ROOT_TOKEN` environment variable the value obtained while [initializing the environment](https://github.com/ivangfr/springboot-vault-examples#initialize-environment) described in the main README
    ```
    VAULT_ROOT_TOKEN=...
    ```

  - List of active leases for `database/creds/student-role`
    ```
    curl -s -X LIST \
      -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
      http://localhost:8200/v1/sys/leases/lookup/database/creds/student-role | jq .
    ```
     
    The response will be something like:
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

  - See specific lease metadata
    ```
    curl -s -X PUT \
      -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
      -d '{ "lease_id": "database/creds/student-role/2RXJje2dzEOgClBVAO4O9dbx" }' \
      http://localhost:8200/v1/sys/leases/lookup | jq .
    ```
     
    The response will be something like:
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

- **MySQL**

  - Open a new terminal

  - Connect to `MySQL monitor` inside docker container
    ```
    docker exec -it -e MYSQL_PWD=secret mysql mysql -uroot
    ```
    > To exit `MySQL monitor`, type `exit`

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
      SET GLOBAL log_output = 'table';
    
      SELECT event_time, SUBSTRING(user_host,1,20) as user_host, thread_id, command_type, SUBSTRING(convert(argument using utf8),1,70) FROM mysql.general_log WHERE user_host LIKE 'v-approle-student-%';
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

- Go to the terminal where the application is running and pressing `Ctrl+C`;
- Stop the services started using the `init-environment` script as explained in [Shutdown Environment](https://github.com/ivangfr/springboot-vault-examples#shutdown-environment) section of the main README.

## Cleanup

To remove the Docker image create by this example, go to a terminal and run the command below:
```
./remove-docker-images.sh spring-cloud-vault-approle-mysql
```
