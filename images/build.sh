#!/bin/bash

trap "exit 1" ERR

VERSION="${1:-latest}"

# go to the script directory
cd "$( dirname "${BASH_SOURCE[0]}" )"

gerrit/build.sh $VERSION
jenkins/build.sh $VERSION
proxy/build.sh $VERSION
common-slave/build.sh $VERSION
cloud-slave/build.sh $VERSION
java8-slave/build.sh $VERSION
java10-slave/build.sh $VERSION
java11-slave/build.sh $VERSION
javascript-slave/build.sh $VERSION

