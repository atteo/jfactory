FROM jfactory/common-slave:latest
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

USER root

# nodejs
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash - && rm -rf /var/lib/apt/lists/*
RUN apt-get update && apt-get install -y nodejs bzip2 libfontconfig && rm -rf /var/lib/apt/lists/*

USER jenkins
