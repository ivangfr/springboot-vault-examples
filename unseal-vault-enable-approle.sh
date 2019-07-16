#!/usr/bin/env bash

source vault.env

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

echo "--> creating AppRole '${STUDENT_USER}' with policy '${DATABASE_ROLE_POLICY}' ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d '{"policies": ["'${DATABASE_ROLE_POLICY}'"], "bound_cidr_list": "0.0.0.0/0", "bind_secret_id": false}' ${VAULT_ADDR}/v1/auth/approle/role/${STUDENT_USER}

echo "--> fetching the identifier of the AppRole '${STUDENT_USER}' ..."
ROLE_ID=$(curl -s -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" ${VAULT_ADDR}/v1/auth/approle/role/${STUDENT_USER}/role-id | jq -r .data.role_id)
echo "ROLE_ID=$ROLE_ID"

echo "--> getting client token ..."
CLIENT_TOKEN=$(curl -X POST -s -d '{"role_id":"'${ROLE_ID}'"}' ${VAULT_ADDR}/v1/auth/approle/login | jq -r .auth.client_token)
echo "CLIENT_TOKEN=$CLIENT_TOKEN"

echo
echo "************************************************************"
echo "export VAULT_ROOT_TOKEN=${VAULT_ROOT_TOKEN}"
echo "export ROLE_ID=${ROLE_ID}"
echo "************************************************************"