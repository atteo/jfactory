FROM java:openjdk-8-jre
MAINTAINER Sławek Piotrowski

ENV GERRIT_VERSION 2.12
ENV GERRIT_HOME "/home/gerrit"
ENV GERRIT_USER "gerrit"

RUN useradd -m -d "$GERRIT_HOME" -U $GERRIT_USER

RUN apt-get update && apt-get install -y git


# Install Gerrit
WORKDIR $GERRIT_HOME
USER "$GERRIT_USER"
RUN curl -L https://gerrit-releases.storage.googleapis.com/gerrit-${GERRIT_VERSION}.war -o gerrit.war
RUN mkdir review_site

# Install plugins
RUN mkdir plugins
RUN curl -L https://gerrit-ci.gerritforge.com/job/plugin-events-log-stable-2.12/lastSuccessfulBuild/artifact/buck-out/gen/plugins/events-log/events-log.jar -o plugins/events-log.jar
RUN curl -L https://gerrit-ci.gerritforge.com/job/plugin-delete-project-stable-2.12/lastSuccessfulBuild/artifact/buck-out/gen/plugins/delete-project/delete-project.jar -o plugins/delete-project.jar
RUN curl -L https://gerrit-ci.gerritforge.com/job/plugin-reviewers-stable-2.12/lastSuccessfulBuild/artifact/buck-out/gen/plugins/reviewers/reviewers.jar -o plugins/reviewers.jar
RUN curl -L https://gerrit-ci.gerritforge.com/job/plugin-branch-network-stable-2.12/lastSuccessfulBuild/artifact/buck-out/gen/plugins/branch-network/branch-network.jar -o plugins/branch-network.jar

VOLUME /home/gerrit/review_site
EXPOSE 8080

COPY run.sh ./

CMD ./run.sh


