#!/usr/bin/env bash

if [ -z "$VAULT_ROOT_TOKEN" ]; then
  echo "WARNING: set to VAULT_ROOT_TOKEN environment variable, the root token generated while running the script unseal-vault-enable-approle-databases.sh!"
  exit 1
fi

VAULT_ADDR=http://localhost:8200

CUSTOM_ROLE_ID="restaurant-service-role-id"
DATABASE_USER="restaurant-user"

CUSTOMER_DATABASE_ROLE="customer-role"
CUSTOMER_DATABASE_ROLE_POLICY="customer-policy"

DISH_DATABASE_ROLE="dish-role"
DISH_DATABASE_ROLE_POLICY="dish-policy"

KV_ROLE_POLICY="kv-policy"

echo
echo "==================="
echo "-- Database (MySQL)"

echo
echo "--> creating Database role '${CUSTOMER_DATABASE_ROLE}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"db_name": "mysql", "creation_statements":"CREATE USER '"'"'{{name}}'"'"'@'"'"'%'"'"' IDENTIFIED BY '"'"'{{password}}'"'"'; GRANT ALL ON *.* TO '"'"'{{name}}'"'"'@'"'"'%'"'"';"}, "default_ttl": "2m", "max_ttl": "10m"' ${VAULT_ADDR}/v1/database/roles/${CUSTOMER_DATABASE_ROLE}
#-- Note. Setting the 'default_ttl' and 'max_ttl' in the command above does not work! In order to test shorter times, change 'config.hcl' file.

echo "--> setting Database policy '${CUSTOMER_DATABASE_ROLE_POLICY}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"policy":"path \"database/creds/'${CUSTOMER_DATABASE_ROLE}'\" {policy=\"read\"} path \"sys/renew/database/creds/*\" {capabilities=[\"update\"]}"}' ${VAULT_ADDR}/v1/sys/policy/${CUSTOMER_DATABASE_ROLE_POLICY}

echo "--> testing Database role '${CUSTOMER_DATABASE_ROLE}' with ROOT_TOKEN ..."
curl -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" ${VAULT_ADDR}/v1/database/creds/${CUSTOMER_DATABASE_ROLE}

echo
echo "--> List of leases"
curl -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -X LIST ${VAULT_ADDR}/v1/sys/leases/lookup/database/creds/${CUSTOMER_DATABASE_ROLE}

echo
echo "====================="
echo "-- Database (MySQL-2)"

echo
echo "--> creating Database role '${DISH_DATABASE_ROLE}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"db_name": "mysql-2", "creation_statements":"CREATE USER '"'"'{{name}}'"'"'@'"'"'%'"'"' IDENTIFIED BY '"'"'{{password}}'"'"'; GRANT ALL ON *.* TO '"'"'{{name}}'"'"'@'"'"'%'"'"';"}, "default_ttl": "2m", "max_ttl": "10m"' ${VAULT_ADDR}/v1/database/roles/${DISH_DATABASE_ROLE}
#-- Note. Setting the 'default_ttl' and 'max_ttl' in the command above does not work! In order to test shorter times, change 'config.hcl' file.

echo "--> setting Database policy '${DISH_DATABASE_ROLE_POLICY}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"policy":"path \"database/creds/'${DISH_DATABASE_ROLE}'\" {policy=\"read\"} path \"sys/renew/database/creds/*\" {capabilities=[\"update\"]}"}' ${VAULT_ADDR}/v1/sys/policy/${DISH_DATABASE_ROLE_POLICY}

echo "--> testing Database role '${DISH_DATABASE_ROLE}' with ROOT_TOKEN ..."
curl -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" ${VAULT_ADDR}/v1/database/creds/${DISH_DATABASE_ROLE}

echo
echo "--> List of leases"
curl -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -X LIST ${VAULT_ADDR}/v1/sys/leases/lookup/database/creds/${DISH_DATABASE_ROLE}

echo
echo "==================="
echo "-- Static KV secret"

echo "setting message KV secret ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d '{"message": "Hello from restaurant-service"}' ${VAULT_ADDR}/v1/secret/restaurant-service

echo "--> setting KV secret policy '${KV_ROLE_POLICY}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"policy":"path \"secret/*\" {policy=\"read\"}"}' ${VAULT_ADDR}/v1/sys/policy/${KV_ROLE_POLICY}

echo "===================================="
echo "-- AppRole (login without secret-id)"

echo
echo "--> creating AppRole '${DATABASE_USER}' with policies '${CUSTOMER_DATABASE_ROLE_POLICY}', '${DISH_DATABASE_ROLE_POLICY}', ${KV_ROLE_POLICY} ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d '{"policies": ["'${CUSTOMER_DATABASE_ROLE_POLICY}'", "'${DISH_DATABASE_ROLE_POLICY}'", "'${KV_ROLE_POLICY}'"], "bound_cidr_list": "0.0.0.0/0", "bind_secret_id": false}' ${VAULT_ADDR}/v1/auth/approle/role/${DATABASE_USER}

echo "--> update ROLE_ID with custom value '${CUSTOM_ROLE_ID}'"
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d '{"role_id": "'${CUSTOM_ROLE_ID}'"}' ${VAULT_ADDR}/v1/auth/approle/role/${DATABASE_USER}/role-id

echo "--> fetching the identifier of the AppRole '${DATABASE_USER}' ..."
ROLE_ID=$(curl -s -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" ${VAULT_ADDR}/v1/auth/approle/role/${DATABASE_USER}/role-id | jq -r .data.role_id)
echo "ROLE_ID=${ROLE_ID}"

echo
echo "--> getting client token ..."
CLIENT_TOKEN=$(curl -X POST -s -d '{"role_id":"'${ROLE_ID}'"}' ${VAULT_ADDR}/v1/auth/approle/login | jq -r .auth.client_token)
echo "CLIENT_TOKEN=${CLIENT_TOKEN}"

echo
echo "--> testing MySQL role '${CUSTOMER_DATABASE_ROLE}' with CLIENT_TOKEN ..."
curl -i -H "X-Vault-Token:${CLIENT_TOKEN}" ${VAULT_ADDR}/v1/database/creds/${CUSTOMER_DATABASE_ROLE}

echo
echo "--> testing MySQL-2 role '${DISH_DATABASE_ROLE}' with CLIENT_TOKEN ..."
curl -i -H "X-Vault-Token:${CLIENT_TOKEN}" ${VAULT_ADDR}/v1/database/creds/${DISH_DATABASE_ROLE}

echo "--> testing message KV secret ..."
curl -i -H "X-Vault-Token:${CLIENT_TOKEN}" ${VAULT_ADDR}/v1/secret/restaurant-service
echo