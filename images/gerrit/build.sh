#!/bin/bash
trap "exit 1" ERR

VERSION="$1"

# go to the script directory
cd "$( dirname "${BASH_SOURCE[0]}" )"

cp ../../batchuser-gerrit-plugin/target/batchuser-gerrit-plugin-1.0.0-SNAPSHOT.jar docker/batchuser.jar

rm -rf docker/initial_repositories
cp -r initial_repositories docker/
cd docker/initial_repositories
for project in */; do
	project="${project%/}"

	# Init bare repository
	mkdir -p "${project}.git"
	cd "${project}.git"
	git init --bare
	cd ..

	# Create import repository
	cd "${project}"
	git init
	git add *
	git config user.email "sentinel@atteo.com"
	git config user.name "Sławek Piotrowski"
	git commit -m "Initial import"

	# Push from import to bare
	git push "../${project}.git" master

	# Remove import repository
	cd ..
	rm -rf "${project}"

	# Make sure bare repository is clean
	cd "${project}.git"
	git -c gc.reflogExpire=0 -c gc.reflogExpireUnreachable=0 -c gc.rerereresolved=0 -c gc.rerereunresolved=0 -c gc.pruneExpire=now gc
	cd ..
done
cd ../..

docker build -t jfactory/gerrit:$VERSION --build-arg=http_proxy --build-arg=https_proxy --build-arg=no_proxy  docker/

rm docker/batchuser.jar
rm -rf docker/initial_repositories

