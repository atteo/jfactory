# JFactory

http://labs.play-with-docker.com/?stack=https://raw.githubusercontent.com/atteo/jfactory-starter/master/docker-compose.yml

## Overview
JFactory is a ready to use CI/CD environment created as a series of a Docker containers. Currently it contains:
* Gerrit for management of the Git repositories
* Jenkins for verifying reviews and continuously deploying ready product
* Nexus for keeping binary files
* Nginx which acts as a proxy to Gerrit, Jenkins and Nexus

## Quick installation instructions

1. Download starter repository.

```
git clone https://github.com/atteo/jfactory-starter
```

2. Execute:
```
setup.sh
```

3. Edit '.env' file and set at least LDAP_URL and LDAP_ACCOUNT_BASE

4. Start Docker containers:

```
docker-compose up
```

Docker-compose will start Gerrit, Nexus, Jenkins and Nginx.
As Gerrit setup is not finished yet, Jenkins might be unable to connect to Gerrit
and the following error will be shown in the logs:

```
com.sonymobile.tools.gerrit.gerritevents.ssh.SshException: com.jcraft.jsch.JSchException: Auth fail
```

This is normal on first run.

5. Open Gerrit web page:

```
http://172.179.0.1/gerrit
```
Note: URL might be different, if you modified MAIN_URL in .env file

Log in as admin/admin123

6. Add SSH public key

7. From console execute

cat ~/jfactory-ldap-starter/certificates/jenkins_key.pub | ssh -p 29418 -i ~/.ssh/id_rsa admin@localhost gerrit create-account --ssh-key - --http-password admin123 jenkins

Fixes:
	- Jenkins permission in Gerrit (forge identity)
	- REST API connection
	- url to access Jenkins from slaves
	- adding Jenkins user automatically
