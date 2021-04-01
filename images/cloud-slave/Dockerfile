FROM jfactory/common-slave:latest
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

# versions
# Latest stable: curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt
ENV TERRAFORM_VERSION=0.14.9 \
    TERRAFORM_HOME=/usr/share/terraform \
    DOCKER_COMPOSE_VERSION=1.28.6 \
	KUBERNETES_VERSION=v1.19.3 \
	ISTIO_VERSION=1.9.1

USER root

# azure, aws, google cloud, docker, helm
RUN \
	apt-get update && \
	apt-get install -y gnupg2 apt-transport-https ca-certificates software-properties-common xmlstarlet python3-yaml && \
    echo "===> add azure repo" && \
    echo "deb [arch=amd64] https://packages.microsoft.com/repos/azure-cli/ stretch main" | tee /etc/apt/sources.list.d/azure-cli.list && \
	curl -L https://packages.microsoft.com/keys/microsoft.asc | apt-key add - && \
	curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - && \
	add-apt-repository \
	   "deb [arch=amd64] https://download.docker.com/linux/debian \
	      $(lsb_release -cs) \
		  stable" \
    && \
	echo "deb http://packages.cloud.google.com/apt cloud-sdk-$(lsb_release -c -s) main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list && \
	curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add - && \
	echo "deb https://baltocdn.com/helm/stable/debian/ all main" | tee /etc/apt/sources.list.d/helm-stable-debian.list && \
	curl https://baltocdn.com/helm/signing.asc | apt-key add - && \
    apt-get -y update && \
    \
    \
    echo "===> install cloud tools" && \
    apt-get install -y unzip docker-ce-cli azure-cli awscli jq gettext-base netcat-openbsd google-cloud-sdk postgresql-client-9.6 helm && \
    \
    \
    echo "===> clean" && \
    apt-get -y clean  && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN echo "docker:x:999:jenkins" >> /etc/group

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

# istioctl
RUN cd /opt &&\
	curl -L https://istio.io/downloadIstio | ISTIO_VERSION="$ISTIO_VERSION" TARGET_ARCH=x86_64 sh - && \
	chmod -R 755 istio-"$ISTIO_VERSION" && \
	chmod -R -x+X istio-"$ISTIO_VERSION" && \
	chmod 755 istio-"$ISTIO_VERSION"/bin/* && \
	ln -s istio-"$ISTIO_VERSION" istio && \
	ln -s /opt/istio/bin/istioctl /usr/local/bin/istioctl

# Trivy
RUN wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | apt-key add - \
	&& echo deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main | tee -a /etc/apt/sources.list.d/trivy.list \
	&& apt-get update \
	&& apt-get -y install trivy

RUN  \
	apt-get update && \
	apt-get install -y socat && \
    echo "===> clean" && \
    apt-get -y clean  && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

EXPOSE 5000 5001 5002
USER jenkins

