#!/usr/bin/env bash

source my-functions.sh

check_script_input_parameter $1

if [ "$1" = "spring-vault-approle-mysql" ] ||
   [ "$1" = "all" ];
then

  docker rmi ivanfranchin/movie-service:1.0.0

fi

if [ "$1" = "spring-vault-approle-multi-datasources-mysql" ] ||
   [ "$1" = "all" ];
then

  docker rmi ivanfranchin/restaurant-service:1.0.0

fi

if [ "$1" = "spring-cloud-vault-approle-mysql" ] ||
   [ "$1" = "all" ];
then

  docker rmi ivanfranchin/student-service:1.0.0

fi

if [ "$1" = "spring-cloud-vault-approle-cassandra" ] ||
   [ "$1" = "all" ];
then

  docker rmi ivanfranchin/book-service:1.0.0

fi

if [ "$1" = "all" ];
then

  docker rmi springboot-vault-examples_cassandra

fi
