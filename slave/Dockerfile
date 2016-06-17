FROM java:8-jdk
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

# versions
ENV MAVEN_VERSION 3.3.3



ENV HOME /home/jenkins
RUN useradd -c "Jenkins user" -d $HOME -m jenkins

# Maven
RUN mkdir -p /usr/share/maven \
  && curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
    | tar -xzC /usr/share/maven --strip-components=1 \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

# nodejs
RUN curl -sL https://deb.nodesource.com/setup_6.x | bash - && rm -rf /var/lib/apt/lists/*
RUN apt-get update && apt-get install -y nodejs && rm -rf /var/lib/apt/lists/*

WORKDIR /home/jenkins

# Docker
RUN apt-get update && apt-get install -y apt-transport-https ca-certificates \
	&& apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D \
	&& echo "deb https://apt.dockerproject.org/repo debian-jessie main" > /etc/apt/sources.list.d/docker.list \
	&& apt-get update && apt-get install -y docker-engine && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

COPY ssh_config .ssh/config
RUN chown jenkins.jenkins .ssh/config

USER jenkins

