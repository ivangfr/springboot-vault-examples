#!/usr/bin/env bash

source scripts/my-functions.sh

check_script_input_parameter $1

if [ "$1" = "spring-vault-approle-mysql" ] ||
   [ "$1" = "all" ];
then

  echo
  echo "--------------------------"
  echo "spring-vault-approle-mysql"
  echo "--------------------------"

  ./mvnw clean compile jib:dockerBuild --projects spring-vault-approle-mysql/movie-service

fi

if [ "$1" = "spring-vault-approle-multi-datasources-mysql" ] ||
   [ "$1" = "all" ];
then

  echo
  echo "--------------------------------------------"
  echo "spring-vault-approle-multi-datasources-mysql"
  echo "--------------------------------------------"

  ./mvnw clean compile jib:dockerBuild --projects spring-vault-approle-multi-datasources-mysql/restaurant-service

fi

if [ "$1" = "spring-cloud-vault-approle-mysql" ] ||
   [ "$1" = "all" ];
then

  echo
  echo "--------------------------------"
  echo "spring-cloud-vault-approle-mysql"
  echo "--------------------------------"

  ./mvnw clean compile jib:dockerBuild --projects spring-cloud-vault-approle-mysql/student-service

fi

if [ "$1" = "spring-cloud-vault-approle-cassandra" ] ||
   [ "$1" = "all" ];
then

  echo
  echo "------------------------------------"
  echo "spring-cloud-vault-approle-cassandra"
  echo "------------------------------------"

  ./mvnw clean compile jib:dockerBuild --projects spring-cloud-vault-approle-cassandra/book-service

fi
