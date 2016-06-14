#!/bin/bash

trap "exit 1" ERR

docker build --build-arg=http_proxy --build-arg=https_proxy --build-arg=no_proxy -t jfactory_slave .
docker build --build-arg=http_proxy --build-arg=https_proxy --build-arg=no_proxy -t jfactory_slave_cm .
