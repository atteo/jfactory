FROM jfactory/common-slave:2.1
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

USER root

# nodejs
RUN curl -sL https://deb.nodesource.com/setup_6.x | bash - && rm -rf /var/lib/apt/lists/*
RUN apt-get update && apt-get install -y nodejs bzip2 libfontconfig && rm -rf /var/lib/apt/lists/*

USER jenkins
