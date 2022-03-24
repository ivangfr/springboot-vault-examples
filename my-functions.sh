#!/usr/bin/env bash

# -- check_script_input_parameter --
# $1: input parameter
function check_script_input_parameter() {
  if [ "$1" != "all" ] &&
     [ "$1" != "spring-vault-approle-mysql" ] &&
     [ "$1" != "spring-vault-approle-multi-datasources-mysql" ] &&
     [ "$1" != "spring-cloud-vault-approle-mysql" ] &&
     [ "$1" != "spring-cloud-vault-approle-cassandra" ];
  then
    printf "Invalid example name provided!"
    printf "\nValid Parameters:"

    printf "\n\tall"
    printf "\n\tspring-vault-approle-mysql"

    printf "\n\tspring-vault-approle-multi-datasources-mysql"
    printf "\n\tspring-cloud-vault-approle-mysql"
    printf "\n\tspring-cloud-vault-approle-cassandra"

    printf "\n"
    exit 1
  fi
}