#!/usr/bin/env bash

source scripts/my-functions.sh

check_script_input_parameter $1

DOCKER_IMAGE_PREFIX="ivanfranchin"
APP_VERSION="1.0.0"

MOVIE_SERVICE_APP_NAME="movie-service"
RESTAURANT_SERVICE_APP_NAME="restaurant-service"
STUDENT_SERVICE_APP_NAME="student-service"
BOOK_SERVICE_APP_NAME="book-service"

MOVIE_SERVICE_PROJECT_NAME="spring-vault-approle-mysql/${MOVIE_SERVICE_APP_NAME}"
RESTAURANT_SERVICE_PROJECT_NAME="spring-vault-approle-multi-datasources-mysql/${RESTAURANT_SERVICE_APP_NAME}"
STUDENT_SERVICE_PROJECT_NAME="spring-cloud-vault-approle-mysql/${STUDENT_SERVICE_APP_NAME}"
BOOK_SERVICE_PROJECT_NAME="spring-cloud-vault-approle-cassandra/${BOOK_SERVICE_APP_NAME}"

MOVIE_SERVICE_DOCKER_IMAGE_NAME="${DOCKER_IMAGE_PREFIX}/${MOVIE_SERVICE_APP_NAME}:${APP_VERSION}"
RESTAURANT_SERVICE_DOCKER_IMAGE_NAME="${DOCKER_IMAGE_PREFIX}/${RESTAURANT_SERVICE_APP_NAME}:${APP_VERSION}"
STUDENT_SERVICE_DOCKER_IMAGE_NAME="${DOCKER_IMAGE_PREFIX}/${STUDENT_SERVICE_APP_NAME}:${APP_VERSION}"
BOOK_SERVICE_DOCKER_IMAGE_NAME="${DOCKER_IMAGE_PREFIX}/${BOOK_SERVICE_APP_NAME}:${APP_VERSION}"

SKIP_TESTS="true"

if [ "$1" = "spring-vault-approle-mysql" ] ||
   [ "$1" = "all" ];
then

  echo
  echo "--------------------------"
  echo "spring-vault-approle-mysql"
  echo "--------------------------"

  ./mvnw clean compile jib:dockerBuild \
    --projects "$MOVIE_SERVICE_PROJECT_NAME" \
    -DskipTests="$SKIP_TESTS" \
    -Dimage="$MOVIE_SERVICE_DOCKER_IMAGE_NAME"

fi

if [ "$1" = "spring-vault-approle-multi-datasources-mysql" ] ||
   [ "$1" = "all" ];
then

  echo
  echo "--------------------------------------------"
  echo "spring-vault-approle-multi-datasources-mysql"
  echo "--------------------------------------------"

  ./mvnw clean compile jib:dockerBuild \
    --projects "$RESTAURANT_SERVICE_PROJECT_NAME" \
    -DskipTests="$SKIP_TESTS" \
    -Dimage="$RESTAURANT_SERVICE_DOCKER_IMAGE_NAME"

fi

if [ "$1" = "spring-cloud-vault-approle-mysql" ] ||
   [ "$1" = "all" ];
then

  echo
  echo "--------------------------------"
  echo "spring-cloud-vault-approle-mysql"
  echo "--------------------------------"

  ./mvnw clean compile jib:dockerBuild \
    --projects "$STUDENT_SERVICE_PROJECT_NAME" \
    -DskipTests="$SKIP_TESTS" \
    -Dimage="$STUDENT_SERVICE_DOCKER_IMAGE_NAME"

fi

if [ "$1" = "spring-cloud-vault-approle-cassandra" ] ||
   [ "$1" = "all" ];
then

  echo
  echo "------------------------------------"
  echo "spring-cloud-vault-approle-cassandra"
  echo "------------------------------------"

  ./mvnw clean compile jib:dockerBuild \
    --projects "$BOOK_SERVICE_PROJECT_NAME" \
    -DskipTests="$SKIP_TESTS" \
    -Dimage="$BOOK_SERVICE_DOCKER_IMAGE_NAME"

fi
