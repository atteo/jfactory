#!/bin/bash
trap "exit 1" ERR

VERSION="${1:-latest}"

# go to the script directory
cd "$( dirname "${BASH_SOURCE[0]}" )"

docker build --build-arg=http_proxy --build-arg=https_proxy --build-arg=no_proxy -t jfactory/java17-with-libreoffice-slave:$VERSION .
