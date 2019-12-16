# verbose-hello-world

A fairly verbose project for `hello, world`.

## Modules

|artifact id                                      |description|
|-------------------------------------------------|-----|
|`verbose-hello-world`                            |the parent module             |
|&nbsp;&nbsp;&nbsp;&nbsp;`verbose-hello-world-api`|defines `HelloWorld` interface|
|&nbsp;&nbsp;&nbsp;&nbsp;`verbose-hello-world-lib`|implements the `HelloWorld` interface|
|&nbsp;&nbsp;&nbsp;&nbsp;`verbose-hello-world-app`|a second level parent module for apps|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`verbose-hello-world-app1`|a second level parent module for apps|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`verbose-hello-world-app2`|a second level parent module for apps|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`verbose-hello-world-app3`|a second level parent module for apps|



## Building in different JDKs using Docker

```shell script
$ sh ./docker.maven.sh <tag> <phase1> <phase2> ...
```

e.g.
```shell script
$ sh ./docker.maven.sh 3-jdk-11-openj9 clean install
```

See [dockerhub/maven](https://hub.docker.com/_/maven) for available tags.