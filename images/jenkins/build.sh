#!/bin/bash

trap "exit 1" ERR

# go to the script directory
cd "$( dirname "${BASH_SOURCE[0]}" )"

docker build -t jfactory/jenkins:2.0 --build-arg=http_proxy --build-arg=https_proxy --build-arg=no_proxy  .


