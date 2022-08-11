# springboot-vault-examples
## `> spring-vault-approle-multi-datasources-mysql`

## Application

- ### restaurant-service

  - [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) application that manages `dishes` and `customers`
  - It uses [`MySQL`](https://www.mysql.com/) database as storage
  - It connects to two `MySQL` instances. One stores `dishes` information and another `customers`
  - It uses [`Spring Vault`](https://docs.spring.io/spring-vault/docs/2.1.3.RELEASE/reference/html/#_document_structure)
  - It uses [`Hikari`](https://github.com/brettwooldridge/HikariCP) JDBC connection pool
  - Credentials to access `MySQL` is generated dynamically by [`Vault`](https://www.vaultproject.io)
  - **Leases are renewed and rotated**
  - `AppRole` is the `Vault` authentication method used

## Prerequisite

Before running this example, make sure the environment is initialized (see [Initialize Environment](https://github.com/ivangfr/springboot-vault-examples#initialize-environment) section in the main README)

## Start restaurant-service

### Running with Maven Wrapper

- In a terminal, make sure you are inside `springboot-vault-examples` root folder

- Run the following commands
  ```
  export DISH_MYSQL_PORT=3307 && \
  ./mvnw clean spring-boot:run \
    --projects spring-vault-approle-multi-datasources-mysql/restaurant-service \
    -Dspring-boot.run.jvmArguments="-Dserver.port=9083"
  ```

### Running as Docker Container

- In a terminal, make sure you are inside `springboot-vault-examples` root folder
  
- Build the Docker image
  ```
  ./docker-build.sh spring-vault-approle-multi-datasources-mysql
  ```
  | Environment Variable  | Description                                               |
  |-----------------------|-----------------------------------------------------------|
  | `VAULT_HOST`          | Specify host of the `Vault` to use (default `localhost`)  |
  | `VAULT_PORT`          | Specify port of the `Vault` to use (default `8200`)       |
  | `CONSUL_HOST`         | Specify host of the `Consul` to use (default `localhost`) |
  | `CONSUL_PORT`         | Specify port of the `Consul` to use (default `8500`)      |
  | `CUSTOMER_MYSQL_HOST` | Specify host of the `MySQL` to use (default `localhost`)  |
  | `CUSTOMER_MYSQL_PORT` | Specify port of the `MySQL` to use (default `3306`)       |
  | `DISH_MYSQL_HOST`     | Specify host of the `MySQL` to use (default `localhost`)  |
  | `DISH_MYSQL_PORT`     | Specify port of the `MySQL` to use (default `3306`)       |

- Run the Docker container
  ```
  docker run --rm --name restaurant-service -p 9083:8080 \
    -e VAULT_HOST=vault -e CONSUL_HOST=consul -e CUSTOMER_MYSQL_HOST=mysql -e DISH_MYSQL_HOST=mysql-2 \
    --network springboot-vault-examples \
    ivanfranchin/restaurant-service:1.0.0
  ```

## Using restaurant-service

You can access `restaurant-service` Swagger website at http://localhost:9083/swagger-ui/index.html

## Useful Links & Commands

- **Vault**

  > **Note**: In order to run some commands, you must have [`jq`](https://stedolan.github.io/jq) installed in your machine

  - Open a new terminal
    
  - Export to `VAULT_ROOT_TOKEN` environment variable the value obtained while [initializing the environment](https://github.com/ivangfr/springboot-vault-examples#initialize-environment) described in the main README
    ```
    export VAULT_ROOT_TOKEN=...
    ```

  - List of active leases for `database/creds/customer-role` (the same can be done for `database/creds/dish-role`)
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

  - See specific lease metadata
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

- **MySQL**

  - Open a new terminal

  - Connect to `MySQL monitor` inside docker container
    ```
    docker exec -it -e MYSQL_PWD=secret mysql mysql -uroot
    -- OR --
    docker exec -it -e MYSQL_PWD=secret mysql-2 mysql -uroot
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
    
      SELECT event_time, SUBSTRING(user_host,1,20) as user_host, thread_id, command_type, SUBSTRING(convert(argument using utf8),1,70) FROM mysql.general_log WHERE user_host LIKE 'v-approle-customer-%';
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

- Go to the terminal where the application is running and pressing `Ctrl+C`
- Stop the services present in `docker-compose.yml` as explained in [Shutdown Environment](https://github.com/ivangfr/springboot-vault-examples#shutdown-environment) section of the main README

## Cleanup

To remove the Docker image create by this example, go to a terminal and run the command below
```
./remove-docker-images.sh spring-vault-approle-multi-datasources-mysql
```
