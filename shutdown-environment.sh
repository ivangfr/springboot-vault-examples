#!/usr/bin/env bash

echo
echo "Starting the environment shutdown"
echo "================================="

echo
echo "Removing containers"
echo "-------------------"
docker rm -fv mysql mysql-2 cassandra vault consul

echo
echo "Removing network"
echo "----------------"
docker network rm springboot-vault-examples

echo
echo "Environment shutdown successfully"
echo "================================="
echo