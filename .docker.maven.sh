#!/bin/bash
#tags=(3-jdk-11-openj9 3-jdk-11-slim)
#for tag in "${tags[@]}"; do
#  echo $tag
#done
declare -a tags=(
  "3-amazoncretto-11"
  "3-jdk-11-openj9"
  "3-jdk-11-slim"
  "3-amazoncretto-8"
  "3-jdk-8-openj9"
  "3-jdk-8-slim"
)
for tag in "${tags[@]}"; do
  echo -----------------------------------------------------------------------------------------------------------------
  echo $tag
  echo -----------------------------------------------------------------------------------------------------------------
  docker run -it --rm --name verbose-hello-world -v "$(pwd)":/usr/src/mymaven -v "$HOME/.m2":/root/.m2 -w /usr/src/mymaven maven:$tag "$@"
done
