#!/usr/bin/env bash

if [ -z "$VAULT_ROOT_TOKEN" ]; then
  echo "WARNING: export to VAULT_ROOT_TOKEN environment variable the root token generated while running the script unseal-vault-enable-approle-databases.sh!"
  exit 1
fi

VAULT_ADDR=http://localhost:8200

CUSTOM_ROLE_ID="student-service-role-id"
DATABASE_USER="student-user"

DATABASE_ROLE="student-role"
DATABASE_ROLE_POLICY="student-policy"

KV_ROLE_POLICY="kv-policy"

echo
echo "================"
echo "-- Database (MySQL)"

echo
echo "--> creating Database role '${DATABASE_ROLE}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"db_name": "mysql", "creation_statements":"CREATE USER '"'"'{{name}}'"'"'@'"'"'%'"'"' IDENTIFIED BY '"'"'{{password}}'"'"'; GRANT ALL ON *.* TO '"'"'{{name}}'"'"'@'"'"'%'"'"';"}, "default_ttl": "2m", "max_ttl": "10m"' ${VAULT_ADDR}/v1/database/roles/${DATABASE_ROLE}
#-- Note. Setting the 'default_ttl' and 'max_ttl' in the command above does not work! In order to test shorter times, change 'config.hcl' file.

echo "--> setting Database policy '${DATABASE_ROLE_POLICY}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"policy":"path \"database/creds/'${DATABASE_ROLE}'\" {policy=\"read\"} path \"sys/renew/database/creds/*\" {capabilities=[\"update\"]}"}' ${VAULT_ADDR}/v1/sys/policy/${DATABASE_ROLE_POLICY}

echo "--> testing Database role '${DATABASE_ROLE}' with ROOT_TOKEN ..."
curl -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" ${VAULT_ADDR}/v1/database/creds/${DATABASE_ROLE}

echo
echo "--> List of leases"
curl -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -X LIST ${VAULT_ADDR}/v1/sys/leases/lookup/database/creds/${DATABASE_ROLE}

echo
echo "================"
echo "-- Static KV secret"

echo "setting message KV secret ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d '{"message": "Hello from student-service"}' ${VAULT_ADDR}/v1/secret/student-service

echo "--> setting KV secret policy '${KV_ROLE_POLICY}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"policy":"path \"secret/*\" {policy=\"read\"}"}' ${VAULT_ADDR}/v1/sys/policy/${KV_ROLE_POLICY}

echo "================"
echo "-- AppRole (login without secret-id)"

echo
echo "--> creating AppRole '${DATABASE_USER}' with policies '${DATABASE_ROLE_POLICY}' and '${KV_ROLE_POLICY}' ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d '{"policies": ["'${DATABASE_ROLE_POLICY}'", "'${KV_ROLE_POLICY}'"], "bound_cidr_list": "0.0.0.0/0", "bind_secret_id": false}' ${VAULT_ADDR}/v1/auth/approle/role/${DATABASE_USER}

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
echo "--> testing MySQL role '${DATABASE_ROLE}' with CLIENT_TOKEN ..."
curl -i -H "X-Vault-Token:${CLIENT_TOKEN}" ${VAULT_ADDR}/v1/database/creds/${DATABASE_ROLE}
echo

echo "--> testing message KV secret ..."
curl -i -H "X-Vault-Token:${CLIENT_TOKEN}" ${VAULT_ADDR}/v1/secret/student-service
echo
