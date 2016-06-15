FROM jenkins:2.3
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

USER root

COPY plugins.txt /plugins.txt
RUN /usr/local/bin/plugins.sh /plugins.txt

RUN apt-get update && apt-get install -y apt-transport-https ca-certificates \
	&& apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D \
	&& echo "deb https://apt.dockerproject.org/repo debian-jessie main" > /etc/apt/sources.list.d/docker.list \
	&& apt-get update && apt-get install -y docker-engine && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

ENV JENKINS_OPTS --prefix=/jenkins
ENV JENKINS_REF /usr/share/jenkins/ref/

# Skip setup wizard
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
RUN echo "2.0" > $JENKINS_REF/jenkins.install.UpgradeWizard.state \
	&& echo "2.0" > $JENKINS_REF/jenkins.install.InstallUtil.lastExecVersion


