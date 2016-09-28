# JFactory

## Overview
JFactory is a ready to use CI/CD environment created as a series of a Docker containers. Currently it contains:
* Gerrit for management of the Git repositories
* Jenkins for verifying reviews and continuously deploying ready product
* Nexus for keeping binary files
* Nginx which acts as a proxy to Gerrit, Jenkins and Nexus

## Quick installation instructions

Warning: Docker 1.12 is currently not supported. Please use version 1.11.2.

Download starter repository.

```
git clone https://github.com/atteo/jfactory-starter
```
Execute:
```
setup.sh
```

Edit '.env' file and set at least LDAP_URL and LDAP_ACCOUNT_BASE

Start Docker containers:

```
docker-compose up
```

Open Gerrit web page

```
http://172.179.0.1/gerrit
```
Note: URL might be different, if you modified MAIN_URL in .env file

## In-depth installation description

### Using HTTPS

Nginx proxy can optionally use HTTPS instead of plain HTTP. To switch to HTTPS change MAIN_URL property in '.env' file from http:// to https:// .
Using HTTPS requires valid SSL certificates. By default self-signed certificates generates by setup.sh script are used.
You can provide your own key and certificate files by overwriting the files in which they are stored:
certificates/nginx-server.key and certificates/nginx-server.crt.

Users need to have the server certificate imported as a trusted certificate in order for git commands to work without issuing an error. 

### Jenkins SSH keys

Jenkins authenticates into Gerrit using SSH key pair. setup.sh generates default keys and there should be no need
to use your own key. The key resides in certificates/jenkins_key and certificates/jenkins_key.pub.

## Common issues

### Not trusted SSL certificate
If you receive the following error when cloning the repo
```
fatal: unable to access 'https://..../gerrit/example/': server certificate verification failed
```
the SSL certificate for HTTPS is not trusted on your machine. Import the certificates in your OS store.
For ubuntu see [this link](https://superuser.com/questions/437330/how-do-you-add-a-certificate-authority-ca-to-ubuntu).
