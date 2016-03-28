FROM       java:8-jdk
MAINTAINER Sławek Piotrowski <sentinel@atteo.com>


ENV NEXUS_VERSION 3.0.0-m7
ENV NEXUS_FILE b2016011501

ENV NEXUS_DIRECTORY "/opt/sonatype/nexus"

RUN mkdir -p ${NEXUS_DIRECTORY} \
  && curl --fail --location --retry 3 \
http://download.sonatype.com/nexus/3/nexus-${NEXUS_VERSION}-unix.tar.gz \
  | tar xvz \
  && cp -r /nexus-3.0.0-${NEXUS_FILE}/. ${NEXUS_DIRECTORY} \
  && rm -rf nexus-3.0.0-${NEXUS_FILE}


VOLUME ${NEXUS_DIRECTORY}/data

EXPOSE 8081
WORKDIR ${NEXUS_DIRECTORY}
USER root
ENV CONTEXT_PATH /
CMD ./bin/nexus run
