#!/bin/bash

trap "exit 1" ERR

if [[ -z "${MAIN_URL}" ]]; then
	echo "MAIN_URL cannot be empty" >&2
	exit 1
fi

sed -i -re "s,MAIN_URL,${MAIN_URL}," /usr/share/jenkins/ref/jenkins.model.JenkinsLocationConfiguration.xml.override

exec /usr/local/bin/jenkins.sh "$@"
