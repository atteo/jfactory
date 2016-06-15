#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
JENKINS_DIR="data/jenkins-volume"
JENKINS_KEY_NAME="jenkins_ssh_key"
JENKINS_SLAVE_DIR="data/slave"
GERRIT_DIR="data/gerrit-volume"

GOSERVER_DIR="data/goserver-volume/home"

createJenkinsSshKey() {

	echo -n "Jenkins public key: "

	if [[ -r "${GERRIT_DIR}/${JENKINS_KEY_NAME}.pub" ]] && [[ -r "${JENKINS_DIR}/${JENKINS_KEY_NAME}" ]]; then
		echo "already exists"
		return;
	fi

	rm -f "${JENKINS_DIR}/${JENKINS_KEY_NAME}" "${JENKINS_DIR}/${JENKINS_KEY_NAME}.pub"
	ssh-keygen -N "" -f "${JENKINS_DIR}/${JENKINS_KEY_NAME}"
	
	mv "${JENKINS_DIR}/${JENKINS_KEY_NAME}.pub" "${GERRIT_DIR}/"
	cp "${JENKINS_DIR}/${JENKINS_KEY_NAME}" "${JENKINS_SLAVE_DIR}/"
	echo "created"
}

cd "$SCRIPT_DIR"

createJenkinsSshKey

