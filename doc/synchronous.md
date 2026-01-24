# Synchronous I/O Report

## Overview

The `HelloWorld` interface provides a comprehensive set of **synchronous** I/O methods for teaching Java I/O concepts using the simplest "hello, world" string. All methods are blocking and execute in the calling thread.

## Core Interface

**Interface:** `com.github.jinahya.hello.api.HelloWorld`

**Type:** `@FunctionalInterface` - enables lambda expressions

**Core Data:** 12 bytes representing "hello, world" in US_ASCII encoding

## Method Categories

### 1. Core Byte Array Operations

#### `byte[] set(byte[] array, int index)`
- Sets hello-world-bytes starting at specified index
- Abstract method (SAM) - must be implemented
- Validates array bounds

#### `default byte[] set(byte[] array)`
- Sets hello-world-bytes starting at index 0
- Delegates to `set(array, 0)`

### 2. Character/String Operations (java.lang)

#### `default <T extends Appendable> T append(T appendable) throws IOException`
- Appends hello-world-bytes as characters to any `Appendable`
- Covers: `StringBuilder`, `StringBuffer`, `Writer`, etc.
- Demonstrates byte-to-char conversion

### 3. Stream Operations (java.io)

#### `default <T extends OutputStream> T write(T stream) throws IOException`
- Writes bytes to any `OutputStream`
- Covers: `FileOutputStream`, `ByteArrayOutputStream`, `Socket.getOutputStream()`, etc.
- **Note:** Does not flush the stream

#### `default <T extends File> T append(T file) throws IOException`
- Appends to file using `FileOutputStream` in append mode
- Demonstrates try-with-resources pattern
- Flushes and closes stream automatically

#### `default <T extends DataOutput> T write(T output) throws IOException`
- Writes to `DataOutput` interface
- Covers: `DataOutputStream`, `RandomAccessFile`, etc.
- Demonstrates structured data output

#### `default <T extends RandomAccessFile> T write(T file) throws IOException`
- Writes at current file pointer position
- Demonstrates random access file operations
- Position-based I/O

#### `default <T extends Writer> T write(T writer) throws IOException`
- Writes to any `Writer`
- Delegates to `append(writer)`
- Covers: `FileWriter`, `StringWriter`, `PrintWriter`, etc.

### 4. Network Operations (java.net)

#### `default <T extends Socket> T send(T socket) throws IOException`
- Sends through TCP socket
- Uses `socket.getOutputStream()`
- Demonstrates network I/O

### 5. NIO Operations (java.nio)

#### `default <T extends ByteBuffer> T put(T buffer)`
- Puts bytes into `ByteBuffer`
- Handles both array-backed and direct buffers
- Demonstrates buffer position management
- Throws `BufferOverflowException` if insufficient capacity

#### `default <T extends WritableByteChannel> T write(T channel) throws IOException`
- Writes to any `WritableByteChannel`
- Demonstrates channel I/O with ByteBuffer
- Handles partial writes (while loop pattern)
- Covers: `FileChannel`, `SocketChannel`, `DatagramChannel`, etc.

#### `default <T extends SocketChannel> T send(T channel) throws IOException`
- **Deprecated** - use `write(channel)` instead
- Wrapper for `write(WritableByteChannel)`

#### `default <T extends Path> T append(T path) throws IOException`
- Appends to file via NIO `Path`
- Uses `FileChannel.open()` with `StandardOpenOption.CREATE` and `APPEND`
- Demonstrates NIO file operations
- Forces metadata to disk

### 6. Asynchronous NIO Operations (java.nio.channels)

#### `default <T extends AsynchronousByteChannel> T write(T channel) throws InterruptedException, ExecutionException`
- **Blocking** async write (waits for completion)
- Uses `Future.get()` to block
- Demonstrates async-to-sync conversion

#### `default <T extends AsynchronousByteChannel, A> void write(T channel, A attachment, CompletionHandler<? super T, ? super A> handler)`
- **Non-blocking** async write with callback
- Demonstrates `CompletionHandler` pattern
- Handles partial writes recursively

#### `default <T extends AsynchronousSocketChannel> T send(T channel) throws InterruptedException, ExecutionException`
- **Deprecated** - use `write(AsynchronousByteChannel)` instead
- Blocking async socket channel write

#### `default <T extends AsynchronousSocketChannel, A> void send(T channel, A attachment, CompletionHandler<? super T, ? super A> handler)`
- **Deprecated** - use `write(AsynchronousByteChannel, A, CompletionHandler)` instead
- Non-blocking async socket channel write

#### `default <T extends AsynchronousFileChannel> T write(T channel, long position) throws InterruptedException, ExecutionException`
- **Blocking** async file write at specific position
- Demonstrates positioned file I/O
- Handles partial writes with position tracking

#### `default <T extends AsynchronousFileChannel, A> void write(T channel, long position, A attachment, CompletionHandler<? super T, ? super A> handler)`
- **Non-blocking** async file write at position
- Demonstrates async file I/O with callbacks
- Recursive completion handler pattern

#### `default <T extends Path, A> void append(T path, A attachment, CompletionHandler<? super T, ? super A> handler) throws IOException`
- Async append to file via `Path`
- Opens `AsynchronousFileChannel`
- Writes at end of file (`channel.size()`)
- Forces and closes channel in completion handler

## Key Teaching Points

### 1. Exception Handling
- All I/O methods throw `IOException`
- Demonstrates checked exception handling
- Async methods throw `InterruptedException` and `ExecutionException`

### 2. Resource Management
- Try-with-resources pattern in `append(File)`
- Manual resource cleanup in async methods
- Channel closing in completion handlers

### 3. Buffer Management
- ByteBuffer position/limit/capacity concepts
- Direct vs. array-backed buffers
- Buffer flipping for read/write transitions

### 4. Partial Writes
- While-loop pattern for handling partial writes
- Position tracking in file operations
- Recursive completion handlers for async operations

### 5. Type Generics
- Extensive use of bounded type parameters (`<T extends ...>`)
- Enables method chaining (returns same type)
- Demonstrates generic programming in I/O

## Coverage Summary

| Category | Methods | Coverage |
|----------|---------|----------|
| **java.io** | 6 methods | ✅ Complete |
| **java.net** | 1 method | ✅ TCP covered |
| **java.nio** | 4 methods | ✅ Core NIO covered |
| **java.nio.channels (async)** | 7 methods | ✅ Async NIO covered |

## Missing for Comprehensive Teaching

1. **FileChannel (synchronous with position)** - Only async version exists
2. **DatagramSocket/DatagramChannel** - UDP networking not covered
3. **ServerSocket** - Server-side networking not covered
4. **Input streams/readers** - Read operations not covered (by design - write-only interface)

## Design Philosophy

The interface focuses on **output/write operations** only, making it simpler for teaching. The "hello, world" string provides a concrete, predictable data set (12 bytes) that students can easily verify and understand.

All methods are **thread-safe** and designed to be composable and chainable through their return types.
