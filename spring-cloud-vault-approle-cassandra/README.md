# springboot-vault-examples
## `> spring-cloud-vault-approle-cassandra`

## Project Diagram

![project-diagram](../documentation/spring-cloud-vault-approle-cassandra.jpeg)

## Application

- ### book-service

  - [`Spring Boot`](https://docs.spring.io/spring-boot/index.html) application that manages `books`
  - It uses [`Cassandra`](https://cassandra.apache.org/) database as storage
  - It uses [`Spring Cloud Vault`](https://cloud.spring.io/spring-cloud-vault/reference/html/)
  - Credentials to access `Cassandra` is generated dynamically by [`Vault`](https://www.vaultproject.io)
  - **Leases are renewed but NOT rotated**
  - `AppRole` is the `Vault` authentication method used

## Prerequisite

Before running this example, make sure the environment is initialized (see [Initialize Environment](https://github.com/ivangfr/springboot-vault-examples#initialize-environment) section in the main README).

## Start book-service

### Running with Maven Wrapper

- In a terminal, make sure you are inside the `springboot-vault-examples` root folder.

- Run the following command:
  ```bash
  ./mvnw clean spring-boot:run \
    --projects spring-cloud-vault-approle-cassandra/book-service \
    -Dspring-boot.run.jvmArguments="-Dserver.port=9081"
  ```

### Running as Docker Container

- In a terminal, make sure you are inside the `springboot-vault-examples` root folder.
  
- Build the Docker image:
  ```bash
  ./build-docker-images.sh spring-cloud-vault-approle-cassandra
  ```
  | Environment Variable | Description                                                  |
  |----------------------|--------------------------------------------------------------|
  | `VAULT_HOST`         | Specify host of the `Vault` to use (default `localhost`)     |
  | `VAULT_PORT`         | Specify port of the `Vault` to use (default `8200`)          |
  | `CONSUL_HOST`        | Specify host of the `Consul` to use (default `localhost`)    |
  | `CONSUL_PORT`        | Specify port of the `Consul` to use (default `8500`)         |
  | `CASSANDRA_HOST`     | Specify host of the `Cassandra` to use (default `localhost`) |
  | `CASSANDRA_PORT`     | Specify port of the `Cassandra` to use (default `9042`)      |

- Run the Docker container:
  ```bash
  docker run --rm --name book-service -p 9081:8080 \
    -e VAULT_HOST=vault -e CONSUL_HOST=consul -e CASSANDRA_HOST=cassandra \
    --network springboot-vault-examples \
    ivanfranchin/book-service:1.0.0
  ```

## Using book-service

You can access `book-service` Swagger website at http://localhost:9081/swagger-ui.html

## Useful Links & Commands

- **Vault**

  > **Note**: In order to run some commands, you must have [`jq`](https://jqlang.github.io/jq/) installed in your machine.

  - Open a new terminal
    
  - Set to `VAULT_ROOT_TOKEN` environment variable the value obtained while [initializing the environment](https://github.com/ivangfr/springboot-vault-examples#initialize-environment) described in the main README.
    ```bash
    VAULT_ROOT_TOKEN=...
    ```

  - List of active leases for `database/creds/book-role`
    ```bash
    curl -s -X LIST \
      -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
      http://localhost:8200/v1/sys/leases/lookup/database/creds/book-role | jq .
    ```
     
    The response will be something like:
    ```json
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

  - See specific lease metadata
    ```bash
    curl -s -X PUT \
      -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" \
      -d '{ "lease_id": "database/creds/book-role/AsQLBqfNGNNNeeEFp2c6sQs9" }' \
      http://localhost:8200/v1/sys/leases/lookup | jq .
    ```
     
    The response will be something like:
    ```json
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

- **Cassandra**

  - Open a new terminal

  - Connect to `cqlsh` inside docker container and list books
    ```bash
    docker exec -it cassandra cqlsh -ucassandra -pcassandra -k mycompany
    ```
    > To exit `cqlsh`, type `exit`

    - List users
      ```bash
      LIST USERS;
      ```

- **Consul**

  `Consul` can be accessed at http://localhost:8500

## Shutdown

- Go to the terminal where the application is running and pressing `Ctrl+C`.
- Stop the services started using the `init-environment` script as explained in [Shutdown Environment](https://github.com/ivangfr/springboot-vault-examples#shutdown-environment) section of the main README.

## Cleanup

To remove the Docker image created by this example, go to a terminal and run the command below:
```bash
./remove-docker-images.sh spring-cloud-vault-approle-cassandra
```
