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
RUN curl -sL https://deb.nodesource.com/setup_6.x | bash -
RUN apt-get install -y nodejs

WORKDIR /home/jenkins
USER jenkins

