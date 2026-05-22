# 05. Application Modules Analysis

## Overview

The application layer consists of four variants, each demonstrating a different approach to instantiating and using the `HelloWorld` implementation.

| App | Approach | Framework | Complexity |
|-----|----------|-----------|------------|
| App1 | Direct | None | Simple |
| App2 | SPI | ServiceLoader | Medium |
| App3 | DI | Google Guice | Medium |
| App4 | CDI | Weld SE | Complex |

## Common Configuration

All application modules share:

```xml

<parent>
  <artifactId>03-verbose-hello-world-app</artifactId>
</parent>

<properties>
<mainClass>com.github.jinahya.hello.app2.HelloWorldMain</mainClass>
</properties>
```

### Executable Generation

Each app generates multiple executable formats:

1. **Standard JAR** - Requires classpath setup
2. **Shade JAR** - Fat JAR with all dependencies
3. **Assembly JAR** - jar-with-dependencies
4. **Spring Boot JAR** - Repackaged for Spring Boot
5. **Archives** - TAR/GZ/BZ2

---

## App1: Direct Instantiation

### Purpose
Demonstrates the simplest approach - direct instantiation without any framework.

### Main Class

```java
public class HelloWorldMain {
    public static void main(String[] args) throws IOException {
        HelloWorld helloWorld = new HelloWorldImpl();
        byte[] bytes = new byte[HelloWorld.BYTES.length];
        helloWorld.set(bytes);
        System.out.write(bytes);
        System.out.flush();
    }
}
```

### Dependencies

```xml
<dependency>
    <groupId>com.github.jinahya</groupId>
    <artifactId>02-verbose-hello-world-lib</artifactId>
    <scope>compile</scope>
</dependency>
```

### Characteristics

- **Simplest implementation**
- **Tightly coupled** to implementation class
- **No configuration** needed
- **Fastest startup**

---

## App2: Service Provider Interface (SPI)

### Purpose
Demonstrates Java's built-in service discovery mechanism.

### Main Class

```java
public class HelloWorldMain {
    public static void main(String[] args) throws IOException {
        ServiceLoader<HelloWorldServiceProvider> loader =
            ServiceLoader.load(HelloWorldServiceProvider.class);

        HelloWorld helloWorld = loader.findFirst()
            .orElseThrow(() -> new IllegalStateException("No provider found"))
            .getInstance();

        byte[] bytes = new byte[HelloWorld.BYTES.length];
        helloWorld.set(bytes);
        System.out.write(bytes);
        System.out.flush();
    }
}
```

### Dependencies

```xml
<dependency>
    <groupId>com.github.jinahya</groupId>
    <artifactId>01-verbose-hello-world-api</artifactId>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.github.jinahya</groupId>
    <artifactId>02-verbose-hello-world-lib</artifactId>
    <scope>runtime</scope>  <!-- Runtime only! -->
</dependency>
```

### Service Configuration

```
# META-INF/services/com.github.jinahya.hello.api.spi.HelloWorldServiceProvider
com.github.jinahya.hello.lib.HelloWorldImpl
```

### Characteristics

- **Loose coupling** - API only at compile time
- **Pluggable implementations** - Swap at runtime
- **Standard Java mechanism**
- **No external dependencies**

---

## App3: Google Guice

### Purpose
Demonstrates dependency injection with Google Guice.

### Main Class

```java
public class HelloWorldMain {
    @Inject
    private HelloWorld helloWorld;

    public void run() throws IOException {
        byte[] bytes = new byte[HelloWorld.BYTES.length];
        helloWorld.set(bytes);
        System.out.write(bytes);
        System.out.flush();
    }

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new HelloWorldModule());
        HelloWorldMain main = injector.getInstance(HelloWorldMain.class);
        main.run();
    }
}
```

### Guice Module

```java
public class HelloWorldModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HelloWorld.class).to(HelloWorldImpl.class);
    }
}
```

### Dependencies

```xml
<dependency>
    <groupId>com.github.jinahya</groupId>
    <artifactId>01-verbose-hello-world-api</artifactId>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.github.jinahya</groupId>
    <artifactId>02-verbose-hello-world-lib</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>com.google.inject</groupId>
    <artifactId>guice</artifactId>
</dependency>
<dependency>
    <groupId>jakarta.inject</groupId>
    <artifactId>jakarta.inject-api</artifactId>
</dependency>
```

### Characteristics

- **Field injection** with `@Inject`
- **Binding configuration** in module
- **Runtime dependency resolution**
- **Type-safe configuration**

---

## App4: CDI (Weld SE)

### Purpose
Demonstrates Contexts and Dependency Injection (CDI) for Java SE.

### Main Class

```java
public class HelloWorldMain {
    @Inject
    @HelloWorldQualifier
    private HelloWorld helloWorld;

    public void run() throws IOException {
        byte[] bytes = new byte[HelloWorld.BYTES.length];
        helloWorld.set(bytes);
        System.out.write(bytes);
        System.out.flush();
    }

    public static void main(String[] args) throws IOException {
        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            HelloWorldMain main = container.select(HelloWorldMain.class).get();
            main.run();
        }
    }
}
```

### CDI Provider

```java
@ApplicationScoped
public class HelloWorldProvider {
    @Produces
    @HelloWorldQualifier
    public HelloWorld getHelloWorld() {
        return new HelloWorldImpl();
    }
}
```

### Custom Qualifier

```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface HelloWorldQualifier {
}
```

### beans.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
       bean-discovery-mode="all">
</beans>
```

### Dependencies

```xml
<dependency>
    <groupId>com.github.jinahya</groupId>
    <artifactId>01-verbose-hello-world-api</artifactId>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.github.jinahya</groupId>
    <artifactId>02-verbose-hello-world-lib</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>jakarta.enterprise</groupId>
    <artifactId>jakarta.enterprise.cdi-api</artifactId>
</dependency>
<dependency>
    <groupId>jakarta.inject</groupId>
    <artifactId>jakarta.inject-api</artifactId>
</dependency>
<dependency>
    <groupId>org.jboss.weld.se</groupId>
    <artifactId>weld-se-core</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Characteristics

- **CDI annotations** (`@Inject`, `@Produces`, `@Qualifier`)
- **Custom qualifiers** for bean selection
- **Weld SE** for standalone execution
- **Standard Jakarta EE pattern**

---

## Comparison Summary

| Feature | App1 | App2 | App3 | App4 |
|---------|------|------|------|------|
| **Coupling** | Tight | Loose | Loose | Loose |
| **Framework** | None | JDK | Guice | CDI |
| **Configuration** | None | Service file | Module class | beans.xml + annotations |
| **Startup Time** | Fastest | Fast | Medium | Slower |
| **Testability** | Low | Medium | High | High |
| **Flexibility** | Low | Medium | High | High |
| **Complexity** | Minimal | Low | Medium | Higher |

---

## Running the Applications

### App1 (Direct)
```bash
java -jar 03-verbose-hello-world-app/01-verbose-hello-world-app1/target/*-shade.jar
```

### App2 (SPI)
```bash
java -jar 03-verbose-hello-world-app/02-verbose-hello-world-app2/target/*-shade.jar
```

### App3 (Guice)
```bash
java -jar 03-verbose-hello-world-app/03-verbose-hello-world-app3/target/*-shade.jar
```

### App4 (CDI)
```bash
java -jar 03-verbose-hello-world-app/04-verbose-hello-world-app4/target/*-shade.jar
```

All should output:
```
hello, world
```

---
[Back to Index](00_index.md) | [Previous: Library Module](04_lib_module.md) | [Next: Dependencies](06_dependencies.md)
