# 03. API Module Analysis

## Module Information

| Attribute | Value |
|-----------|-------|
| **Artifact ID** | 01-verbose-hello-world-api |
| **Packaging** | jar |
| **Lines of Code** | ~3,000+ |
| **Main Classes** | 39 |
| **Test Classes** | 20+ |

## Core Interface: HelloWorld

The `HelloWorld` interface is the heart of this project. It's a **@FunctionalInterface** that defines how to set 12 bytes representing "hello, world" into various targets.

### Interface Definition

```java
@FunctionalInterface
public interface HelloWorld {
    // The core message
    byte[] BYTES = {
        0x68, 0x65, 0x6C, 0x6C, 0x6F, 0x2C,  // "hello,"
        0x20, 0x77, 0x6F, 0x72, 0x6C, 0x64   // " world"
    };

    // Abstract method (SAM)
    byte[] set(byte[] array);
}
```

### Method Categories

The interface provides 40+ overloaded methods organized into categories:

#### 1. Byte Array Operations
```java
byte[] set(byte[] array);                    // Core method
byte[] set(byte[] array, int index);         // With offset
default byte[] get();                        // Create and return array
```

#### 2. Stream Operations (java.io)
```java
<T extends OutputStream> T write(T stream);
<T extends DataOutput> T write(T output);
default byte[] write(Socket socket);
```

#### 3. Channel Operations (java.nio.channels)
```java
<T extends WritableByteChannel> T write(T channel);
<T extends AsynchronousChannel> CompletableFuture<T> writeAsync(T channel);
<T extends FileChannel> T write(T channel, long position);
```

#### 4. ByteBuffer Operations
```java
<T extends ByteBuffer> T put(T buffer);
default ByteBuffer put();                    // Allocate and put
```

#### 5. File Operations
```java
<T extends Path> T write(T path);
<T extends Path> T write(T path, OpenOption... options);
```

#### 6. Network Operations
```java
byte[] send(DatagramSocket socket, SocketAddress address);
byte[] send(DatagramChannel channel, SocketAddress address);
<T extends Socket> T write(T socket);
```

#### 7. Async Operations
```java
CompletableFuture<ByteBuffer> writeAsync(AsynchronousByteChannel channel);
CompletableFuture<Integer> sendAsync(DatagramChannel channel, SocketAddress address);
```

## SPI Interface

### HelloWorldServiceProvider

```java
public interface HelloWorldServiceProvider {
    HelloWorld getInstance();
}
```

Used with Java's `ServiceLoader` for service discovery:

```java
ServiceLoader<HelloWorldServiceProvider> loader =
    ServiceLoader.load(HelloWorldServiceProvider.class);
HelloWorld instance = loader.findFirst()
    .orElseThrow()
    .getInstance();
```

## Async Support

### AsynchronousHelloWorld Interface

```java
public interface AsynchronousHelloWorld {
    CompletableFuture<byte[]> setAsync(byte[] array);
    CompletableFuture<ByteBuffer> putAsync(ByteBuffer buffer);
    CompletableFuture<? extends WritableByteChannel> writeAsync(WritableByteChannel channel);
}
```

### DefaultAsynchronousHelloWorld

Default implementation wrapping a `HelloWorld` instance:

```java
public class DefaultAsynchronousHelloWorld implements AsynchronousHelloWorld {
    private final HelloWorld delegate;
    private final Executor executor;

    @Override
    public CompletableFuture<byte[]> setAsync(byte[] array) {
        return CompletableFuture.supplyAsync(() -> delegate.set(array), executor);
    }
}
```

## Utility Classes

### Package: com.github.jinahya.hello.api.util

| Class | Purpose |
|-------|---------|
| `HelloWorldLoggers` | SLF4J logger management |
| `HelloWorldValidator` | Bean Validation utilities |
| `HelloWorldUtils` | General utilities |

### Java Lang Utilities

| Class | Purpose |
|-------|---------|
| `JavaLangUtils` | System and runtime utilities |
| `JavaLangArrayUtils` | Array manipulation |
| `JavaLangReflectUtils` | Reflection utilities |

### Java NIO Utilities

| Class | Purpose |
|-------|---------|
| `JavaNioByteBufferUtils` | ByteBuffer operations |
| `JavaNioChannelsUtils` | Channel utilities |
| `JavaNioChannelsCompletionHandlerUtils` | Async completion handlers |
| `JavaNioChannelsNetworkChannelUtils` | Network channel utilities |

### Java I/O Utilities

| Class | Purpose |
|-------|---------|
| `JavaIoCloseableUtils` | Closeable resource handling |
| `JavaIoFlushableUtils` | Flushable resource handling |

### Java Net Utilities

| Class | Purpose |
|-------|---------|
| `JavaNetSocketOptionUtils` | Socket option configuration |
| `SocketOptionsPrinter` | Socket options display |

### Concurrent Utilities

| Class | Purpose |
|-------|---------|
| `JavaUtilConcurrentCallableUtils` | Callable utilities |
| `JavaUtilConcurrentExecutorUtils` | Executor management |

### Other Utilities

| Class | Purpose |
|-------|---------|
| `Stopwatch` | Performance timing |
| `StopwatchProvider` | Stopwatch factory |
| `LoggingUtils` | Logging helpers |
| `LogbackUtils` | Logback configuration |

## Reactive Support (Test Scope)

The API module tests against multiple reactive libraries:

| Library | Purpose |
|---------|---------|
| Project Reactor | Reactive Streams implementation |
| RxJava 3 | ReactiveX for Java |
| SmallRye Mutiny | Reactive library for Quarkus |
| Eclipse Vert.x | Reactive toolkit |

### HelloWorldFlow

Integration with Java's Flow API (Reactive Streams):

```java
public class HelloWorldFlow {
    public static Flow.Publisher<ByteBuffer> createPublisher(HelloWorld helloWorld);
    public static Flow.Subscriber<ByteBuffer> createSubscriber(Consumer<byte[]> consumer);
}
```

## Module Exports

```java
module com.github.jinahya.hello.api {
    exports com.github.jinahya.hello;
    exports com.github.jinahya.hello.api;
    exports com.github.jinahya.hello.api.spi;

    requires org.slf4j;
    requires static org.jspecify;
}
```

## Key Design Decisions

### 1. Functional Interface
- Enables lambda expressions
- Single abstract method: `set(byte[] array)`
- All other methods have default implementations

### 2. Immutable Constants
- `BYTES` array represents "hello, world"
- Documented byte values in multiple formats (hex, binary, octal)

### 3. Fluent API
- Methods return the target for chaining
- Example: `helloWorld.write(stream).flush()`

### 4. Overloaded Methods
- Same operation for different target types
- Consistent naming across different I/O mechanisms

### 5. Null Safety
- JSpecify annotations for compile-time null checking
- `@NullMarked` package-level annotation

---
[Back to Index](00_index.md) | [Previous: Architecture](02_architecture.md) | [Next: Library Module](04_lib_module.md)
