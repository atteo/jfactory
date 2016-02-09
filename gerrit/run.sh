#!/bin/bash

gerrit_config() {
	git config -f etc/gerrit.config "$@"
}

configure_http() {
	gerrit_config gerrit.canonicalWebUrl "${PROXY_URL}/gerrit"
	gerrit_config httpd.listenUrl "proxy-http://*:8080/gerrit/"
}

configure_ldap() {
	gerrit_config auth.type "LDAP"
	gerrit_config ldap.server "${LDAP_URL}"
	gerrit_config ldap.sslVerify "false"
	gerrit_config ldap.accountBase "${LDAP_ACCOUNT_BASE}"
	#gerrit_config ldap.accountScope "one"
	gerrit_config ldap.groupBase "${LDAP_GROUP_BASE}"
}

configure_smtp() {
	if [[ -z "${SMTP_SERVER}" ]]; then
		gerrit_config sendemail.enable "false"
		return
	fi

	gerrit_config sendemail.enable "true"
	gerrit_config sendemail.smtpServer "${SMTP_SERVER}"
	gerrit_config sendemail.smtpServerPort "${SMTP_SERVER_PORT:-25}"
	gerrit_config sendemail.from "${SMTP_FROM}"
}

configure_linking() {
	if [[ -z "${JIRA_URL}" ]]; then
		return;
	fi

	gerrit_config commentlink.jira.match "([A-Z]+-[0-9]+)"
	gerrit_config commentlink.jira.link "${JIRA_URL}/browse/\$1"
}

configure_download_plugin() {
	gerrit_config download.scheme "http"
	gerrit_config download.command "checkout"
}

echo "Updating..."
java -jar gerrit.war init -d review_site --batch --no-auto-start

mv plugins/* review_site/plugins/

cd review_site

configure_http
configure_ldap
configure_smtp
configure_linking
configure_download_plugin

echo "Reindexing..."
java -jar bin/gerrit.war 'reindex'

bin/gerrit.sh run

