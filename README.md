# verbose-hello-world

[![Java CI with Maven](https://github.com/jinahya/verbose-hello-world/actions/workflows/maven.yml/badge.svg)](https://github.com/jinahya/verbose-hello-world/actions/workflows/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jinahya_verbose-hello-world&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jinahya_verbose-hello-world)

A fairly verbose `hello, world` project, which explores (almost) all Java I/O APIs with the 12-byte `hello, world`
payload.

Too verbose? See [succinct-hello-world](https://github.com/jinahya/succinct-hello-world).

## Modules

| module                            | description                                                   |
|-----------------------------------|---------------------------------------------------------------|
| `01-verbose-hello-world-api`      | Defines the `HelloWorld` interface                            |
| `02-verbose-hello-world-lib`      | Implements the `HelloWorld` interface                         |
| `03-verbose-hello-world-app`      | Prints `hello, world`                                         |
| `├─ 01-verbose-hello-world-appa`  | &nbsp;&nbsp;-&nbsp;with the `HelloWorldImpl`, directly        |
| `├─ 02-verbose-hello-world-appb`  | &nbsp;&nbsp;-&nbsp;via SPI (Service Provider Interface)       |
| `├─ 03-verbose-hello-world-appc`  | &nbsp;&nbsp;-&nbsp;via DI (Dependency Injection)              |
| `└─ 04-verbose-hello-world-appd`  | &nbsp;&nbsp;-&nbsp;via CDI (Contexts and Dependency Injection) |

The [`companion-chapters`](companion-chapters) directory holds the chapters the book leaves out, rendered as PDF and
HTML.

## Build

Requires Java 27 (with `--enable-preview`, already configured) and Maven 3.6.3+ (the wrapper is included).

```shell
$ ./mvnw clean install
```

## Issues to solve

* [`hello-world-api`](https://github.com/jinahya/verbose-hello-world/issues/4)
* [`hello-world-lib`](https://github.com/jinahya/verbose-hello-world/issues/12)
* [`hello-world-app`](https://github.com/jinahya/verbose-hello-world/issues/13)

## Links

### [JDK Bug System](https://bugs.openjdk.org)

* [(aio) AsynchronousCloseException not notified to completion handler when channel group shutdown](https://bugs.openjdk.org/browse/JDK-7056546)

### [Oracle Help Center](https://docs.oracle.com)

* [Networking IPv6 User Guide](https://docs.oracle.com/javase/8/docs/technotes/guides/net/ipv6_guide/) (javase/8)
* [Java Security Standard Algorithm Names](https://docs.oracle.com/en/java/javase/27/docs/specs/security/standard-names.html) (javase/27)

### [IBM Documentation](https://www.ibm.com/docs/en)

* [Example: Accepting connections from both IPv6 and IPv4 clients](https://www.ibm.com/docs/en/i/7.1?topic=sscaaiic-example-accepting-connections-from-both-ipv6-ipv4-clients)

### [Reactive Streams](https://github.com/reactive-streams/reactive-streams-jvm) (GitHub)

### [JUnit](https://github.com/junit-team/junit-framework) (GitHub)

* [TestInstanceFactory on enclosing class is not called for @Nested test class #1567](https://github.com/junit-team/junit-framework/issues/1567)

### [Dagger](https://github.com/google/dagger) (GitHub)

* [jakarta.inject support #2058](https://github.com/google/dagger/issues/2058)

### [Weld Testing Extensions](https://github.com/weld/weld-testing) (GitHub)

* [WeldJunit5AutoExtension](https://github.com/weld/weld-testing/blob/main/junit-jupiter/README.md#weldjunit5autoextension)

### [Apache OpenWebBeans](https://openwebbeans.apache.org/)

* [OpenWebBeans JUnit 5](https://openwebbeans.apache.org/openwebbeans-junit5.html)

### [Stack Overflow](https://stackoverflow.com/)

* [Multi module POM - creating a site that works](https://stackoverflow.com/q/10848715/330457)
* [How do SO_REUSEADDR and SO_REUSEPORT differ?](https://stackoverflow.com/q/14388706/330457)
* [JUL to SLF4J Bridge](https://stackoverflow.com/q/9117030/330457)
* [What is the minimum number of written bytes of a SocketChannel when its key is writable?](https://stackoverflow.com/q/77895312/330457)

### ETC

* [7 Popular Unit Test Naming Conventions](https://dzone.com/articles/7-popular-unit-test-naming) (DZone)
* [Difference Between SO_REUSEADDR and SO_REUSEPORT](https://www.geeksforgeeks.org/difference-between-so_reuseaddr-and-so_reuseport/) (GeeksforGeeks)
