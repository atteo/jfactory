#!/bin/bash

java -jar gerrit.war init -d review_site --batch --no-auto-start

mv plugins/* review_site/plugins/

cd review_site

bin/gerrit.sh run
