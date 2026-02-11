# TARGETS

Analysis of `HelloWorld.java` interface methods organized by Java API package.

> **Note**: The `→` in Notes column shows the call-chain (e.g., `→ set(byte[])` means the method delegates to `set(byte[])`). Methods are ordered by dependency within each package.

## java.lang

| Target Class | Method | Status | Notes |
|--------------|--------|--------|-------|
| `byte[]` | `set(byte[], int)` | Active | Abstract method |
| `byte[]` | `set(byte[])` | Active | → `set(array, 0)` |
| `byte[]` | `set()` | Active | → `set(new byte[BYTES])` |
| `Appendable` | `append(T)` | Active | → `set(byte[])` |

## java.lang.foreign

| Target Class | Method | Status | Notes |
|--------------|--------|--------|-------|
| `MemorySegment` | `copy(T)` | Active | → `set(byte[])` |

## java.io

| Target Class | Method | Status | Notes |
|--------------|--------|--------|-------|
| `OutputStream` | `write(T)` | Active | → `set(byte[])` |
| `DataOutput` | `write(T)` | Active | → `set(byte[])` |
| `RandomAccessFile` | `write(T)` | Active | → `write(DataOutput)` |
| `Writer` | `write(T)` | Active | → `append(Appendable)` |
| `File` | `append(T)` | Active | → `write(OutputStream)` |

## java.net

| Target Class | Method | Status | Notes |
|--------------|--------|--------|-------|
| `DatagramPacket` | `set(DatagramPacket)` | Active | → `set(byte[])` |
| `DatagramPacket` | `append(DatagramPacket)` | Active | → `put(ByteBuffer)` |
| `DatagramSocket` | `send(T)` | Active | → `set(DatagramPacket)` |
| `DatagramSocket` | `send(T, SocketAddress)` | Active | → `set(DatagramPacket)` |
| `MulticastSocket` | `send(T)` | Deprecated | → `send(DatagramSocket)` |
| `MulticastSocket` | `send(T, SocketAddress)` | Deprecated | → `send(DatagramSocket, SocketAddress)` |
| `Socket` | `send(T)` | Active | → `write(OutputStream)` |

## java.net.http

| Target Class | Method | Status | Notes |
|--------------|--------|--------|-------|
| `HttpRequest.Builder` | `method(T, String)` | Active | → `set(byte[])` |

## java.nio

| Target Class | Method | Status | Notes |
|--------------|--------|--------|-------|
| `ByteBuffer` | `put(T)` | Active | → `set(byte[])` |
| `ByteBuffer` | `put()` | Active | → `put(ByteBuffer.allocate(BYTES))` |

## java.nio.channels

| Target Class | Method | Status | Notes |
|--------------|--------|--------|-------|
| `WritableByteChannel` | `write(T)` | Active | → `put(ByteBuffer)` |
| `DatagramChannel` | `write(T)` | Active | → `write(WritableByteChannel)` |
| `DatagramChannel` | `send(T, SocketAddress)` | Active | → `put(ByteBuffer)` |
| `SocketChannel` | `send(T)` | Deprecated | → `write(WritableByteChannel)` |
| `AsynchronousByteChannel` | `write(T)` | Active | → `put(ByteBuffer)` |
| `AsynchronousByteChannel` | `write(T, A, CompletionHandler)` | Active | → `put(ByteBuffer)` |
| `AsynchronousFileChannel` | `write(T, long)` | Active | → `put(ByteBuffer)` |
| `AsynchronousFileChannel` | `write(T, long, A, CompletionHandler)` | Active | → `put(ByteBuffer)` |
| `AsynchronousSocketChannel` | `send(T)` | Deprecated | → `write(AsynchronousByteChannel)` |
| `AsynchronousSocketChannel` | `send(T, A, CompletionHandler)` | Deprecated | → `write(AsynchronousByteChannel, ...)` |

## java.nio.file

| Target Class | Method | Status | Notes |
|--------------|--------|--------|-------|
| `Path` | `append(T)` | Active | → `write(WritableByteChannel)` |
| `Path` | `append(T, A, CompletionHandler)` | Active | → `write(AsynchronousFileChannel, ...)` |

## java.security / javax.crypto

| Target Class | Method | Status | Notes |
|--------------|--------|--------|-------|
| `MessageDigest` | `update(T)` | Active | → `set(byte[])` |
| `Signature` | `update(T)` | Active | → `set(byte[])` |
| `Mac` | `update(T)` | Active | → `set(byte[])` |
| `Cipher` | `update(T, Consumer)` | Active | → `set(byte[])` |

## java.sql

| Target Class | Method | Status | Notes |
|--------------|--------|--------|-------|
| `Blob` | `set(T, long)` | Active | → `write(OutputStream)` |

## java.util.zip

| Target Class | Method | Status | Notes |
|--------------|--------|--------|-------|
| `Checksum` | `update(T)` | Active | → `set(byte[])` |
| `Deflater` | `input(T)` | Active | → `set(byte[])` |

---

## Summary

| Metric | Count |
|--------|-------|
| **Total methods** | 40 |
| **Active methods** | 35 |
| **Deprecated methods** | 5 |
| **Abstract methods** | 1 |
| **Async (Future-based)** | 2 |
| **Async (CompletionHandler)** | 3 |
| **Factory methods** | 2 |

### Deprecated Methods

| Method | Use Instead |
|--------|-------------|
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

| Package | Target | Method | Notes |
|---------|--------|--------|-------|
| `java.net.http` | `WebSocket` | | `sendBinary(ByteBuffer, boolean)` |
| `java.nio.channels` | `GatheringByteChannel` | | Scatter/gather I/O |
| `java.nio.channels` | `SeekableByteChannel` | | Positioned writes |
| `java.util.concurrent.Flow` | `Publisher<ByteBuffer>` | | Reactive streams |
| `javax.imageio.stream` | `ImageOutputStream` | | `write(byte[])` |
| `javax.sound.sampled` | `SourceDataLine` | | Audio output |

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
