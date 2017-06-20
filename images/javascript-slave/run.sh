#!/bin/bash

docker run -v /etc/localtime:/etc/localtime:ro \
	-v /etc/ssl/certs:/etc/ssl/certs:ro \
	-v /usr/share/ca-certificates/:/usr/share/ca-certificates/:ro \
	-ti jfactory/javascript-slave:2.0

