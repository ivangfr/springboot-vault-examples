#!/usr/bin/env bash

if [ -z "$VAULT_ROOT_TOKEN" ]; then
  echo "WARNING: export to VAULT_ROOT_TOKEN environment variable the root token generated while running the script unseal-vault-enable-approle.sh!"
  exit 1
fi

source vault.env

echo
echo "================"
echo "-- Database (MySQL)"

echo
echo "--> mounting Database ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"type": "database"}' ${VAULT_ADDR}/v1/sys/mounts/database

echo "--> configuring plugin and connection ..."
curl -X POST -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -d "{\"plugin_name\": \"mysql-database-plugin\", \"allowed_roles\": \"${DATABASE_ROLE}\", \"connection_url\": \"root:secret@tcp(mysql:3306)/\"}" ${VAULT_ADDR}/v1/database/config/mysql

# --
# P.S. Setting the 'default_ttl' and 'max_ttl' here do not work! In order to test shorter times, change 'config.hcl' file.
# --
echo
echo "--> creating Database role '${DATABASE_ROLE}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"db_name": "mysql", "creation_statements":"CREATE USER '"'"'{{name}}'"'"'@'"'"'%'"'"' IDENTIFIED BY '"'"'{{password}}'"'"'; GRANT ALL ON *.* TO '"'"'{{name}}'"'"'@'"'"'%'"'"';"}, "default_ttl": "2m", "max_ttl": "10m"' ${VAULT_ADDR}/v1/database/roles/${DATABASE_ROLE}

echo "--> setting Database policy '${DATABASE_ROLE_POLICY}' ..."
curl -X POST -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" -d '{"policy":"path \"database/creds/'${DATABASE_ROLE}'\" {policy=\"read\"} path \"sys/renew/database/creds/*\" {capabilities=[\"update\"]}"}' ${VAULT_ADDR}/v1/sys/policy/${DATABASE_ROLE_POLICY}

echo "--> testing Database role '${DATABASE_ROLE}' with ROOT_TOKEN ..."
curl -i -H "X-Vault-Token:${VAULT_ROOT_TOKEN}" ${VAULT_ADDR}/v1/database/creds/${DATABASE_ROLE}

echo
echo "--> List of leases"
curl -i -H "X-Vault-Token: ${VAULT_ROOT_TOKEN}" -X LIST ${VAULT_ADDR}/v1/sys/leases/lookup/database/creds/${DATABASE_ROLE}
echo
