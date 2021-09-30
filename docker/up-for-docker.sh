#!/bin/bash
# Program:
#   This program show docker image status.
# Parameters:
#   $1 container-name,such as plg-wms-basic
#   $2 image-name,such as plg-eureka:1.0.0

containerName=$1
version=$2
env=$3

existContainer=`docker ps --format "{{.Names}}" | grep -w "$containerName" | head -n 1`
if [ -n "$existContainer" ]; then
  imageName=`docker ps --format "{{.Image}} {{.Names}}" | grep -w "$containerName" | awk '{ print $1 }' `
  echo '=== Exist container '"$existContainer : $imageName"
  docker stop "$existContainer"
fi

existStopContainer=`docker ps -a --format "{{.Names}}" | grep -w "$containerName" | head -n 1`
if [ -n "$existStopContainer" ]; then
  echo "=== Remove container ""$existStopContainer"
  docker rm "$existStopContainer"
fi

if [ -n "$imageName" ]; then
  existImage=`docker images --format "{{.Repository}}:{{.Tag}}" | grep -w "$imageName" | head -n 1`
  if [ -n "$existImage" ]; then
    echo "=== Remove image ""$existImage"
    docker rmi "$imageName"
  fi
fi

existNewImage=`docker images --format "{{.Repository}}:{{.Tag}}" | grep -w "$containerName":"$version" | head -n 1`
if [ -n "$existNewImage" ]; then
  echo "=== Remove new image which exist ""$existNewImage"
  docker rmi "$existNewImage"
fi

existNewImage=`docker images --format "{{.Repository}}:{{.Tag}}" | grep -w "$containerName":"$version" | head -n 1`
if [ -z "$existNewImage" ]; then
  docker build --build-arg RUN_ENV="$env" -t "$containerName":"$version" .
fi

existNewImage=`docker images --format "{{.Repository}}:{{.Tag}}" | grep -w "$containerName":"$version" | head -n 1`
if [ -n "$existNewImage" ]; then
  echo "=== Run image ""$version"
  docker run --network host --restart=always --name "$containerName" -v /data/logs:/usr/local/logs -v /data/upload:/usr/local/upload -d "$containerName":"$version"
fi

echo "=== over ==="
exit 0