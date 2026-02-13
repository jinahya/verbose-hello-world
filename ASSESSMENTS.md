# Assessments

Code review findings per interface and method, organized by Java API package.

> **Interfaces**: `HelloWorld` (sync), `AsynchronousHelloWorld` (async), `ReactiveHelloWorld` (reactive), `HelloWorldGraphics` (graphics)
>
> **Legend**: Issues are tagged as **Stub implementation**, **Missing javadoc**, **Missing null check**, **Inconsistent style**, or specific bug descriptions. Methods with no issues are marked accordingly.

## HelloWorld

### java.lang

#### `set(byte[], int)` — abstract method
- No issues. Clean SAM.

#### `set(byte[])`
- **Stub implementation**: Body returns `null` with commented-out logic. Needs implementation.
- Javadoc and `@implSpec` are correct.

#### `set()`
- No issues. Delegates to `set(byte[])`.

#### `append(Appendable)`
- **Stub implementation**: The loop appending each byte as `char` is commented out. Needs implementation.
- Javadoc is correct.

### java.lang.foreign

#### `copy(MemorySegment)`
- No issues. Null check, size check, delegates to `set(byte[])`.

### java.io

#### `write(OutputStream)`
- No issues. Null check, delegates to `set(byte[])`, writes array.

#### `append(File)`
- **Stub implementation**: `FileOutputStream` creation and `write(stream)` call are commented out. Needs implementation.

#### `write(DataOutput)`
- **Stub implementation**: `output.write(array)` is commented out. Needs implementation.

#### `write(RandomAccessFile)`
- **Stub implementation**: `file.write(array)` is commented out. Needs implementation.

#### `write(Writer)`
- **Stub implementation**: `append(writer)` is commented out. Needs implementation.

### java.net

#### `set(DatagramPacket)`
- No issues.

#### `append(DatagramPacket)`
- No issues. Uses `ByteBuffer.wrap` to write at offset+length, then extends packet length.

#### `send(DatagramSocket)` — connected
- No issues. Validates connected state, delegates to `set(DatagramPacket)`.

#### `send(DatagramSocket, SocketAddress)` — with target
- No issues.

#### ~~`send(MulticastSocket)`~~ — deprecated
- No issues. Delegates to `send(DatagramSocket)`.

#### ~~`send(MulticastSocket, SocketAddress)`~~ — deprecated
- No issues. Delegates to `send(DatagramSocket, SocketAddress)`.

#### `send(Socket)`
- **Stub implementation**: `write(stream)` is commented out. Needs implementation.

### java.net.http

#### `method(HttpRequest.Builder, String)`
- No issues.

### java.nio

#### `put(ByteBuffer)`
- No issues. Handles both array-backed and direct buffers correctly.

#### `put()`
- No issues. Allocates, puts, flips.

### java.nio.channels

#### `write(WritableByteChannel)`
- No issues. Loop with `hasRemaining` is correct.

#### ~~`write(GatheringByteChannel)`~~ — deprecated
- No issues. Correctly deprecated, delegates via `ByteBuffer[]` array.

#### ~~`write(SeekableByteChannel)`~~ — deprecated
- **Missing javadoc**: No javadoc at all.
- Delegates to `write(WritableByteChannel)` via cast.

#### `send(DatagramChannel, SocketAddress)`
- **Partial write not retried**: `channel.send()` is atomic for datagrams (sends all or nothing), so the `written != BYTES` check correctly throws. No issue.
- `SO_SNDBUF` assertion is advisory only; fine for educational use.

#### `write(DatagramChannel)` — connected
- Same pattern as `send(DatagramChannel, SocketAddress)`. No issues.

#### ~~`send(SocketChannel)`~~ — deprecated
- No issues. Delegates to `write(WritableByteChannel)`.

#### `write(AsynchronousByteChannel)` — Future-based
- **ExecutionException unwrapping**: Correctly re-throws `InterruptedException`, `Error`, `RuntimeException`, `IOException`, and wraps anything else in `RuntimeException`. No issues.

#### ~~`send(AsynchronousSocketChannel)`~~ — deprecated
- No issues. Delegates to `write(AsynchronousByteChannel)`.

#### `write(AsynchronousByteChannel, A, CompletionHandler)`
- **Stub implementation**: The `channel.write(...)` call with internal `CompletionHandler` is commented out. Needs implementation.
- Javadoc and `@implSpec` are correct.

#### ~~`send(AsynchronousSocketChannel, A, CompletionHandler)`~~ — deprecated
- No issues. Delegates to `write(AsynchronousByteChannel, A, CompletionHandler)`.

#### `write(AsynchronousFileChannel, long)` — Future-based
- No issues. Same `ExecutionException` unwrapping pattern as `write(AsynchronousByteChannel)`.

#### `write(AsynchronousFileChannel, long, A, CompletionHandler)`
- No issues in specification. Implementation uses internal `Long` attachment to track position. Correct.

#### `append(Path, A, CompletionHandler)`
- Will be removed from `HelloWorld` (moved to `AsynchronousHelloWorld`).

### java.nio.file

#### `append(Path)`
- **Stub implementation**: `FileChannel.open`, `write(channel)`, `force(true)` are all commented out. Needs implementation.

### java.security / javax.crypto

#### `update(MessageDigest)`
- No issues.

#### `update(Signature)`
- No issues.

#### `update(Cipher, Consumer)`
- No issues.

#### `update(Mac)`
- No issues.

### java.sql

#### `set(Blob, long)`
- **Missing javadoc**: No javadoc at all.
- Implementation looks correct; loops until all bytes are written via `setBytes`.

### java.util

#### `set(BitSet, int)`
- **Inconsistent `@throws` style**: Uses "when" instead of "if" for both `NullPointerException` and `IllegalArgumentException`.
- Implementation is correct (little-endian bit order).

### java.util.zip

#### `update(Checksum)`
- **Missing javadoc**: No javadoc at all.

#### `input(Deflater)`
- **Missing javadoc**: No javadoc at all.
- **Missing null check**: No `Objects.requireNonNull` for `deflater`.

---

## AsynchronousHelloWorld

### Static factory methods

#### `from(HelloWorld)` — static factory
- No issues. Null check, wraps in `DefaultAsynchronousHelloWorld`.

### General async

#### `applyAsync(T, BiFunction, Executor)` — abstract
- No issues.

#### `applyAsync(T, BiFunction)`
- No issues. Delegates to `applyAsync(T, BiFunction, ForkJoinPool.commonPool())`.

### java.net.http

#### `sendBinary(WebSocket, boolean)` — abstract
- No issues in specification.

#### `sendPing(WebSocket)` — abstract
- No issues in specification.

#### `sendPong(WebSocket)` — abstract
- No issues in specification.

### java.nio.channels

#### `write(AsynchronousByteChannel, A, CompletionHandler)` — abstract
- No issues. Javadoc and snippet are correct.

#### ~~`send(AsynchronousSocketChannel, A, CompletionHandler)`~~ — deprecated
- No issues. Delegates to `write(AsynchronousByteChannel, A, CompletionHandler)`.

#### `write(AsynchronousFileChannel, long, A, CompletionHandler)` — abstract
- No issues. Javadoc complete with `@throws` for NPE and IAE.

### java.nio.file

#### `append(Path, A, CompletionHandler)`
- No issues after fixes applied:
  - `force()`/`close()` separated so `close()` always runs even if `force()` throws.
  - `addSuppressed` used in `failed` path to preserve original exception when `close()` throws.
  - `addSuppressed` used in `completed` path when `force()` fails and `close()` also fails.
- **TOCTOU race** documented via `@implNote`: `channel.size()` and `write()` are not atomic.
- `@throws NullPointerException` documented for `path` and `handler`.

### DefaultAsynchronousHelloWorld (implementation)

#### `applyAsync(T, BiFunction, Executor)`
- No issues. Uses `CompletableFuture.supplyAsync`.

#### `sendBinary(WebSocket, boolean)`
- No issues.

#### `sendPing(WebSocket)`
- **Stub implementation**: Returns `null`. Needs implementation.

#### `sendPong(WebSocket)`
- **Stub implementation**: Returns `null`. Needs implementation.

#### `write(AsynchronousByteChannel, A, CompletionHandler)`
- No issues. Recursive callback pattern with `this` reference. Correctly handles partial writes.

#### `write(AsynchronousFileChannel, long, A, CompletionHandler)`
- No issues. Uses internal `Long` attachment to track position across partial writes. Correctly increments position by result.

## ReactiveHelloWorld

*(pending)*

## HelloWorldGraphics

*(pending)*
