#!/usr/bin/env bash

while true
do
  curl -s -I http://localhost:9080/api/students
	sleep 1
done