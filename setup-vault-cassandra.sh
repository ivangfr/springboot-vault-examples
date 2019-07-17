#!/usr/bin/env bash

if [ -z "$VAULT_ROOT_TOKEN" ]; then
  echo "WARNING: export to VAULT_ROOT_TOKEN environment variable the root token generated while running the script unseal-vault-enable-approle.sh!"
  exit 1
fi

DATABASE_ROLE=bookdb
DATABASE_ROLE_POLICY="${DATABASE_ROLE}-policy"
BOOK_USER=bookdb-user
VAULT_ADDR=http://localhost:8200

echo
echo "================"
echo "-- Database (Cassandra)"

echo
echo "--> configuring plugin and connection ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d "{\"plugin_name\": \"cassandra-database-plugin\", \"allowed_roles\": \"${DATABASE_ROLE}\", \"hosts\": \"cassandra\", \"username\": \"cassandra\", \"password\": \"cassandra\", \"protocol_version\": 3}" ${VAULT_ADDR}/v1/database/config/cassandra

# --
# P.S. Setting the 'default_ttl' and 'max_ttl' here do not work! In order to test shorter times, change 'config.hcl' file.
# --
echo
echo "--> creating Database role '${DATABASE_ROLE}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"db_name": "cassandra", "creation_statements":"CREATE USER '"'"'{{username}}'"'"' WITH PASSWORD '"'"'{{password}}'"'"' NOSUPERUSER; GRANT ALL PERMISSIONS ON ALL KEYSPACES TO {{username}}"}, "default_ttl": "2m", "max_ttl": "10m"' ${VAULT_ADDR}/v1/database/roles/${DATABASE_ROLE}
#curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"db_name": "cassandra", "creation_statements":"CREATE USER '"'"'{{name}}'"'"'@'"'"'%'"'"' IDENTIFIED BY '"'"'{{password}}'"'"'; GRANT ALL ON *.* TO '"'"'{{name}}'"'"'@'"'"'%'"'"';"}, "default_ttl": "2m", "max_ttl": "10m"' ${VAULT_ADDR}/v1/database/roles/${DATABASE_ROLE}

echo "--> setting Database policy '${DATABASE_ROLE_POLICY}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"policy":"path \"database/creds/'${DATABASE_ROLE}'\" {policy=\"read\"} path \"sys/renew/database/creds/*\" {capabilities=[\"update\"]}"}' ${VAULT_ADDR}/v1/sys/policy/${DATABASE_ROLE_POLICY}

echo "--> testing Database role '${DATABASE_ROLE}' with ROOT_TOKEN ..."
curl -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" ${VAULT_ADDR}/v1/database/creds/${DATABASE_ROLE}

echo
echo "--> List of leases"
curl -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -X LIST ${VAULT_ADDR}/v1/sys/leases/lookup/database/creds/${DATABASE_ROLE}

echo
echo "================"
echo "-- AppRole (login without secret-id)"

echo "--> creating AppRole '${BOOK_USER}' with policy '${DATABASE_ROLE_POLICY}' ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d '{"policies": ["'${DATABASE_ROLE_POLICY}'"], "bound_cidr_list": "0.0.0.0/0", "bind_secret_id": false}' ${VAULT_ADDR}/v1/auth/approle/role/${BOOK_USER}

echo "--> fetching the identifier of the AppRole '${BOOK_USER}' ..."
ROLE_ID=$(curl -s -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" ${VAULT_ADDR}/v1/auth/approle/role/${BOOK_USER}/role-id | jq -r .data.role_id)
echo "ROLE_ID=$ROLE_ID"

echo
echo "--> getting client token ..."
CLIENT_TOKEN=$(curl -X POST -s -d '{"role_id":"'${ROLE_ID}'"}' ${VAULT_ADDR}/v1/auth/approle/login | jq -r .auth.client_token)
echo "CLIENT_TOKEN=$CLIENT_TOKEN"

echo
echo "--> testing Cassandra role '${DATABASE_ROLE}' with CLIENT_TOKEN ..."
curl -i -H "X-Vault-Token:$CLIENT_TOKEN" ${VAULT_ADDR}/v1/database/creds/${DATABASE_ROLE}

echo
echo "************************************************************"
echo "export ROLE_ID=${ROLE_ID}"
echo "************************************************************"
echo