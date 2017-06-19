#!/bin/bash

trap "exit 1" ERR

# go to the script directory
cd "$( dirname "${BASH_SOURCE[0]}" )"

gerrit/build.sh
jenkins/build.sh
proxy/build.sh
slave/build.sh

