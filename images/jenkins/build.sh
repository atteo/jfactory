#!/bin/bash

trap "exit 1" ERR

VERSION="$1"

# go to the script directory
cd "$( dirname "${BASH_SOURCE[0]}" )"

docker build -t jfactory/jenkins:$VERSION --build-arg=http_proxy --build-arg=https_proxy --build-arg=no_proxy  .


