#!/bin/bash

trap "exit 1" ERR

mkdir -p review_site

docker build -t jfactory .

docker run -v /home/sentinel/projects/jfactory/review_site:/home/gerrit/review_site -p 8080:8080 -it jfactory
