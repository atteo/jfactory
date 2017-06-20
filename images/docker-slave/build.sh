#!/bin/bash
trap "exit 1" ERR

# go to the script directory
cd "$( dirname "${BASH_SOURCE[0]}" )"

docker build --build-arg=http_proxy --build-arg=https_proxy --build-arg=no_proxy -t jfactory/docker-slave:2.0 .
