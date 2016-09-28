FROM java:openjdk-8-jre
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

ENV GERRIT_VERSION 2.12.1
ENV PLUGIN_VERSION 2.12
ENV GERRIT_HOME "/home/gerrit"
ENV GERRIT_USER "gerrit"

RUN useradd -m -d "$GERRIT_HOME" -U $GERRIT_USER

RUN apt-get update && apt-get install -y git

# Install Gerrit
WORKDIR $GERRIT_HOME
USER "$GERRIT_USER"
RUN curl -L https://gerrit-releases.storage.googleapis.com/gerrit-${GERRIT_VERSION}.war -o gerrit.war
RUN mkdir review_site

# Install bouncy castle
ENV BOUNCY_CASTLE_VERSION 154
ENV BOUNCY_CASTLE_URL https://downloads.bouncycastle.org/java

RUN curl \
	-L ${BOUNCY_CASTLE_URL}/bcprov-jdk15on-${BOUNCY_CASTLE_VERSION}.jar \
	-o ${GERRIT_HOME}/bcprov-jdk15on-${BOUNCY_CASTLE_VERSION}.jar \
	&& curl \
	-L ${BOUNCY_CASTLE_URL}/bcpkix-jdk15on-${BOUNCY_CASTLE_VERSION}.jar \
	-o ${GERRIT_HOME}/bcpkix-jdk15on-${BOUNCY_CASTLE_VERSION}.jar

# Install plugins
RUN mkdir plugins \
	&& curl -L https://gerrit-ci.gerritforge.com/job/plugin-events-log-stable-${PLUGIN_VERSION}/lastSuccessfulBuild/artifact/buck-out/gen/plugins/events-log/events-log.jar -o plugins/events-log.jar \
	&& curl -L https://gerrit-ci.gerritforge.com/job/plugin-delete-project-stable-${PLUGIN_VERSION}/lastSuccessfulBuild/artifact/buck-out/gen/plugins/delete-project/delete-project.jar -o plugins/delete-project.jar \
	&& curl -L https://gerrit-ci.gerritforge.com/job/plugin-reviewers-stable-${PLUGIN_VERSION}/lastSuccessfulBuild/artifact/buck-out/gen/plugins/reviewers/reviewers.jar -o plugins/reviewers.jar \
	&& unzip -j gerrit.war WEB-INF/plugins/download-commands.jar -d plugins

VOLUME /home/gerrit/review_site
EXPOSE 8080

COPY batchuser.jar plugins/
COPY project.config ./
COPY run.sh ./

CMD ./run.sh


