#!/bin/bash

gerrit_config() {
	git config -f etc/gerrit.config "$@"
}

echo "Updating..."
java -jar gerrit.war init -d review_site --batch --no-auto-start

mv plugins/* review_site/plugins/

cd review_site

gerrit_config gerrit.canonicalWebUrl "http://${PROXY_URL}/gerrit"
gerrit_config httpd.listenUrl "proxy-http://*:8080/gerrit/"

echo "Reindexing..."
java -jar bin/gerrit.war 'reindex'

bin/gerrit.sh run
