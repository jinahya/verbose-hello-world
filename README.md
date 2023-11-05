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

| artifact id                    | description                           |
|--------------------------------|---------------------------------------|
| `verbose-hello-world`          | the parent module                     |
| `+- verbose-hello-world-api`   | defines the `HelloWorld` interface    |
| `+- verbose-hello-world-lib`   | implements the `HelloWorld` interface |
| `+- verbose-hello-world-app`   | a second level parent module for apps |
| `+-- verbose-hello-world-app1` | a second level parent module for apps |
| `+-- verbose-hello-world-app2` | a second level parent module for apps |
| `+-- verbose-hello-world-app3` | a second level parent module for apps |

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

## Links

### Java

#### doc.oracle.com
* [Networking IPv6 User Guide](https://docs.oracle.com/javase/8/docs/technotes/guides/net/ipv6_guide/)
* [Java Cryptography Architecture / Standard Algorithm Name Documentation for JDK 8](https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html) (javase/8)
* [Java Security Standard Algorithm Names](https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html) (javase/11)
* [Java Security Standard Algorithm Names](https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html) (javase/17)
#### JDK Bug System
* [(aio) AsynchronousCloseException not notified to completion handler when channel group shutdown](https://bugs.openjdk.org/browse/JDK-7056546)

#### stackoverflow.com
* [Multi module POM - creating a site that works](https://stackoverflow.com/q/10848715/330457)
* [How do SO_REUSEADDR and SO_REUSEPORT differ?](https://stackoverflow.com/q/14388706/330457)

#### github.com
* [Add AbstractStringAssert#extracting(Charset)](https://github.com/assertj/assertj/issues/3229) (assertj/assertj)

#### geeksforgeeks.org
* [Difference Between SO_REUSEADDR and SO_REUSEPORT](https://www.geeksforgeeks.org/difference-between-so_reuseaddr-and-so_reuseport/)

#### ibm.com
* [Example: Accepting connections from both IPv6 and IPv4 clients](https://www.ibm.com/docs/en/i/7.1?topic=sscaaiic-example-accepting-connections-from-both-ipv6-ipv4-clients) 

### Python
 
#### pymotw.com
  * [abc — Abstract Base Classes](https://pymotw.com/3/abc/)
