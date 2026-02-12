# TARGETS

Analysis of interface methods organized by Java API package.

> **Interfaces**: `HelloWorld` (sync), `AsynchronousHelloWorld` (async), `ReactiveHelloWorld` (reactive)
>
> **Note**: The `→` in Notes column shows the call-chain (e.g., `→ set(byte[])` means the method delegates to `set(byte[])`). Methods are ordered by dependency within each package.

## java.lang

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `byte[]` | `set(byte[], int)` | Active | Abstract method |
| `HelloWorld` | `byte[]` | `set(byte[])` | Active | → `set(array, 0)` |
| `HelloWorld` | `byte[]` | `set()` | Active | → `set(new byte[BYTES])` |
| `HelloWorld` | `Appendable` | `append(T)` | Active | → `set(byte[])` |

## java.lang.foreign

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `MemorySegment` | `copy(T)` | Active | → `set(byte[])` |

## java.io

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `OutputStream` | `write(T)` | Active | → `set(byte[])` |
| `HelloWorld` | `DataOutput` | `write(T)` | Active | → `set(byte[])` |
| `HelloWorld` | `RandomAccessFile` | `write(T)` | Active | → `write(DataOutput)` |
| `HelloWorld` | `Writer` | `write(T)` | Active | → `append(Appendable)` |
| `HelloWorld` | `File` | `append(T)` | Active | → `write(OutputStream)` |

## java.net

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `DatagramPacket` | `set(DatagramPacket)` | Active | → `set(byte[])` |
| `HelloWorld` | `DatagramPacket` | `append(DatagramPacket)` | Active | → `put(ByteBuffer)` |
| `HelloWorld` | `DatagramSocket` | `send(T)` | Active | → `set(DatagramPacket)` |
| `HelloWorld` | `DatagramSocket` | `send(T, SocketAddress)` | Active | → `set(DatagramPacket)` |
| `HelloWorld` | `MulticastSocket` | `send(T)` | Deprecated | → `send(DatagramSocket)` |
| `HelloWorld` | `MulticastSocket` | `send(T, SocketAddress)` | Deprecated | → `send(DatagramSocket, SocketAddress)` |
| `HelloWorld` | `Socket` | `send(T)` | Active | → `write(OutputStream)` |

## java.net.http

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `HttpRequest.Builder` | `method(T, String)` | Active | → `set(byte[])` |
| `AsynchronousHelloWorld` | `WebSocket` | `send(T, boolean)` | Active | `sendBinary(ByteBuffer, boolean)` |
| `AsynchronousHelloWorld` | `WebSocket` | `ping(T)` | Active | `sendPing(ByteBuffer)` |
| `AsynchronousHelloWorld` | `WebSocket` | `pong(T)` | Active | `sendPong(ByteBuffer)` |

## java.nio

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `ByteBuffer` | `put(T)` | Active | → `set(byte[])` |
| `HelloWorld` | `ByteBuffer` | `put()` | Active | → `put(ByteBuffer.allocate(BYTES))` |

## java.nio.channels

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `WritableByteChannel` | `write(T)` | Active | → `put(ByteBuffer)` |
| `HelloWorld` | `GatheringByteChannel` | `write(T)` | Deprecated | → `put(ByteBuffer)` |
| `HelloWorld` | `SeekableByteChannel` | `write(T)` | Deprecated | → `write(WritableByteChannel)` |
| `HelloWorld` | `DatagramChannel` | `write(T)` | Active | → `write(WritableByteChannel)` |
| `HelloWorld` | `DatagramChannel` | `send(T, SocketAddress)` | Active | → `put(ByteBuffer)` |
| `HelloWorld` | `SocketChannel` | `send(T)` | Deprecated | → `write(WritableByteChannel)` |
| `HelloWorld` | `AsynchronousByteChannel` | `write(T)` | Active | → `put(ByteBuffer)` |
| `AsynchronousHelloWorld` | `AsynchronousByteChannel` | `write(T, A, CompletionHandler)` | Active | → `put(ByteBuffer)` |
| `HelloWorld` | `AsynchronousFileChannel` | `write(T, long)` | Active | → `put(ByteBuffer)` |
| `AsynchronousHelloWorld` | `AsynchronousFileChannel` | `write(T, long, A, CompletionHandler)` | Active | → `put(ByteBuffer)` |
| `AsynchronousHelloWorld` | `AsynchronousSocketChannel` | `send(T, A, CompletionHandler)` | Deprecated | → `write(AsynchronousByteChannel, ...)` |

## java.nio.file

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `Path` | `append(T)` | Active | → `write(WritableByteChannel)` |
| `AsynchronousHelloWorld` | `Path` | `append(T, A, CompletionHandler)` | Active | → `write(AsynchronousFileChannel, ...)` |

## java.security / javax.crypto

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `MessageDigest` | `update(T)` | Active | → `set(byte[])` |
| `HelloWorld` | `Signature` | `update(T)` | Active | → `set(byte[])` |
| `HelloWorld` | `Mac` | `update(T)` | Active | → `set(byte[])` |
| `HelloWorld` | `Cipher` | `update(T, Consumer)` | Active | → `set(byte[])` |

## java.sql

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `Blob` | `set(T, long)` | Active | → `write(OutputStream)` |

## java.util

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `BitSet` | `set(T, int)` | Active | → `set(byte[])`, little-endian bit order |

## java.util.zip

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `Checksum` | `update(T)` | Active | → `set(byte[])` |
| `HelloWorld` | `Deflater` | `input(T)` | Active | → `set(byte[])` |

---

## Summary

| Metric | Count |
|--------|-------|
| **Total methods** | 45 |
| **Active methods** | 38 |
| **Deprecated methods** | 7 |
| **Abstract methods** | 1 |
| **Async (Future-based)** | 2 |
| **Async (CompletionHandler)** | 3 |
| **Factory methods** | 2 |

### Deprecated Methods

| Method | Use Instead |
|--------|-------------|
| `write(GatheringByteChannel)` | `write(WritableByteChannel)` |
| `write(SeekableByteChannel)` | `write(WritableByteChannel)` |
| `send(SocketChannel)` | `write(WritableByteChannel)` |
| `send(AsynchronousSocketChannel)` | `write(AsynchronousByteChannel)` |
| `send(AsynchronousSocketChannel, A, CompletionHandler)` | `write(AsynchronousByteChannel, A, CompletionHandler)` |
| `send(MulticastSocket)` | `send(DatagramSocket)` |
| `send(MulticastSocket, SocketAddress)` | `send(DatagramSocket, SocketAddress)` |

---

## Method Patterns

| Pattern | Description | Examples |
|---------|-------------|----------|
| Sync blocking | Most methods | `write(OutputStream)`, `send(Socket)` |
| Async Future | Returns channel, use `.get()` | `write(AsynchronousByteChannel)` |
| Async Callback | CompletionHandler notification | `write(T, A, CompletionHandler)` |
| Factory | Creates and returns new object | `set()`, `put()` |

---

## Potential Additions

### To Add

| Interface | Package | Target | Notes |
|-----------|---------|--------|-------|
| `ReactiveHelloWorld` | `java.util.concurrent.Flow` | `Publisher<ByteBuffer>` | Reactive streams |
| `HelloWorld` | `javax.imageio.stream` | `ImageOutputStream` | `write(byte[])` |
| `HelloWorld` | `javax.sound.sampled` | `SourceDataLine` | Audio output |

### Redundant (Covered by Existing Methods)

| Target | Covered By | Reason |
|--------|------------|--------|
| `BufferedOutputStream` | `OutputStream` | Subclass |
| `BufferedWriter` | `Writer` | Subclass |
| `CharBuffer` | `Appendable` | Implements Appendable |
| `CipherOutputStream` | `OutputStream` | Subclass |
| `DataOutputStream` | `DataOutput` | Implements DataOutput |
| `GZIPOutputStream` | `OutputStream` | Subclass |
| `JarOutputStream` | `OutputStream` | Subclass |
| `MappedByteBuffer` | `ByteBuffer` | Subclass |
| `ObjectOutputStream` | `OutputStream` | Subclass |
| `Pipe.SinkChannel` | `WritableByteChannel` | Implements interface |
| `PrintStream` | `OutputStream` | Subclass |
| `PrintWriter` | `Writer` | Subclass |
| `SSLSocket` | `Socket` | Subclass |
| `ZipOutputStream` | `OutputStream` | Subclass |
