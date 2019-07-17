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

echo
echo "================"
echo "-- Mounting Database ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"type": "database"}' ${VAULT_ADDR}/v1/sys/mounts/database

echo "************************************************************"
echo "export VAULT_ROOT_TOKEN=${VAULT_ROOT_TOKEN}"
echo "************************************************************"
echo