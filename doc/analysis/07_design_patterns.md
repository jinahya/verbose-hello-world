# 07. Design Patterns

## Overview

The verbose-hello-world project demonstrates numerous design patterns, making it an excellent educational resource for understanding pattern applications in Java.

## Creational Patterns

### 1. Factory Method Pattern

**Location**: `HelloWorldServiceProvider`

The SPI interface acts as a factory method:

```java
public interface HelloWorldServiceProvider {
    HelloWorld getInstance();  // Factory method
}
```

**Usage**:
```java
HelloWorld instance = provider.getInstance();
```

### 2. Abstract Factory Pattern

**Location**: CDI Providers (App4)

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

### 3. Builder Pattern

**Location**: Various utility methods using fluent interfaces

```java
// ByteBuffer building
ByteBuffer buffer = ByteBuffer.allocate(12);
helloWorld.put(buffer).flip();
```

### 4. Singleton Pattern

**Location**: CDI `@ApplicationScoped` beans

```java
@ApplicationScoped  // Single instance per application
public class HelloWorldProvider {
    // ...
}
```

## Structural Patterns

### 5. Adapter Pattern

**Location**: `DefaultAsynchronousHelloWorld`

Adapts synchronous `HelloWorld` to async interface:

```java
public class DefaultAsynchronousHelloWorld implements AsynchronousHelloWorld {
    private final HelloWorld delegate;  // Adaptee

    @Override
    public CompletableFuture<byte[]> setAsync(byte[] array) {
        // Adapts sync to async
        return CompletableFuture.supplyAsync(() -> delegate.set(array));
    }
}
```

### 6. Decorator Pattern

**Location**: `HelloWorldWrap` extends `HelloWorldImpl`

```java
public class HelloWorldWrap extends HelloWorldImpl {
    @Override
    public byte[] set(final byte[] array) {
        // Decorates with different implementation
        ByteBuffer.wrap(array).put(HelloWorld.BYTES);
        return array;
    }
}
```

### 7. Facade Pattern

**Location**: Utility classes like `HelloWorldUtils`

Provides simplified interface to complex subsystems:

```java
public final class HelloWorldUtils {
    public static void writeToStream(HelloWorld hw, OutputStream os) {
        // Hides complexity of byte handling
    }
}
```

### 8. Proxy Pattern

**Location**: Implicit in DI frameworks

Guice and CDI create proxies for injected dependencies.

## Behavioral Patterns

### 9. Strategy Pattern

**Location**: Multiple `HelloWorld` implementations

```java
// Different strategies for same operation
HelloWorld strategy1 = new HelloWorldImpl();   // Direct indexing
HelloWorld strategy2 = new HelloWorldDemo();   // arraycopy
HelloWorld strategy3 = new HelloWorldWrap();   // ByteBuffer

// Same interface, different algorithms
strategy1.set(array);
strategy2.set(array);
strategy3.set(array);
```

### 10. Template Method Pattern

**Location**: Default methods in `HelloWorld` interface

```java
public interface HelloWorld {
    // Abstract method - subclasses implement
    byte[] set(byte[] array);

    // Template methods use the abstract method
    default <T extends OutputStream> T write(T stream) throws IOException {
        stream.write(set(new byte[BYTES.length]));  // Uses set()
        return stream;
    }

    default <T extends ByteBuffer> T put(T buffer) {
        buffer.put(set(new byte[BYTES.length]));  // Uses set()
        return buffer;
    }
}
```

### 11. Observer Pattern

**Location**: `HelloWorldFlow` with Java Flow API

```java
public class HelloWorldFlow {
    public static Flow.Publisher<ByteBuffer> createPublisher(HelloWorld hw) {
        return subscriber -> {
            // Observer pattern: subscriber observes publisher
            subscriber.onNext(hw.put(ByteBuffer.allocate(12)));
            subscriber.onComplete();
        };
    }
}
```

### 12. Command Pattern

**Location**: Functional interface as command

```java
// HelloWorld as a command object
HelloWorld command = array -> {
    // Command logic
    return array;
};

// Execute command
command.set(new byte[12]);
```

### 13. Chain of Responsibility

**Location**: ServiceLoader iteration

```java
ServiceLoader.load(HelloWorldServiceProvider.class)
    .stream()
    .map(ServiceLoader.Provider::get)
    .map(HelloWorldServiceProvider::getInstance)
    .findFirst()  // Chain finds first handler
    .orElseThrow();
```

## Architectural Patterns

### 14. Service Provider Interface (SPI)

**Location**: `HelloWorldServiceProvider`

```
META-INF/services/com.github.jinahya.hello.api.spi.HelloWorldServiceProvider
└── com.github.jinahya.hello.lib.HelloWorldImpl
```

```java
ServiceLoader<HelloWorldServiceProvider> loader =
    ServiceLoader.load(HelloWorldServiceProvider.class);
```

### 15. Dependency Injection

**Location**: App3 (Guice), App4 (CDI)

```java
// Field injection
@Inject
private HelloWorld helloWorld;

// Constructor injection
@Inject
public MyClass(HelloWorld helloWorld) {
    this.helloWorld = helloWorld;
}
```

### 16. Inversion of Control (IoC)

**Location**: All DI implementations

The framework controls object creation and lifecycle:

```java
// Framework creates and injects
Injector injector = Guice.createInjector(new HelloWorldModule());
HelloWorldMain main = injector.getInstance(HelloWorldMain.class);
```

## Functional Patterns

### 17. Functional Interface

**Location**: `HelloWorld`

```java
@FunctionalInterface
public interface HelloWorld {
    byte[] set(byte[] array);
}

// Lambda implementation
HelloWorld lambda = array -> {
    System.arraycopy(BYTES, 0, array, 0, BYTES.length);
    return array;
};
```

### 18. Method Reference

**Location**: Throughout the codebase

```java
// Method reference usage
Optional.of(provider)
    .map(HelloWorldServiceProvider::getInstance)
    .ifPresent(HelloWorld::get);
```

### 19. Fluent Interface

**Location**: `HelloWorld` methods

```java
// Method chaining
helloWorld
    .put(ByteBuffer.allocate(12))
    .flip()
    .array();
```

## Pattern Summary Table

| Pattern | Category | Location | Purpose |
|---------|----------|----------|---------|
| Factory Method | Creational | `HelloWorldServiceProvider` | Object creation |
| Abstract Factory | Creational | CDI Providers | Family of objects |
| Singleton | Creational | `@ApplicationScoped` | Single instance |
| Adapter | Structural | `DefaultAsynchronousHelloWorld` | Interface adaptation |
| Decorator | Structural | `HelloWorldWrap` | Extended behavior |
| Facade | Structural | Utility classes | Simplified interface |
| Strategy | Behavioral | Multiple implementations | Algorithm selection |
| Template Method | Behavioral | Default interface methods | Algorithm skeleton |
| Observer | Behavioral | `HelloWorldFlow` | Event notification |
| SPI | Architectural | ServiceLoader | Service discovery |
| DI | Architectural | Guice/CDI | Dependency management |
| Functional Interface | Functional | `HelloWorld` | Lambda support |

---
[Back to Index](00_index.md) | [Previous: Dependencies](06_dependencies.md) | [Next: Testing Strategy](08_testing.md)
