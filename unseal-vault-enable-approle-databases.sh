#!/usr/bin/env bash

VAULT_ADDR=http://localhost:8200

echo "================"
echo "-- Initializing Vault"

VAULT_KEYS=$(curl -X PUT -s -d '{ "secret_shares": 1, "secret_threshold": 1 }' ${VAULT_ADDR}/v1/sys/init | jq .)
VAULT_KEY1=$(echo ${VAULT_KEYS} | jq -r .keys_base64[0])
VAULT_ROOT_TOKEN=$(echo ${VAULT_KEYS} | jq -r .root_token)

echo
echo "--> unsealing Vault ..."
curl -X PUT -d '{ "key": "'${VAULT_KEY1}'" }' ${VAULT_ADDR}/v1/sys/unseal

echo
echo "--> Vault status"
curl ${VAULT_ADDR}/v1/sys/init

echo
echo "================"
echo "-- AppRole (login without secret-id)"

echo
echo "--> enabling the AppRole auth method ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d '{"type": "approle"}' ${VAULT_ADDR}/v1/sys/auth/approle

echo "================"
echo "-- KV Secrets Engine - Version 1"

echo
echo "--> enabling KV Secrets Engine ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d '{"type": "kv", "description": "my KV Secrets Engine", "config": {"force_no_cache": true}}' ${VAULT_ADDR}/v1/sys/mounts/secret

echo "================"
echo "-- Mounting Database ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"type": "database"}' ${VAULT_ADDR}/v1/sys/mounts/database

echo "--> configuring Cassandra plugin and connection ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d "{\"plugin_name\": \"cassandra-database-plugin\", \"allowed_roles\": \"*\", \"hosts\": \"cassandra\", \"username\": \"cassandra\", \"password\": \"cassandra\", \"protocol_version\": 3}" ${VAULT_ADDR}/v1/database/config/cassandra

echo
echo "--> configuring MySQL plugin and connection ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d "{\"plugin_name\": \"mysql-database-plugin\", \"allowed_roles\": \"*\", \"connection_url\": \"root:secret@tcp(mysql:3306)/\"}" ${VAULT_ADDR}/v1/database/config/mysql

echo
echo "--> configuring MySQL-2 plugin and connection ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d "{\"plugin_name\": \"mysql-database-plugin\", \"allowed_roles\": \"*\", \"connection_url\": \"root:secret@tcp(mysql-2:3306)/\"}" ${VAULT_ADDR}/v1/database/config/mysql-2

echo "************************************************************"
echo "export VAULT_ROOT_TOKEN=${VAULT_ROOT_TOKEN}"
echo "************************************************************"
echo