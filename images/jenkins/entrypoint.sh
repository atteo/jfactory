#!/bin/bash

trap "exit 1" ERR

if [[ -z "${MAIN_URL}" ]]; then
	echo "MAIN_URL cannot be empty" >&2
	exit 1
fi

if [[ -z "${JENKINS_SMTP_FROM}" ]]; then
	echo "JENKINS_SMTP_FROM cannot by empty" >&2
	exit 1
fi

sed -i -re "s,MAIN_URL,${MAIN_URL},;s,JENKINS_SMTP_FROM,${JENKINS_SMTP_FROM}," /usr/share/jenkins/ref/jenkins.model.JenkinsLocationConfiguration.xml.override

exec /sbin/tini -- /usr/local/bin/jenkins.sh "$@"
