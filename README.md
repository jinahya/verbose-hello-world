# verbose-hello-world

A fairly verbose project for `hello, world`.

## Modules

```
verbose-hello-world                    the parent module
|-- verbose-hello-world-api            defines the HelloWorld interface
|-- verbose-hello-world-lib            implements the HelloWorld interface
`-- verbose-hello-world-app            a seond level parent for apps
    |-- verbose-hello-world-app1
    |-- verbose-hello-world-app2
    |-- verbose-hello-world-app3
    |-- verbose-hello-world-srv1
    |-- verbose-hello-world-srv2
    |-- verbose-hello-world-srv3
    `-- verbose-hello-world-srv4
```

|artifact id                   |description                          |
|------------------------------|-------------------------------------|
|`verbose-hello-world`         |the parent module                    |
|`+- verbose-hello-world-api`  |defines the `HelloWorld` interface   |
|`+- verbose-hello-world-lib`  |implements the `HelloWorld` interface|
|`+- verbose-hello-world-app`  |a second level parent module for apps|
|`+-- verbose-hello-world-app1`|a second level parent module for apps|
|`+-- verbose-hello-world-app2`|a second level parent module for apps|
|`+-- verbose-hello-world-app3`|a second level parent module for apps|

## Miscellaneous

### Building with different JDKs using Dockert

```shell script
$ sh ./docker.maven.sh <tag> <phase1> <phase2> ...
```

e.g.

```shell script
$ sh ./docker.maven.sh 3-jdk-11-openj9 clean install
```

See [dockerhub/maven](https://hub.docker.com/_/maven) for available tags.
