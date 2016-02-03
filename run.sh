#!/bin/bash

java -jar gerrit.war init -d review_site --batch --no-auto-start

cd review_site

bin/gerrit.sh run
