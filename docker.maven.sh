#!/bin/sh
if [ $# -lt 3 ]; then
  echo "Usage: $1 <tag> <goal1> <goal2> ..."
  exit 1
fi
tag="$1"
shift
docker run -it --rm --name verbose-hello-world -v "$(pwd)":/usr/src/mymaven -v "$HOME/.m2":/root/.m2 -w /usr/src/mymaven maven:"$tag" mvn "$@"
