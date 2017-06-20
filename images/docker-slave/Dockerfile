FROM jfactory/common-slave:2.0
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

USER root

# Docker
RUN apt-get update && apt-get install -y apt-transport-https ca-certificates \
    && apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D \
    && echo "deb https://apt.dockerproject.org/repo debian-jessie main" > /etc/apt/sources.list.d/docker.list \
    && apt-get update && apt-get install -y docker-engine && usermod -aG docker jenkins && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Docker compose
RUN curl -L https://github.com/docker/compose/releases/download/1.13.0/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose && \
	chmod +x /usr/local/bin/docker-compose

USER jenkins
