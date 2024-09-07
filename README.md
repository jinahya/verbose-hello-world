# verbose-hello-world

[![Java CI with Maven](https://github.com/jinahya/verbose-hello-world/actions/workflows/maven.yml/badge.svg?branch=develop)](https://github.com/jinahya/verbose-hello-world/actions/workflows/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jinahya_verbose-hello-world&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jinahya_verbose-hello-world)

A fairly verbose modules just for generating`hello, world`.

## Modules

| module                                 | description                           |
|----------------------------------------|---------------------------------------|
| `01-verbose-hello-world-api`           | Defines the `HelloWorld` interface    |
| `02-verbose-hello-world-lib`           | Implements the `HelloWorld` interface |
| `03-verbose-hello-world-app`           |                                       |
| `\- 01-verbose-hello-world-app1`       | Uses the `HelloWorldImpl`             |
| `\- 02-verbose-hello-world-app2`       | SPI                                   |
| `\- 03-verbose-hello-world-app3`       | DI                                    |
| `\- 04-verbose-hello-world-app4`       | CDI                                   |
| `04-verbose-hello-world-srv`           |                                       |
| `\- 00-verbose-hello-world-srv-common` |                                       |
| `\- 01-verbose-hello-world-srv1`       |                                       |
| `\- 02-verbose-hello-world-srv2`       |                                       |
| `\- 03-verbose-hello-world-srv3`       |                                       |
| `\- 04-verbose-hello-world-srv4`       |                                       |

## Miscellaneous

### Building with different JDKs using Docker

```shell script
$ sh ./docker.maven.sh <tag> <phase1> <phase2> ...
```

e.g.

```shell script
$ sh ./docker.maven.sh 3-jdk-11-openj9 clean install
```

See [dockerhub/maven](https://hub.docker.com/_/maven) for available tags.

## Links

### [JDK Bug System](https://bugs.openjdk.org)

* [(aio) AsynchronousCloseException not notified to completion handler when channel group shutdown](https://bugs.openjdk.org/browse/JDK-7056546)

### [Help Center](https://docs.oracle.com) (docs.oracle.com)

* [Networking IPv6 User Guide](https://docs.oracle.com/javase/8/docs/technotes/guides/net/ipv6_guide/)
* [Java Cryptography Architecture / Standard Algorithm Name Documentation for JDK 8](https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html) (javase/8)
* [Java Security Standard Algorithm Names](https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html) (javase/11)
* [Java Security Standard Algorithm Names](https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html) (javase/17)
* [Java Security Standard Algorithm Names](https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html) (javase/21)

### [DZone](https://dzone.com)

* [7 Popular Unit Test Naming Conventions](https://dzone.com/articles/7-popular-unit-test-naming)

### [GeeksforGeeks](https://geeksforgeeks.org)

* [Difference Between SO_REUSEADDR and SO_REUSEPORT](https://www.geeksforgeeks.org/difference-between-so_reuseaddr-and-so_reuseport/)

### [AssertJ](https://github.com/assertj/assertj) (GitHub)

* [Add bytes()/bytes(Charset)/bytes(String) navigation methods to AbstractStringAssert #3229
  ](https://github.com/assertj/assertj/issues/3229) (assertj/assertj)

### [Dagger](https://github.com/google/dagger) (GitHub)

* [jakarta.inject support #2058](https://github.com/google/dagger/issues/2058) (assertj/assertj)

### [JUnit](https://github.com/junit-team) (GitHub)

#### [JUnit 5](https://github.com/junit-team/junit5)

*
* [TestInstanceFactory on enclosing class is not called for @Nested test class #1567](https://github.com/junit-team/junit5/issues/1567)

### [ReactiveX](https://github.com/ReactiveX) (GitHub)

#### [RxJava: Reactive Extensions for the JVM](https://github.com/ReactiveX/RxJava) (GitHub)

* [Reactive Streams](https://github.com/ReactiveX/RxJava/wiki/Reactive-Streams)

#### [RxJava Reactive Streams](https://github.com/ReactiveX/RxJavaReactiveStreams)

### [Weld](https://github.com/weld)

#### [Weld Testing Extensions](https://github.com/weld/weld-testing) (GitHub)

* [Weld JUnit 5 (Jupiter) Extensions](https://github.com/weld/weld-testing/blob/master/junit5/README.md#weldjunit5autoextension)

### [IBM Documentation](https://www.ibm.com/docs/en)

* [Example: Accepting connections from both IPv6 and IPv4 clients](https://www.ibm.com/docs/en/i/7.1?topic=sscaaiic-example-accepting-connections-from-both-ipv6-ipv4-clients)

### [Apache OpenWebBeans](https://openwebbeans.apache.org/)

* [OpenWebBeans JUnit 5](https://openwebbeans.apache.org/openwebbeans-junit5.html)

### [stackoverflow](https://stackoverflow.com/)

* [Multi module POM - creating a site that works](https://stackoverflow.com/q/10848715/330457)
* [How do SO_REUSEADDR and SO_REUSEPORT differ?](https://stackoverflow.com/q/14388706/330457)
* [JUL to SLF4J Bridge](https://stackoverflow.com/q/9117030/330457)
* [What is the minimum number of written bytes of a SocketChannel when its key is writable?](https://stackoverflow.com/q/77895312/330457)

### [Python 3... documentation](https://docs.python.org/3/)

* [`base64` — Base16, Base32, Base64, Base85 Data Encodings](https://docs.python.org/3/library/base64.html)

* [`hashlib` — Secure hashes and message digests](https://docs.python.org/3/library/hashlib.html)

### [Python 3 Module of the Week](https://pymotw.com/3/)

* [abc — Abstract Base Classes](https://pymotw.com/3/abc/)

### ETC

* [Java: Remainder (modulo) operator with negative numbers](https://programming.guide/java/remainder-modulo-operator-negative-numbers.html)
