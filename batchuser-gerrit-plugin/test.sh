#!/bin/bash

trap "exit 1" ERR

GERRIT_VERSION=2.13.2

cd target

if ! [[ -r gerrit.war ]]; then
	curl -C - -L https://gerrit-releases.storage.googleapis.com/gerrit-${GERRIT_VERSION}.war -o gerrit.war
fi

if ! [[ -d review_site/etc ]]; then
	mkdir -p review_site/etc
fi

java -jar gerrit.war init -d review_site --batch --no-auto-start

cp "batchuser-gerrit-plugin-1.0.0-SNAPSHOT.jar" review_site/plugins/batchuser.jar
cp ../gerrit.config review_site/etc

cd review_site

java -jar bin/gerrit.war 'reindex'

bin/gerrit.sh daemon
