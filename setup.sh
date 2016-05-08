#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
JENKINS_DIR="data/jenkins-volume"
GERRIT_DIR="data/gerrit-volume"

createJenkinsSshKey() {
	local KEY_NAME="jenkins_ssh_key"

	echo -n "Jenkins public key: "

	if [[ -r "${GERRIT_DIR}/${KEY_NAME}.pub" ]]; then
		echo "already exists"
		return;
	fi

	rm -f "${JENKINS_DIR}/${KEY_NAME}" "${JENKINS_DIR}/${KEY_NAME}.pub"
	ssh-keygen -N "" -f "${JENKINS_DIR}/${KEY_NAME}"

	mv "${JENKINS_DIR}/${KEY_NAME}.pub" "${GERRIT_DIR}/"
	echo "created"
}

cd "$SCRIPT_DIR"

createJenkinsSshKey
