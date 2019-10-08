#!/usr/bin/env bash

while true
do
  curl -I http://localhost:9081/api/books
	sleep 1
done