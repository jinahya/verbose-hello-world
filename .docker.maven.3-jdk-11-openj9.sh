#!/bin/sh
docker run -it --rm --name verbose-hello-world -v "$(pwd)":/usr/src/mymaven -v "$HOME/.m2":/root/.m2 -w /usr/src/mymaven maven:3-jdk-11-openj9 "$@"