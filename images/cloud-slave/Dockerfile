FROM jfactory/common-slave:latest
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

# versions
ENV TERRAFORM_VERSION=0.10.0 \
    TERRAFORM_HOME=/usr/share/terraform \
    DOCKER_COMPOSE_VERSION=1.15.0

USER root

# azure, aws and docker
RUN \
	apt-get update && \
	apt-get install -y gnupg apt-transport-https ca-certificates && \
    echo "===> add azure repo" && \
    echo "deb [arch=amd64] https://packages.microsoft.com/repos/azure-cli/ wheezy main" | tee /etc/apt/sources.list.d/azure-cli.list && \
    apt-key adv --keyserver packages.microsoft.com --recv-keys 417A0893 && \
    \
    \
    echo "===> add docker repo" && \
	echo "deb https://apt.dockerproject.org/repo debian-stretch main" > /etc/apt/sources.list.d/docker.list && \
	curl -L https://apt.dockerproject.org/gpg | apt-key add - && \
    \
    apt-get -y update && \
    \
    \
    echo "===> install cloud tools" && \
    apt-get install -y unzip docker-engine azure-cli awscli jq gettext-base netcat-openbsd && \
    usermod -aG docker jenkins && \
    \
    \
    echo "===> clean" && \
    apt-get -y clean  && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# terraform
RUN mkdir -p $TERRAFORM_HOME \
  && curl -fsSL https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip -o bin.zip \
  && unzip bin.zip -d $TERRAFORM_HOME \
  && ln -s $TERRAFORM_HOME/terraform /usr/bin/terraform \
  && rm bin.zip

# Docker compose
RUN curl -L https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose && \
	chmod +x /usr/local/bin/docker-compose

# Kubernetes
RUN curl -L https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl -o /usr/local/bin/kubectl && \
    chmod +x /usr/local/bin/kubectl

