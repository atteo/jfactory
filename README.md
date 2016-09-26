# JFactory

## Overview
JFactory is a ready to use CI/CD environment created as a series of a Docker containers. Currently it contains:
* Gerrit for management of the Git repositories
* Jenkins for verifying reviews and continuously deploying ready product
* Nexus for keeping binary files
* Nginx which acts as a proxy to Gerrit, Jenkins and Nexus

## Installation instructions

### Downloading starter repository
First download starter repository. 

```
git clone https://github.com/atteo/jfactory-starter
```

This repository contains docker-compose.yml file for [Docker Compose](https://docs.docker.com/compose/),
the setup script setup.sh and .env file.

### Setting up keys and certificates

To generate default keys and certificates execute:
```
setup.sh
```

### Configuration

Edit '.env' and set the required properties.

### Starting Docker containers

To start all Docker containers:

```
docker-compose up
```


## In-depth description

### HTTP certificates

Gerrit, Jenkins and Nexus are accessible through through the Nginx proxy. Nginx requires valid SSL certificates.
setup.sh script generates self signed certificates. You can use them or you can provide your own key and certificate files
by overwriting two files: certificates/nginx-server.key and certificates/nginx-server.crt.

### Jenkins SSH keys

Jenkins authenticates into Gerrit using SSH key pair. setup.sh generates default keys and there should be no need
to use your own key. The key resides in certificates/jenkins_key and certificates/jenkins_key.pub.
