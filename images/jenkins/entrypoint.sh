#!/bin/bash

trap "exit 1" ERR

if [[ -z "${PROXY_URL}" ]]; then
	echo "PROXY_URL cannot be empty" >&2
	exit 1
fi

sed -i -re "s,PROXY_URL,${PROXY_URL}," /usr/share/jenkins/ref/jenkins.model.JenkinsLocationConfiguration.xml.override

exec /usr/local/bin/jenkins.sh "$@"
