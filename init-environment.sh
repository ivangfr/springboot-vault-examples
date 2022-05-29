#!/usr/bin/env bash

source scripts/my-functions.sh

MYSQL_VERSION="8.0.29"
CASSANDRA_VERSION="4.0.4"
VAULT_VERSION="1.10.3"
CONSUL_VERSION="1.12.1"

echo
echo "Starting environment"
echo "===================="

echo
echo "Creating network"
echo "----------------"
docker network create springboot-vault-examples

echo
echo "Starting consul container"
echo "-------------------------"

docker run -d --rm --name consul \
  -p 8400:8400 \
  -p 8500:8500 \
  -p 8600:53/udp \
  --network=springboot-vault-examples \
  consul:${CONSUL_VERSION}

echo
echo "Starting vault container"
echo "------------------------"

docker run -d --rm --name vault \
  -p 8200:8200 \
  -e "MYSQL_ROOT_PASSWORD=secret" \
  -e "MYSQL_DATABASE=exampledb" \
  --cap-add=IPC_LOCK \
  -v ${PWD}/docker/vault:/my/vault \
  --network=springboot-vault-examples \
  vault:${VAULT_VERSION} \
  vault server -config=/my/vault/config/config.hcl

echo
wait_for_container_log "consul" "Node info in sync"

echo
wait_for_container_log "vault" "Vault server started!"

echo
echo "Starting mysql container"
echo "------------------------"

docker run -d --rm --name mysql \
  -p 3306:3306 \
  -e "MYSQL_ROOT_PASSWORD=secret" \
  -e "MYSQL_DATABASE=exampledb" \
  --network=springboot-vault-examples \
  mysql:${MYSQL_VERSION}

echo
echo "Starting mysql-2 container"
echo "--------------------------"

docker run -d --rm --name mysql-2 \
  -p 3307:3306 \
  -e "MYSQL_ROOT_PASSWORD=secret" \
  -e "MYSQL_DATABASE=exampledb" \
  --network=springboot-vault-examples \
  mysql:${MYSQL_VERSION}

echo
echo "Building cassandra image"
echo "------------------------"

docker build -t springboot-vault-examples_cassandra:latest docker/cassandra

echo
echo "Starting cassandra container"
echo "----------------------------"

docker run -d --rm --name cassandra \
  -p 9042:9042 \
  -p 7199:7199 \
  -p 9160:9160 \
  -e "MYSQL_ROOT_PASSWORD=secret" \
  -e "MYSQL_DATABASE=exampledb" \
  --network=springboot-vault-examples \
  springboot-vault-examples_cassandra:latest

echo
wait_for_container_log "mysql" "port: 3306"

echo
wait_for_container_log "mysql-2" "port: 3306"

echo
wait_for_container_log "cassandra" "Created default superuser role"

source scripts/unseal-vault-enable-approle-databases.sh

source scripts/setup-spring-cloud-vault-approle-mysql.sh
source scripts/setup-spring-cloud-vault-approle-cassandra.sh
source scripts/setup-spring-vault-approle-mysql.sh
source scripts/setup-spring-vault-approle-multi-datasources-mysql.sh

echo "****************************************************"
echo "export VAULT_ROOT_TOKEN=${VAULT_ROOT_TOKEN}"
echo "****************************************************"

echo
echo "Environment Up and Running"
echo "=========================="
echo