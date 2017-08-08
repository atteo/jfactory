#!/bin/bash

trap "exit 1" ERR

# go to the script directory
cd "$( dirname "${BASH_SOURCE[0]}" )"

cd batchuser-gerrit-plugin
mvn clean install
cd ..

images/build.sh "${1:-latest}"



