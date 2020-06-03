# springboot-vault-examples
## `> spring-cloud-vault-approle-cassandra`

## Application

- **book-service**

  - [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) application that manages `books`
  - It uses [`Cassandra`](https://cassandra.apache.org/) database as storage
  - It uses [`Spring Cloud Vault`](https://cloud.spring.io/spring-cloud-vault/spring-cloud-vault.html)
  - Credentials to access `Cassandra` is generated dynamically by [`Vault`](https://www.vaultproject.io)
  - **Leases are renewed but NOT rotated**
  - `AppRole` is the `Vault` authentication method used

## Prerequisite

Before running this example, all the steps described at [Start Environment](https://github.com/ivangfr/springboot-vault-examples#start-environment) section in the main README should be previously executed.

## Setup Vault-Cassandra

- Open a terminal and make sure you are inside `springboot-vault-examples` root folder

- Run the following script
  ```
  ./setup-spring-cloud-vault-approle-cassandra.sh
  ```

## Start book-service

### Running with Maven Wrapper

- In a terminal, make sure you are inside `springboot-vault-examples` root folder

- Run the following command
  ```
  ./mvnw clean spring-boot:run \
    --projects spring-cloud-vault-approle-cassandra/book-service \
    -Dspring-boot.run.jvmArguments="-Dserver.port=9081"
  ```

### Running as Docker Container

- Go to the `springboot-vault-examples` root folder and build the docker image
  ```
  ./mvnw clean spring-boot:build-image -DskipTests --projects spring-cloud-vault-approle-cassandra/book-service
  ```
  | Environment Variable | Description                                                  |
  | -------------------- | ------------------------------------------------------------ |
  | `VAULT_HOST`         | Specify host of the `Vault` to use (default `localhost`)     |
  | `VAULT_PORT`         | Specify port of the `Vault` to use (default `8200`)          |
  | `CONSUL_HOST`        | Specify host of the `Consul` to use (default `localhost`)    |
  | `CONSUL_PORT`        | Specify port of the `Consul` to use (default `8500`)         |
  | `CASSANDRA_HOST`     | Specify host of the `Cassandra` to use (default `localhost`) |
  | `CASSANDRA_PORT`     | Specify port of the `Cassandra` to use (default `9042`)      |

- Run the docker container
  ```
  docker run -d --rm --name book-service -p 9081:8080 \
    -e VAULT_HOST=vault -e CONSUL_HOST=consul -e CASSANDRA_HOST=cassandra \
    --network springboot-vault-examples_default \
    docker.mycompany.com/book-service:1.0.0
  ```

## Using book-service

You can access `book-service` Swagger website at: http://localhost:9081/swagger-ui.html

## Useful Links & Commands

- **Vault**

  > **Note:** In order to run some commands, you must have [`jq`](https://stedolan.github.io/jq) installed on you machine

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

  1. See specific lease metadata
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

- **Cassandra**

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

- **Consul**

  `Consul` can be accessed at http://localhost:8500

## Shutdown

- Stop application
  - If the application was started with `Maven`, go to the terminals where it is running and press `Ctrl+C`
  - If the application was started as a Docker container, run the command below
    ```
    docker stop book-service
    ```
    
- Stop docker-compose containers by following the instruction in [Shutdown](https://github.com/ivangfr/springboot-vault-examples#shutdown) section in the main README.

## Issues

- Sometimes, the following exception happens, and the application doesn't start
  ```
  WARN 39360 --- [           main] LeaseEventPublisher$LoggingErrorListener : [RequestedSecret [path='database/creds/book-role', mode=RENEW]] Lease [leaseId='null', leaseDuration=PT0S, renewable=false] Status 500 Internal Server Error [database/creds/book-role]: 1 error occurred:
  	* read tcp 172.23.0.6:60742->172.23.0.3:9042: i/o timeout
  
  ; nested exception is org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 Internal Server Error: [{"errors":["1 error occurred:\n\t* read tcp 172.23.0.6:60742-\u003e172.23.0.3:9042: i/o timeout\n\n"]}
  ]
  
  org.springframework.vault.VaultException: Status 500 Internal Server Error [database/creds/book-role]: 1 error occurred:
  	* read tcp 172.23.0.6:60742->172.23.0.3:9042: i/o timeout
  
  ; nested exception is org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 Internal Server Error: [{"errors":["1 error occurred:\n\t* read tcp 172.23.0.6:60742-\u003e172.23.0.3:9042: i/o timeout\n\n"]}
  ]
  	at org.springframework.vault.client.VaultResponses.buildException(VaultResponses.java:86) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	at org.springframework.vault.core.VaultTemplate.lambda$doRead$5(VaultTemplate.java:409) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	at org.springframework.vault.core.VaultTemplate.doWithSession(VaultTemplate.java:388) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	at org.springframework.vault.core.VaultTemplate.doRead(VaultTemplate.java:398) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	at org.springframework.vault.core.VaultTemplate.read(VaultTemplate.java:290) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	at org.springframework.vault.core.lease.SecretLeaseContainer.doGetSecrets(SecretLeaseContainer.java:662) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	at org.springframework.vault.core.lease.SecretLeaseContainer.start(SecretLeaseContainer.java:396) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	at org.springframework.vault.core.lease.SecretLeaseContainer.addRequestedSecret(SecretLeaseContainer.java:355) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	at org.springframework.vault.core.env.LeaseAwareVaultPropertySource.loadProperties(LeaseAwareVaultPropertySource.java:184) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	at org.springframework.vault.core.env.LeaseAwareVaultPropertySource.<init>(LeaseAwareVaultPropertySource.java:169) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	at org.springframework.vault.core.env.LeaseAwareVaultPropertySource.<init>(LeaseAwareVaultPropertySource.java:122) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	at org.springframework.cloud.vault.config.LeasingVaultPropertySourceLocator.createVaultPropertySource(LeasingVaultPropertySourceLocator.java:157) ~[spring-cloud-vault-config-2.2.3.RELEASE.jar:2.2.3.RELEASE]
  	at org.springframework.cloud.vault.config.LeasingVaultPropertySourceLocator.createVaultPropertySource(LeasingVaultPropertySourceLocator.java:89) ~[spring-cloud-vault-config-2.2.3.RELEASE.jar:2.2.3.RELEASE]
  	at org.springframework.cloud.vault.config.VaultPropertySourceLocatorSupport.doCreatePropertySources(VaultPropertySourceLocatorSupport.java:162) ~[spring-cloud-vault-config-2.2.3.RELEASE.jar:2.2.3.RELEASE]
  	at org.springframework.cloud.vault.config.VaultPropertySourceLocatorSupport.createCompositePropertySource(VaultPropertySourceLocatorSupport.java:138) ~[spring-cloud-vault-config-2.2.3.RELEASE.jar:2.2.3.RELEASE]
  	at org.springframework.cloud.vault.config.VaultPropertySourceLocatorSupport.locate(VaultPropertySourceLocatorSupport.java:111) ~[spring-cloud-vault-config-2.2.3.RELEASE.jar:2.2.3.RELEASE]
  	at org.springframework.cloud.bootstrap.config.PropertySourceLocator.locateCollection(PropertySourceLocator.java:52) ~[spring-cloud-context-2.2.3.RELEASE.jar:2.2.3.RELEASE]
  	at org.springframework.cloud.bootstrap.config.PropertySourceLocator.locateCollection(PropertySourceLocator.java:47) ~[spring-cloud-context-2.2.3.RELEASE.jar:2.2.3.RELEASE]
  	at org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration.initialize(PropertySourceBootstrapConfiguration.java:98) ~[spring-cloud-context-2.2.3.RELEASE.jar:2.2.3.RELEASE]
  	at org.springframework.boot.SpringApplication.applyInitializers(SpringApplication.java:626) ~[spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
  	at org.springframework.boot.SpringApplication.prepareContext(SpringApplication.java:370) ~[spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:314) ~[spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1237) ~[spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1226) ~[spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
  	at com.mycompany.bookservice.BookServiceApplication.main(BookServiceApplication.java:12) ~[classes/:na]
  Caused by: org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 Internal Server Error: [{"errors":["1 error occurred:\n\t* read tcp 172.23.0.6:60742-\u003e172.23.0.3:9042: i/o timeout\n\n"]}
  ]
  	at org.springframework.web.client.HttpServerErrorException.create(HttpServerErrorException.java:100) ~[spring-web-5.2.6.RELEASE.jar:5.2.6.RELEASE]
  	at org.springframework.web.client.DefaultResponseErrorHandler.handleError(DefaultResponseErrorHandler.java:172) ~[spring-web-5.2.6.RELEASE.jar:5.2.6.RELEASE]
  	at org.springframework.web.client.DefaultResponseErrorHandler.handleError(DefaultResponseErrorHandler.java:112) ~[spring-web-5.2.6.RELEASE.jar:5.2.6.RELEASE]
  	at org.springframework.web.client.ResponseErrorHandler.handleError(ResponseErrorHandler.java:63) ~[spring-web-5.2.6.RELEASE.jar:5.2.6.RELEASE]
  	at org.springframework.web.client.RestTemplate.handleResponse(RestTemplate.java:782) ~[spring-web-5.2.6.RELEASE.jar:5.2.6.RELEASE]
  	at org.springframework.web.client.RestTemplate.doExecute(RestTemplate.java:740) ~[spring-web-5.2.6.RELEASE.jar:5.2.6.RELEASE]
  	at org.springframework.web.client.RestTemplate.execute(RestTemplate.java:674) ~[spring-web-5.2.6.RELEASE.jar:5.2.6.RELEASE]
  	at org.springframework.web.client.RestTemplate.getForObject(RestTemplate.java:315) ~[spring-web-5.2.6.RELEASE.jar:5.2.6.RELEASE]
  	at org.springframework.vault.core.VaultTemplate.lambda$doRead$5(VaultTemplate.java:401) ~[spring-vault-core-2.2.0.RELEASE.jar:2.2.0.RELEASE]
  	... 23 common frames omitted
  ```