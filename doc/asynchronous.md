# Asynchronous I/O Report

## Overview

The `AsynchronousHelloWorld` interface provides **asynchronous** I/O methods using Java's `CompletionStage` API. All operations are non-blocking and return immediately with a `CompletionStage` that completes when the operation finishes.

## Core Interface

**Interface:** `com.github.jinahya.hello.api.AsynchronousHelloWorld`

**Implementation:** `com.github.jinahya.hello.api.DefaultAsynchronousHelloWorld`

**Executor Strategy:** Uses virtual threads by default (`Executors.newVirtualThreadPerTaskExecutor()`)

## Factory Methods

### `static AsynchronousHelloWorld from(HelloWorld underlying, Executor executor)`
- Creates instance with custom executor
- Allows control over thread pool

### `static AsynchronousHelloWorld from(HelloWorld underlying)`
- Creates instance with virtual thread executor
- Recommended for I/O-intensive operations
- Optimal for high concurrency

## Method Categories

### 1. Core Byte Array Operations

#### `CompletionStage<byte[]> set(byte[] array, int index)`
- Async version of synchronous `set(array, index)`
- Executes in executor thread pool

#### `default CompletionStage<byte[]> set(byte[] array)`
- Async version of synchronous `set(array)`
- Delegates to `set(array, 0)`

### 2. Character/String Operations (java.lang)

#### `<T extends Appendable> CompletionStage<T> append(T appendable)`
- Async append to `Appendable`
- Wraps `IOException` in `UncheckedIOException`
- Exceptions propagate through `CompletionStage`

### 3. Stream Operations (java.io)

#### `<T extends OutputStream> CompletionStage<T> write(T stream)`
- Async write to `OutputStream`
- Does not flush stream
- Exceptions wrapped in `UncheckedIOException`

#### `default <T extends File> CompletionStage<T> append(T file)`
- Async append to file
- Creates `FileOutputStream` asynchronously
- Chains operations: create → write → flush → close
- Demonstrates `thenCompose()` for sequential async operations

#### `<T extends Writer> CompletionStage<T> write(T writer)`
- Async write to `Writer`
- Delegates to underlying `HelloWorld.write(writer)`

#### `<T extends DataOutput> CompletionStage<T> write(T output)`
- Async write to `DataOutput`
- Covers: `DataOutputStream`, etc.

#### `<T extends RandomAccessFile> CompletionStage<T> write(T file)`
- Async write to `RandomAccessFile`
- Position-based I/O

### 4. Network Operations (java.net)

#### `<T extends Socket> CompletionStage<T> send(T socket)`
- Async send through TCP socket
- Uses socket's output stream

### 5. NIO Operations (java.nio)

#### `<T extends ByteBuffer> CompletionStage<T> put(T buffer)`
- Async put into `ByteBuffer`
- No I/O exceptions (pure memory operation)

#### `<T extends WritableByteChannel> CompletionStage<T> write(T channel)`
- Async write to `WritableByteChannel`
- Covers: `FileChannel`, `SocketChannel`, etc.

#### `<T extends Path> CompletionStage<T> append(T path)`
- Async append to file via NIO `Path`
- Uses `FileChannel` operations

## Key Design Patterns

### 1. Exception Handling
- All `IOException`s wrapped in `UncheckedIOException`
- Exceptions propagate through `CompletionStage.exceptionally()`
- No checked exceptions in method signatures

### 2. Async Composition
```java
CompletableFuture.supplyAsync(() -> {
    // Create resource
    return new FileOutputStream(file, true);
})
.thenCompose(stream -> 
    write(stream).thenApply(writtenStream -> {
        // Process result
        writtenStream.flush();
        writtenStream.close();
        return file;
    })
)
```

### 3. Executor Usage
- All operations use provided executor
- Virtual threads optimal for I/O-bound operations
- No thread pool exhaustion with virtual threads

### 4. Resource Management
- Resources created asynchronously
- Cleanup in `thenApply()` callbacks
- Proper exception handling ensures cleanup

## Implementation Details

### DefaultAsynchronousHelloWorld

**Pattern:** Wraps synchronous `HelloWorld` calls in async execution

```java
return CompletableFuture.supplyAsync(
    () -> execute(() -> service.write(stream)),
    executor
);
```

**Helper Method:**
```java
private static <R> R execute(ThrowableSupplier<R, IOException> supplier) {
    try {
        return supplier.get();
    } catch (IOException ioe) {
        throw new UncheckedIOException(ioe);
    }
}
```

## Comparison with Synchronous API

| Aspect | Synchronous | Asynchronous |
|--------|------------|--------------|
| **Return Type** | Direct value | `CompletionStage<T>` |
| **Exceptions** | `throws IOException` | Wrapped in `UncheckedIOException` |
| **Blocking** | Blocks calling thread | Non-blocking |
| **Thread Model** | Calling thread | Executor threads |
| **Composition** | Sequential calls | `thenCompose()`, `thenApply()` |

## Teaching Points

### 1. CompletionStage API
- `supplyAsync()` - Create async operation
- `thenCompose()` - Chain async operations
- `thenApply()` - Transform results
- `exceptionally()` - Error handling

### 2. Virtual Threads
- Introduced in Java 21
- Perfect for I/O-bound operations
- No thread pool management needed
- High concurrency support

### 3. Exception Propagation
- Checked exceptions → Unchecked exceptions
- Error handling via `CompletionStage`
- No try-catch in async code

### 4. Resource Lifecycle
- Async resource creation
- Cleanup in callbacks
- Exception-safe cleanup

## Coverage Summary

| Category | Methods | Status |
|----------|---------|--------|
| **java.io** | 5 methods | ✅ Complete |
| **java.net** | 1 method | ✅ Complete |
| **java.nio** | 3 methods | ✅ Complete |
| **Async NIO** | 0 methods | ⚠️ Not covered (uses CompletionHandler pattern) |

## Notes

- **Async NIO methods** (with `CompletionHandler`) are not included in `AsynchronousHelloWorld`
- These remain in synchronous `HelloWorld` interface as they use callback pattern
- `CompletionStage` and `CompletionHandler` are different async paradigms
- Consider adding wrapper methods if needed for consistency

## Best Practices Demonstrated

1. ✅ Use virtual threads for I/O operations
2. ✅ Wrap checked exceptions in unchecked for async APIs
3. ✅ Chain operations with `thenCompose()` for sequential async work
4. ✅ Handle resource cleanup in callbacks
5. ✅ Use `CompletionStage` for composable async operations
