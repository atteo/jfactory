FROM java:openjdk-8-jre
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>

ENV GERRIT_VERSION 2.14.1
ENV PLUGIN_VERSION 2.14
ENV GERRIT_HOME "/home/gerrit"
ENV GERRIT_USER "gerrit"

RUN useradd -m -d "$GERRIT_HOME" -U $GERRIT_USER

RUN apt-get update && apt-get install -y git

# Install Gerrit
WORKDIR $GERRIT_HOME
USER "$GERRIT_USER"
RUN curl -f -L https://gerrit-releases.storage.googleapis.com/gerrit-${GERRIT_VERSION}.war -o gerrit.war
RUN mkdir review_site

# Install plugins
RUN mkdir plugins \
	&& curl -f -L https://gerrit-ci.gerritforge.com/job/plugin-delete-project-bazel-stable-${PLUGIN_VERSION}/lastSuccessfulBuild/artifact/bazel-genfiles/plugins/delete-project/delete-project.jar -o plugins/delete-project.jar \
	&& curl -f -L https://gerrit-ci.gerritforge.com/job/plugin-reviewers-bazel-stable-${PLUGIN_VERSION}/lastSuccessfulBuild/artifact/bazel-genfiles/plugins/reviewers/reviewers.jar -o plugins/reviewers.jar \
	&& unzip -j gerrit.war WEB-INF/plugins/download-commands.jar -d plugins

VOLUME /home/gerrit/review_site
EXPOSE 8080

COPY batchuser.jar plugins/
COPY project.config ./
COPY run.sh ./

CMD ./run.sh


