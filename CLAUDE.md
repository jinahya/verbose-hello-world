# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A verbose "hello, world" project whose sole purpose is to explore and learn about (almost) all Java I/O related APIs, using the simplest possible payload: the 12-byte string `"hello, world"`. The API module's `HelloWorld` interface provides default method implementations covering every major I/O pathway in the JDK (`java.io`, `java.nio`, `java.net`, async channels, reactive streams, etc.). The lib and app modules additionally demonstrate DI/CDI integration patterns (Dagger, Guice, HK2, Spring, OpenWebBeans, Weld).

## Build Commands

```bash
# Full build (all modules)
./mvnw clean install

# Build without tests
./mvnw clean install -DskipTests

# Run tests for a specific module
./mvnw test -pl 01-verbose-hello-world-api
./mvnw test -pl 02-verbose-hello-world-lib

# Run tests with a specific DI/CDI profile (lib module only)
./mvnw test -Pcdi-se-openwebbeans -pl 02-verbose-hello-world-lib
./mvnw test -Pcdi-se-openwebbeans-junit5 -pl 02-verbose-hello-world-lib
./mvnw test -Pcdi-se-weld -pl 02-verbose-hello-world-lib
./mvnw test -Pcdi-se-weld-junit5 -pl 02-verbose-hello-world-lib
./mvnw test -Pdi-dagger -pl 02-verbose-hello-world-lib
./mvnw test -Pdi-guice -pl 02-verbose-hello-world-lib
./mvnw test -Phk2 -pl 02-verbose-hello-world-lib
./mvnw test -Pspring -pl 02-verbose-hello-world-lib

# Run a single test class
./mvnw test -pl 01-verbose-hello-world-api -Dtest=HelloWorld_01_Set_ArrayWithIndex_Test

# SonarCloud analysis
./mvnw install org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=jinahya_verbose-hello-world
```

## Build Requirements

- **Java 25** with preview features enabled (`--enable-preview` configured in compiler, surefire, and `.mvn/jvm.config`)
- **Maven 3.6.3+** (wrapper included: `./mvnw`)
- `.mvn/jvm.config` exports internal compiler modules for Error Prone annotation processing
- `.mvn/maven.config` enables ByteBuddy experimental mode (`-Dnet.bytebuddy.experimental=true`) required by Mockito

## Module Architecture

```
verbose-hello-world (root POM aggregator)
├── 01-verbose-hello-world-api    # HelloWorld interface + utilities
├── 02-verbose-hello-world-lib    # Implementations (HelloWorldImpl, HelloWorldDemo, HelloWorldWrap)
└── 03-verbose-hello-world-app    # Application modules (POM aggregator)
    ├── 01-...-app1               # Direct instantiation of HelloWorldImpl
    ├── 02-...-app2               # SPI via ServiceLoader
    ├── 03-...-app3               # DI (Guice)
    └── 04-...-app4               # CDI (Jakarta CDI)
```

### API Module (`01-verbose-hello-world-api`)

The core `HelloWorld` interface is a `@FunctionalInterface` whose single abstract method is `set(byte[], int)` — writing the 12-byte `"hello, world"` string. All other methods are `default` implementations covering diverse I/O patterns:

- `java.lang` — byte arrays, `Appendable`
- `java.io` — streams, files, writers
- `java.net` — sockets
- `java.nio` — `ByteBuffer`, channels (sync + async), `CompletionHandler`
- Reactive Streams — via `HelloWorldFlow` and `ReactiveHelloWorldFactory`

Also contains utility classes under subpackages for reflection, NIO, concurrency, security, and logging.

### Lib Module (`02-verbose-hello-world-lib`)

Provides concrete implementations. Test sources are split into **separate directories per DI framework**, activated by Maven profiles. Each profile uses `build-helper-maven-plugin` to add its test source tree:

- `src/test/java-cdi-se-openwebbeans/`
- `src/test/java-cdi-se-weld/`
- `src/test/java-di-dagger/`
- `src/test/java-di-guice/`
- `src/test/java-di-hk2/`
- `src/test/java-di-spring/`
- (plus `-junit5` variants for CDI)

### App Module (`03-verbose-hello-world-app`)

The `generate-executables` profile (active by default) produces multiple artifact formats: fat JAR (assembly), shaded JAR, Spring Boot JAR, and compressed archives.

## Testing

- **JUnit 5** (Jupiter) with **Mockito** for mocking and **Awaitility** for async assertions
- Coverage via **JaCoCo**
- The default `./mvnw test` runs common tests; DI/CDI-specific tests require activating the corresponding Maven profile (see Build Commands above)
- CI runs all 8 profiles sequentially against the lib module

## Key Conventions

- Java Module System (`module-info.java`) is used across modules
- Annotation processors: Lombok, Error Prone, NullAway
- Null safety: JSpecify annotations (`@NullMarked`, `@Nullable`)
- Custom annotations `@屋上架屋` and `@屋下架屋` are used as code documentation markers
- Logging: SLF4J API with Logback runtime, plus `jul-to-slf4j` bridge
