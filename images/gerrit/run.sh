#!/bin/bash

gerrit_config() {
	git config -f etc/gerrit.config "$@"
}

configure_http() {
	gerrit_config gerrit.canonicalWebUrl "${PROXY_URL}/gerrit"
	if [[ "${PROXY_URL}" == "https"* ]]; then
		gerrit_config httpd.listenUrl "proxy-https://*:8080/gerrit/"
	else
		gerrit_config httpd.listenUrl "proxy-http://*:8080/gerrit/"
	fi
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

configure_download_schemes() {
	gerrit_config --replace-all download.scheme "http"
	gerrit_config --add download.scheme "ssh"
	gerrit_config download.command "checkout"
}

configure_gc() {
	gerrit_config gc.startTime "Fri 2:30"
	gerrit_config gc.interval "7 day"
}

configure_suggest() {
	gerrit_config suggest.fullTextSearch "true"
}

configure_batchuser() {
	gerrit_config plugin.batchuser.username "jenkins"
	gerrit_config plugin.batchuser.name "Jenkins"
	gerrit_config plugin.batchuser.sshKey "$(cat jenkins_key.pub)"
}

configure_ssh() {
	gerrit_config sshd.maxConnectionsPerUser "0"
}

echo "Updating..."
java -jar gerrit.war init -d review_site --batch --no-auto-start

mv plugins/* review_site/plugins/

cd review_site

# Install the Bouncy Castle
cp -f ${GERRIT_HOME}/bcprov-jdk15on-${BOUNCY_CASTLE_VERSION}.jar lib/bcprov-jdk15on-${BOUNCY_CASTLE_VERSION}.jar
cp -f ${GERRIT_HOME}/bcpkix-jdk15on-${BOUNCY_CASTLE_VERSION}.jar lib/bcpkix-jdk15on-${BOUNCY_CASTLE_VERSION}.jar

configure_http
configure_ldap
configure_smtp
configure_linking
configure_download_schemes
configure_gc
configure_suggest
configure_batchuser
configure_ssh

echo "Reindexing..."
java -jar bin/gerrit.war 'reindex'

exec bin/gerrit.sh run

