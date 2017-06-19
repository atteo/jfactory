FROM jenkinsci/jenkins:2.65
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

USER root

# Install Docker command line
RUN apt-get update && apt-get install -y apt-transport-https ca-certificates \
	&& apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D \
	&& echo "deb https://apt.dockerproject.org/repo debian-jessie main" > /etc/apt/sources.list.d/docker.list \
	&& apt-get update && apt-get install -y docker-engine && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Install plugins
COPY plugins.txt /plugins.txt
COPY plugins.sh /plugins.sh
RUN /plugins.sh

ENV JENKINS_OPTS --prefix=/jenkins
ENV JENKINS_REF /usr/share/jenkins/ref/

# Skip setup wizard and add workaround for Docker plugin
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false -Djenkins.slaves.DefaultJnlpSlaveReceiver.disableStrictVerification=true -Djava.awt.headless=true
RUN echo "2.0" > $JENKINS_REF/jenkins.install.UpgradeWizard.state \
	&& echo "2.0" > $JENKINS_REF/jenkins.install.InstallUtil.lastExecVersion

# Some initial configuration
COPY config.xml /usr/share/jenkins/ref/
COPY credentials.xml /usr/share/jenkins/ref/
COPY gerrit-trigger.xml /usr/share/jenkins/ref/
COPY jenkins.model.JenkinsLocationConfiguration.xml.override /usr/share/jenkins/ref/
COPY entrypoint.sh /usr/local/bin/
COPY jobs /usr/share/jenkins/ref/jobs

ENTRYPOINT /usr/local/bin/entrypoint.sh
