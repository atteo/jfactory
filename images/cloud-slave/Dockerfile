FROM jfactory/common-slave:latest
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

# versions
ENV TERRAFORM_VERSION=0.10.3 \
    TERRAFORM_HOME=/usr/share/terraform \
    DOCKER_COMPOSE_VERSION=1.16.1 \
	# Latest stable: curl -s https://storage.googleapis.com/kubernetes-release/release/stable.tx
	KUBERNETES_VERSION=v1.7.5

USER root

# azure, aws and docker
RUN \
	apt-get update && \
	apt-get install -y gnupg2 apt-transport-https ca-certificates software-properties-common && \
    echo "===> add azure repo" && \
    echo "deb [arch=amd64] https://packages.microsoft.com/repos/azure-cli/ wheezy main" | tee /etc/apt/sources.list.d/azure-cli.list && \
    apt-key adv --keyserver packages.microsoft.com --recv-keys 417A0893 && \
	curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - && \
	add-apt-repository \
	   "deb [arch=amd64] https://download.docker.com/linux/debian \
	      $(lsb_release -cs) \
		  stable" \
    && \
    apt-get -y update && \
    \
    \
    echo "===> install cloud tools" && \
	\ # Install libssl1.0.0 as a workaround for https://github.com/Azure/azure-cli/issues/3863
	wget http://ftp.de.debian.org/debian/pool/main/o/openssl/libssl1.0.0_1.0.2l-1~bpo8+1_amd64.deb && dpkg -i libssl1.0.0_1.0.2l-1~bpo8+1_amd64.deb && rm -f libssl1.0.0_1.0.2l-1~bpo8+1_amd64.deb && \
    apt-get install -y unzip docker-ce azure-cli awscli jq gettext-base netcat-openbsd && \
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
RUN curl -L https://storage.googleapis.com/kubernetes-release/release/${KUBERNETES_VERSION}/bin/linux/amd64/kubectl -o /usr/local/bin/kubectl && \
    chmod +x /usr/local/bin/kubectl

