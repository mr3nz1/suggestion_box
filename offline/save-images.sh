#!/usr/bin/env sh
# Run ONCE while online. Exports every image the stack needs into offline/images.tar
# so the project also works on a machine that has never pulled them.
set -e
cd "$(dirname "$0")/.."
docker pull maven:3.9-eclipse-temurin-21
docker pull tomcat:10.1-jdk21-temurin
docker pull postgres:16-alpine
docker save -o offline/images.tar \
    maven:3.9-eclipse-temurin-21 \
    tomcat:10.1-jdk21-temurin \
    postgres:16-alpine
echo "Saved offline/images.tar"
