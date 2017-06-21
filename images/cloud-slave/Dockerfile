FROM jfactory/common-slave:2.0
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

# versions
ENV TERRAFORM_VERSION 0.9.8
ENV TERRAFORM_HOME /usr/share/terraform

USER root

# azure
RUN \
    echo "===> add azure repo" && \
    echo "deb [arch=amd64] https://packages.microsoft.com/repos/azure-cli/ wheezy main" | tee /etc/apt/sources.list.d/azure-cli.list && \
    apt-key adv --keyserver packages.microsoft.com --recv-keys 417A0893 && \
    apt-get update && \
    \
    \
    echo "===> install azure-cli and awscli" && \
    apt-get install -y unzip azure-cli awscli && \
    \
    \
    echo "===> clean" && \
    apt-get clean  && \
    rm -rf /var/lib/apt/lists/*

# terraform
RUN mkdir -p $TERRAFORM_HOME \
  && curl -fsSL https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip -o bin.zip \
  && unzip bin.zip -d $TERRAFORM_HOME \
  && ln -s $TERRAFORM_HOME/terraform /usr/bin/terraform \
  && rm bin.zip

USER jenkins
