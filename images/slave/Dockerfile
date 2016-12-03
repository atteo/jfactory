FROM buildpack-deps:jessie-scm
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

# versions
ENV MAVEN_VERSION 3.3.9

ENV HOME /home/jenkins
RUN useradd -c "Jenkins user" -d $HOME -m jenkins

# Java 8 from Oracle
RUN \
    echo "===> add webupd8 repository..."  && \
    echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list  && \
    echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list  && \
    apt-get update && \
    apt-get install -y apt-transport-https ca-certificates && \
    #apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886  && \
    apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys EEA14886  && \
    apt-get update && \
    \
    \
    echo "===> install Java"  && \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections  && \
    echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections  && \
    DEBIAN_FRONTEND=noninteractive  apt-get install -y --force-yes oracle-java8-installer oracle-java8-set-default  && \
    \
    \
    echo "===> clean up..."  && \
    rm -rf /var/cache/oracle-jdk8-installer  && \
    apt-get clean  && \
    rm -rf /var/lib/apt/lists/*

# Maven
RUN mkdir -p /usr/share/maven \
  && curl -fsSL https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
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
    && apt-get update && apt-get install -y docker-engine && usermod -aG docker jenkins && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Docker compose
RUN curl -L https://github.com/docker/compose/releases/download/1.9.0/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose && \
	chmod +x /usr/local/bin/docker-compose

COPY ssh_config .ssh/config
RUN chown jenkins.jenkins .ssh/config

RUN mkdir -p .m2/repository && chown -R jenkins.jenkins .m2

ENV MVN_COMPILE_GOALS="clean install" \
    PATH=$PATH:/home/jenkins/bin

COPY mvn-incremental /home/jenkins/bin/
RUN  chmod -R +x /home/jenkins/bin \
     && chown -R jenkins.jenkins /home/jenkins/bin

USER jenkins
