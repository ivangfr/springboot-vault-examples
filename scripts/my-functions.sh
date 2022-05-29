#!/usr/bin/env bash

TIMEOUT=120

# -- wait_for_container_log --
# $1: docker container name
# S2: spring value to wait to appear in container logs
function wait_for_container_log() {
  local log_waiting="Waiting for string '$2' in the $1 logs ..."
  echo "${log_waiting} It will timeout in ${TIMEOUT}s"
  SECONDS=0

  while true ; do
    local log=$(docker logs $1 2>&1 | grep "$2")
    if [ -n "$log" ] ; then
      echo $log
      break
    fi

    if [ $SECONDS -ge $TIMEOUT ] ; then
      echo "${log_waiting} TIMEOUT"
      break;
    fi
    sleep 1
  done
}

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