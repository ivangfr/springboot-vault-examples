#!/usr/bin/env bash

while true
do
  curl -I http://localhost:9083/api/customers & curl -I http://localhost:9083/api/dishes
	sleep 1
done