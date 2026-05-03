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
| `HelloWorld` | `FileOutputStream` | `write(T)` | Deprecated | → `write(OutputStream)` |
| `HelloWorld` | `FilterOutputStream` | `write(T)` | Deprecated | → `write(OutputStream)` |
| `HelloWorld` | `BufferedOutputStream` | `write(T)` | Deprecated | → `write(FilterOutputStream)` |
| `HelloWorld` | `PipedOutputStream` | `write(T)` | Deprecated | → `write(OutputStream)` |
| `HelloWorld` | `ObjectOutputStream` | `write(T)` | Deprecated | → `write(OutputStream)` |
| `HelloWorld` | `DataOutput` | `write(T)` | Active | → `set(byte[])` |
| `HelloWorld` | `DataOutputStream` | `write(T)` | Deprecated | → `write(DataOutput)` / `write(FilterOutputStream)` |
| `HelloWorld` | `RandomAccessFile` | `write(T)` | Active | → `write(DataOutput)` |
| `HelloWorld` | `Writer` | `write(T)` | Active | → `append(Appendable)` |
| `HelloWorld` | `BufferedWriter` | `write(T)` | Deprecated | → `write(Writer)` |
| `HelloWorld` | `CharArrayWriter` | `write(T)` | Deprecated | → `write(Writer)` |
| `HelloWorld` | `FilterWriter` | `write(T)` | Deprecated | → `write(Writer)` |
| `HelloWorld` | `OutputStreamWriter` | `write(T)` | Deprecated | → `write(Writer)` |
| `HelloWorld` | `PipedWriter` | `write(T)` | Deprecated | → `write(Writer)` |
| `HelloWorld` | `PrintWriter` | `write(T)` | Deprecated | → `write(Writer)` |
| `HelloWorld` | `StringWriter` | `write(T)` | Deprecated | → `write(Writer)` |
| `HelloWorld` | `File` | `append(T)` | Active | → `write(OutputStream)` |
| `HelloWorld` | `File` | `append(T, Charset)` | Active | → `write(OutputStreamWriter)` |

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
| `HelloWorld` | `CharBuffer` | `put(T)` | Deprecated | → `append(Appendable)` |
| `HelloWorld` | `IntBuffer` | `put(T)` | Deprecated | → `put(ShortBuffer)` |
| `HelloWorld` | `LongBuffer` | `put(T)` | Deprecated | → `put(IntBuffer)` |
| `HelloWorld` | `MappedByteBuffer` | `put(T)` | Deprecated | → `put(ByteBuffer)` |
| `HelloWorld` | `ShortBuffer` | `put(T)` | Deprecated | → `put(ByteBuffer)` |

## java.nio.channels

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `WritableByteChannel` | `write(T)` | Active | → `put(ByteBuffer)` |
| `HelloWorld` | `GatheringByteChannel` | `write(T)` | Deprecated | → `put(ByteBuffer)` |
| `HelloWorld` | `SeekableByteChannel` | `write(T)` | Deprecated | → `write(WritableByteChannel)` |
| `HelloWorld` | `DatagramChannel` | `write(T)` | Active | → `write(WritableByteChannel)` |
| `HelloWorld` | `DatagramChannel` | `send(T, SocketAddress)` | Active | → `put(ByteBuffer)` |
| `HelloWorld` | `Pipe.SinkChannel` | `write(T)` | Deprecated | → `write(WritableByteChannel)` |
| `HelloWorld` | `SocketChannel` | `send(T)` | Deprecated | → `write(WritableByteChannel)` |
| `HelloWorld` | `AsynchronousByteChannel` | `write(T)` | Active | → `put(ByteBuffer)` |
| `AsynchronousHelloWorld` | `AsynchronousByteChannel` | `write(T, A, CompletionHandler)` | Active | → `put(ByteBuffer)` |
| `HelloWorld` | `AsynchronousFileChannel` | `write(T, long)` | Active | → `put(ByteBuffer)` |
| `AsynchronousHelloWorld` | `AsynchronousFileChannel` | `write(T, long, A, CompletionHandler)` | Active | → `put(ByteBuffer)` |
| `HelloWorld` | `AsynchronousSocketChannel` | `send(T)` | Deprecated | → `write(AsynchronousByteChannel)` |
| `AsynchronousHelloWorld` | `AsynchronousSocketChannel` | `send(T, A, CompletionHandler)` | Deprecated | → `write(AsynchronousByteChannel, ...)` |

## java.nio.file

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `Path` | `append(T)` | Active | → `write(WritableByteChannel)` |
| `AsynchronousHelloWorld` | `Path` | `append(T, A, CompletionHandler)` | Active | → `write(AsynchronousFileChannel, ...)` |

## java.security

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `MessageDigest` | `update(T)` | Active | → `set(byte[])` |
| `HelloWorld` | `DigestOutputStream` | `write(T)` | Deprecated | → `write(OutputStream)` |
| `HelloWorld` | `Signature` | `update(T)` | Active | → `set(byte[])` |

## java.sql

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `Blob` | `set(T, long)` | Active | → `write(OutputStream)` |

## java.util

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `BitSet` | `set(T, int)` | Active | → `set(byte[])`, little-endian bit order |
| `HelloWorld` | `BitSet` | `set(T)` | Active | → `set(bitset, 0)` |
| `HelloWorld` | `Collection<? super Byte>` | `collect(T)` | Active | → `set(byte[])` |

## java.util.function

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `Consumer<? super Byte>` | `accept(T)` | Active | → `set(byte[])` |

## java.util.stream

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `Stream.Builder<? super Byte>` | `accept(T)` | Deprecated | → `accept(Consumer)` |

## java.util.jar

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `JarOutputStream` | `write(T)` | Deprecated | → `write(ZipOutputStream)` |
| `HelloWorld` | `JarOutputStream` | `put(T, String)` | Deprecated | → `put(ZipOutputStream, String)` |

## java.util.zip

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `Checksum` | `update(T)` | Active | → `set(byte[])` |
| `HelloWorld` | `Deflater` | `input(T)` | Active | → `set(byte[])` |
| `HelloWorld` | `DeflaterOutputStream` | `write(T)` | Deprecated | → `write(FilterOutputStream)` |
| `HelloWorld` | `GZIPOutputStream` | `write(T)` | Deprecated | → `write(DeflaterOutputStream)` |
| `HelloWorld` | `ZipOutputStream` | `write(T)` | Deprecated | → `write(DeflaterOutputStream)` |
| `HelloWorld` | `ZipOutputStream` | `put(T, String)` | Active | → `write(OutputStream)`, creates/closes `ZipEntry` |

## javax.crypto

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `Cipher` | `update(T, Consumer)` | Active | → `set(byte[])` |
| `HelloWorld` | `CipherOutputStream` | `write(T)` | Deprecated | → `write(FilterOutputStream)` |
| `HelloWorld` | `Mac` | `update(T)` | Active | → `set(byte[])` |

## javax.net.ssl

| Interface | Target Class | Method | Status | Notes |
|-----------|--------------|--------|--------|-------|
| `HelloWorld` | `SSLSocket` | `send(T)` | Deprecated | → `send(Socket)` |

---

## Summary

| Metric | Count |
|--------|-------|
| **Total methods** | 77 |
| **Active methods** | 43 |
| **Deprecated methods** | 34 |
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
| `write(FilterOutputStream)` | `write(OutputStream)` |
| `write(DeflaterOutputStream)` | `write(FilterOutputStream)` |
| `write(GZIPOutputStream)` | `write(DeflaterOutputStream)` |
| `write(ZipOutputStream)` | `put(ZipOutputStream, String)` |
| `put(CharBuffer)` | `append(Appendable)` |
| `put(ShortBuffer)` | `put(ByteBuffer)` |
| `put(IntBuffer)` | `put(ShortBuffer)` |
| `put(LongBuffer)` | `put(IntBuffer)` |
| `write(BufferedOutputStream)` | `write(FilterOutputStream)` |
| `write(CipherOutputStream)` | `write(FilterOutputStream)` |
| `write(DigestOutputStream)` | `write(OutputStream)` |
| `write(FileOutputStream)` | `write(OutputStream)` |
| `write(PipedOutputStream)` | `write(OutputStream)` |
| `write(ObjectOutputStream)` | `write(OutputStream)` |
| `write(DataOutputStream)` | `write(DataOutput)` |
| `write(BufferedWriter)` | `write(Writer)` |
| `write(CharArrayWriter)` | `write(Writer)` |
| `write(FilterWriter)` | `write(Writer)` |
| `write(OutputStreamWriter)` | `write(Writer)` |
| `write(PipedWriter)` | `write(Writer)` |
| `write(PrintWriter)` | `write(Writer)` |
| `write(StringWriter)` | `write(Writer)` |
| `send(SSLSocket)` | `send(Socket)` |
| `put(MappedByteBuffer)` | `put(ByteBuffer)` |
| `write(Pipe.SinkChannel)` | `write(WritableByteChannel)` |
| `write(JarOutputStream)` | `write(ZipOutputStream)` |
| `put(JarOutputStream, String)` | `put(ZipOutputStream, String)` |
| `accept(Stream.Builder<? super Byte>)` | `accept(Consumer)` |

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

