#!/bin/bash

docker run -v /etc/localtime:/etc/localtime:ro \
	-v /etc/ssl/certs:/etc/ssl/certs:ro \
	-v /usr/share/ca-certificates/:/usr/share/ca-certificates/:ro \
	-v /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security:ro \
	-v jfactory_slave-m2-repository:/home/jenkins/.m2/repository \
	-ti jfactory/java8-slave:2.0

