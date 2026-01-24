# 06. Dependencies Analysis

## Dependency Overview

The project uses a carefully managed set of dependencies, categorized by scope and purpose.

## Core Dependencies (Compile Scope)

### Logging

| Dependency | Version | Purpose |
|------------|---------|---------|
| org.slf4j:slf4j-api | 2.x | Logging facade |
| ch.qos.logback:logback-classic | 1.5.24 | Logging implementation (runtime) |

### Null Safety

| Dependency | Version | Purpose |
|------------|---------|---------|
| org.jspecify:jspecify | 1.0.0 | Null-safety annotations |

### Utilities

| Dependency | Version | Purpose |
|------------|---------|---------|
| com.google.guava:guava | 33.5.0-jre | Google Core Libraries |
| org.apache.commons:commons-lang3 | 3.20.0 | Commons Lang utilities |

## Provided Dependencies

These are available at compile time but expected to be provided by the runtime environment.

| Dependency | Version | Purpose |
|------------|---------|---------|
| jakarta.inject:jakarta.inject-api | 2.0.1 | Standard DI annotations |
| jakarta.validation:jakarta.validation-api | 3.1.1 | Bean Validation API |
| jakarta.annotation:jakarta.annotation-api | 3.0.0 | Common annotations |
| org.projectlombok:lombok | 1.18.38 | Boilerplate reduction |

## Test Dependencies

### Testing Frameworks

| Dependency | Version | Purpose |
|------------|---------|---------|
| org.junit.jupiter:junit-jupiter | 5.12.x | JUnit 5 testing |
| org.mockito:mockito-core | 5.21.0 | Mocking framework |
| org.mockito:mockito-junit-jupiter | 5.21.0 | Mockito JUnit 5 integration |
| org.awaitility:awaitility | 4.3.0 | Async testing utilities |

### CDI Implementations (Test Scope)

| Dependency | Version | Purpose |
|------------|---------|---------|
| org.apache.openwebbeans:openwebbeans-se | 4.0.3 | Apache OpenWebBeans CDI |
| org.jboss.weld.se:weld-se-core | 6.0.3.Final | JBoss Weld CDI |
| org.jboss.weld:weld-junit5 | 5.0.3.Final | Weld JUnit 5 integration |

### Dependency Injection Frameworks (Test Scope)

| Dependency | Version | Purpose |
|------------|---------|---------|
| com.google.dagger:dagger | 2.57.2 | Compile-time DI |
| com.google.dagger:dagger-compiler | 2.57.2 | Dagger annotation processor |
| com.google.inject:guice | 7.0.0 | Google Guice runtime DI |
| org.glassfish.hk2:hk2 | 4.0.0-M3 | GlassFish HK2 DI |
| org.springframework:spring-context | 7.0.2 | Spring Framework DI |

### Reactive Libraries (Test Scope)

| Dependency | Version | Purpose |
|------------|---------|---------|
| io.projectreactor:reactor-core | 3.7.5 | Project Reactor |
| io.reactivex.rxjava3:rxjava | 3.1.10 | RxJava 3 |
| io.smallrye.reactive:mutiny | 2.8.0 | SmallRye Mutiny |
| io.vertx:vertx-core | 5.0.1 | Eclipse Vert.x |

### Validation

| Dependency | Version | Purpose |
|------------|---------|---------|
| org.hibernate.validator:hibernate-validator | 8.0.1.Final | Bean Validation implementation |
| org.glassfish.expressly:expressly | 6.0.0 | Expression Language |

### Other Test Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| org.apache.commons:commons-text | 1.13.1 | Text utilities |
| org.aspectj:aspectjrt | 1.10.0 | AspectJ runtime |
| org.bouncycastle:bcprov-jdk18on | 2.73.10 | Crypto provider |

## Dependency Management

### Parent POM

The project inherits from:

```xml
<parent>
    <groupId>com.github.jinahya</groupId>
    <artifactId>jinahya-parent</artifactId>
    <version>0.9.2</version>
</parent>
```

This parent POM provides:
- Version management for common dependencies
- Plugin configurations
- Common build settings

### Property-Based Version Management

```xml
<properties>
    <version.awaitility>4.3.0</version.awaitility>
    <version.com.google.dagger>2.57.2</version.com.google.dagger>
    <version.com.google.inject.guice>7.0.0</version.com.google.inject.guice>
    <version.dagger-compiler>${version.com.google.dagger}</version.dagger-compiler>
    <version.io.projectreactor.reactor-core>3.7.5</version.io.projectreactor.reactor-core>
    <version.io.reactivex.rxjava3.rxjava>3.1.10</version.io.reactivex.rxjava3.rxjava>
    <version.logback-classic>1.5.24</version.logback-classic>
    <version.mockito>5.21.0</version.mockito>
    <version.org.apache.openwebbeans>4.0.3</version.org.apache.openwebbeans>
    <version.org.glassfish.hk2.hk2>4.0.0-M3</version.org.glassfish.hk2.hk2>
    <version.org.jboss.weld.weld-junit5>5.0.3.Final</version.org.jboss.weld.weld-junit5>
    <version.org.jboss.weld.se.weld-se-core>6.0.3.Final</version.org.jboss.weld.se.weld-se-core>
    <version.org.springframework>7.0.2</version.org.springframework>
    <!-- ... more properties ... -->
</properties>
```

## Dependency Graph

```
verbose-hello-world
├── API Module
│   ├── org.slf4j:slf4j-api (compile)
│   ├── org.jspecify:jspecify (compile)
│   ├── com.google.guava:guava (compile)
│   ├── jakarta.validation:jakarta.validation-api (provided)
│   └── [test dependencies]
│
├── LIB Module
│   ├── API Module (compile)
│   ├── jakarta.inject:jakarta.inject-api (provided)
│   └── [extensive test dependencies for DI frameworks]
│
└── APP Modules
    ├── App1: LIB Module (compile)
    ├── App2: API Module (compile) + LIB Module (runtime)
    ├── App3: + com.google.inject:guice
    └── App4: + jakarta.enterprise:cdi-api + weld-se-core
```

## Notable Dependency Choices

### 1. SLF4J + Logback
Standard logging combination for flexibility and performance.

### 2. JSpecify
Modern null-safety annotations (successor to JSR-305, FindBugs annotations).

### 3. Jakarta over Javax
Uses Jakarta EE namespace (jakarta.*) for forward compatibility.

### 4. Multiple DI Frameworks
Educational approach - demonstrates same patterns across frameworks.

### 5. Reactive Libraries
Comprehensive reactive testing with Reactor, RxJava, Mutiny, and Vert.x.

## Security Considerations

### Vulnerable Dependencies
Run periodic checks with:
```bash
./mvnw org.owasp:dependency-check-maven:check
```

### Dependency Updates
Check for updates:
```bash
./mvnw versions:display-dependency-updates
./mvnw versions:display-plugin-updates
```

---
[Back to Index](00_index.md) | [Previous: Application Modules](05_app_modules.md) | [Next: Design Patterns](07_design_patterns.md)
