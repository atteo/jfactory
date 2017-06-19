#!/bin/bash
trap "exit 1" ERR

# go to the script directory
cd "$( dirname "${BASH_SOURCE[0]}" )"

cp ../../batchuser-gerrit-plugin/target/batchuser-gerrit-plugin-1.0.0-SNAPSHOT.jar batchuser.jar

docker build -t jfactory/gerrit:2.0 --build-arg=http_proxy --build-arg=https_proxy --build-arg=no_proxy  .

rm batchuser.jar


