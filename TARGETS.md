# TARGETS

Analysis of `HelloWorld.java` interface methods organized by target type and Java API package.

## java.lang

### byte[]
- `set(byte[], int)` - Sets hello-world-bytes on array at index (abstract method)
- `set(byte[])` - Sets hello-world-bytes on array at index 0
- `set()` - Returns new array with hello-world-bytes

### java.lang.Appendable
- `append(T extends Appendable)` - Appends hello-world-bytes to appendable

## java.io

### java.io.OutputStream
- `write(T extends OutputStream)` - Writes hello-world-bytes to output stream

### java.io.File
- `append(T extends File)` - Appends hello-world-bytes to end of file

### java.io.DataOutput
- `write(T extends DataOutput)` - Writes hello-world-bytes to data output

### java.io.RandomAccessFile
- `write(T extends RandomAccessFile)` - Writes hello-world-bytes to random access file

### java.io.Writer
- `write(T extends Writer)` - Writes hello-world-bytes to writer

## java.net

### java.net.DatagramPacket
- `set(DatagramPacket)` - Sets hello-world-bytes in datagram packet

### java.net.DatagramSocket
- `send(T extends DatagramSocket)` - Sends hello-world-bytes via connected datagram socket

### java.net.Socket
- `send(T extends Socket)` - Sends hello-world-bytes through socket

## java.nio

### java.nio.ByteBuffer
- `put(T extends ByteBuffer)` - Puts hello-world-bytes on byte buffer
- `put()` - Returns new buffer with hello-world-bytes (ready to drain)

## java.nio.channels

### java.nio.channels.WritableByteChannel
- `write(T extends WritableByteChannel)` - Writes hello-world-bytes to writable byte channel (sync)

### java.nio.channels.DatagramChannel (deprecated)
- ~~`send(T extends DatagramChannel)`~~ - Use `write(WritableByteChannel)` instead

### java.nio.channels.SocketChannel (deprecated)
- ~~`send(T extends SocketChannel)`~~ - Use `write(WritableByteChannel)` instead

### java.nio.channels.AsynchronousByteChannel
- `write(T extends AsynchronousByteChannel)` - Writes hello-world-bytes (Future-based)
- `write(T, A, CompletionHandler)` - Writes hello-world-bytes async with completion handler

### java.nio.channels.AsynchronousSocketChannel (deprecated)
- ~~`send(T extends AsynchronousSocketChannel)`~~ - Use `write(AsynchronousByteChannel)` instead
- ~~`send(T, A, CompletionHandler)`~~ - Use `write(AsynchronousByteChannel, A, CompletionHandler)` instead

### java.nio.channels.AsynchronousFileChannel
- `write(T, long position)` - Writes to async file channel at position (Future-based)
- `write(T, long position, A, CompletionHandler)` - Writes to async file channel async with handler

## java.nio.file

### java.nio.file.Path
- `append(T extends Path)` - Appends hello-world-bytes to end of file at path (sync)
- `append(T, A, CompletionHandler)` - Appends to file at path async with handler

## java.lang.foreign

### java.lang.foreign.MemorySegment
- `set(MemorySegment)` - Sets hello-world-bytes on memory segment at offset 0

## java.security

### java.security.MessageDigest
- `update(T extends MessageDigest)` - Updates message digest with hello-world-bytes

### java.security.Signature
- `update(T extends Signature)` - Updates signature with hello-world-bytes

### javax.crypto.Cipher
- `update(T extends Cipher)` - Updates cipher with hello-world-bytes

### javax.crypto.Mac
- `update(T extends Mac)` - Updates MAC with hello-world-bytes

## java.sql

### java.sql.Blob
- `set(T extends Blob, long pos)` - Sets hello-world-bytes in BLOB at position

## java.util.zip

### java.util.zip.Deflater
- `input(T extends Deflater)` - Sets hello-world-bytes as deflater input

### java.util.zip.Checksum
- `update(T extends Checksum)` - Updates checksum with hello-world-bytes

## Summary

- **Total methods**: 35 (31 unique + 4 deprecated)
- **Abstract methods**: 1 (`set(byte[], int)`)
- **Deprecated methods**: 4
  - `send(DatagramChannel)` → use `write(WritableByteChannel)`
  - `send(SocketChannel)` → use `write(WritableByteChannel)`
  - `send(AsynchronousSocketChannel)` → use `write(AsynchronousByteChannel)`
  - `send(AsynchronousSocketChannel, A, CompletionHandler)` → use `write(AsynchronousByteChannel, A, CompletionHandler)`
- **Asynchronous methods**: 4
  - Future-based: `write(AsynchronousByteChannel)`, `write(AsynchronousFileChannel, long)`
  - CompletionHandler-based: `write(AsynchronousByteChannel, A, CompletionHandler)`, `write(AsynchronousFileChannel, long, A, CompletionHandler)`, `append(Path, A, CompletionHandler)`

## Method Patterns

1. **Synchronous blocking**: Most methods (OutputStream, Writer, File, Socket, channels, etc.)
2. **Asynchronous Future-based**: Return type allows `.get()` blocking or async chaining
3. **Asynchronous CompletionHandler**: Callback-based async operations
4. **Factory methods**: `set()`, `put()` - create and return new objects pre-populated with hello-world-bytes

## To be added

### java.net
- ~~`DatagramSocket`~~ - **ADDED** to HelloWorld.java (send via connected socket)
- ~~`DatagramChannel`~~ - **ADDED** to HelloWorld.java (redundant, use WritableByteChannel)
- `MulticastSocket` - Multicast datagram transmission
- **REDUNDANT**: `ServerSocket` - Covered by Socket via accept().getOutputStream()
- **REDUNDANT**: `URLConnection` / `HttpURLConnection` - Covered by OutputStream via getOutputStream()

### java.nio.channels
- `GatheringByteChannel` - Scatter/gather I/O (already in MoreHelloWorld.java)
- `SeekableByteChannel` - Positioned writes
- `DatagramChannel` - Non-blocking datagram I/O
- **REDUNDANT**: `Pipe.SinkChannel` - Covered by WritableByteChannel (educational: pipe pattern)
- **REDUNDANT**: `SelectableChannel` with `Selector` - Covered by WritableByteChannel (educational: multiplexed I/O)

### java.nio (Buffers)
- **REDUNDANT**: `CharBuffer` - Covered by Appendable
- **REDUNDANT**: `MappedByteBuffer` - Covered by ByteBuffer (force/load are caller's responsibility)
- **REDUNDANT**: Direct vs Heap `ByteBuffer` - Covered by ByteBuffer

### java.lang.foreign (Foreign Function & Memory API)
- ~~`MemorySegment`~~ - **ADDED** to HelloWorld.java
- **REDUNDANT**: `Arena` - Factory pattern, callers can allocate and call write(segment)
- **REDUNDANT**: `SegmentAllocator` - Factory pattern, callers can allocate and call write(segment)
- `VarHandle` - Low-level memory access

### java.io (Additional)
- **REDUNDANT**: `PrintStream` - Covered by OutputStream (educational: print/println methods)
- **REDUNDANT**: `PrintWriter` - Covered by Writer (educational: print/println methods)
- **REDUNDANT**: `BufferedOutputStream` - Covered by OutputStream
- **REDUNDANT**: `BufferedWriter` - Covered by Writer
- **REDUNDANT**: `DataOutputStream` - Covered by DataOutput (already handled)
- **REDUNDANT**: `ObjectOutputStream` - Covered by OutputStream (educational: serialization)
- **REDUNDANT**: `PipedOutputStream` - Covered by OutputStream (educational: pipe pattern)
- **REDUNDANT**: `FileDescriptor` - Not a writable target, requires wrapping in FileOutputStream (covered by OutputStream)

### java.nio.file (Additional)
- `Files.write(Path, byte[])` - High-level utility method
- **REDUNDANT**: `Files.newOutputStream(Path)` - Returns OutputStream, covered
- **REDUNDANT**: `Files.newBufferedWriter(Path)` - Returns Writer, covered
- **REDUNDANT**: `FileStore` - Not an I/O target

### java.util.concurrent
- **REDUNDANT**: `BlockingQueue<Byte>` - Not an I/O target (concurrency pattern)
- **REDUNDANT**: `Exchanger<byte[]>` - Not an I/O target (concurrency pattern)
- **REDUNDANT**: `CompletableFuture<byte[]>` - Not an I/O target (async pattern)
- **REDUNDANT**: `ForkJoinPool` - Not an I/O target

### Reactive Streams (java.util.concurrent.Flow)
- `Flow.Publisher<Byte>` - Reactive byte publisher
- `Flow.Subscriber<Byte>` - Reactive byte consumer
- `SubmissionPublisher<Byte>` - Concrete publisher implementation

### java.util.zip / java.util.jar
- **REDUNDANT**: `GZIPOutputStream` - Covered by OutputStream
- **REDUNDANT**: `ZipOutputStream` - Covered by OutputStream
- **REDUNDANT**: `JarOutputStream` - Covered by OutputStream
- ~~`Deflater`~~ - **ADDED** to HelloWorld.java (input with hello-world-bytes)
- ~~`Checksum`~~ - **ADDED** to HelloWorld.java (update CRC32, CRC32C, Adler32 with hello-world-bytes)

### java.security / javax.crypto
- ~~`MessageDigest`~~ - **ADDED** to HelloWorld.java (update with hello-world-bytes)
- ~~`Signature`~~ - **ADDED** to HelloWorld.java (update with hello-world-bytes)
- ~~`Cipher`~~ - **ADDED** to HelloWorld.java (update with hello-world-bytes)
- ~~`Mac`~~ - **ADDED** to HelloWorld.java (update with hello-world-bytes)
- **REDUNDANT**: `CipherOutputStream` - Covered by OutputStream

### java.nio.charset
- `CharsetEncoder` - Explicit encoding (transformation, not I/O)
- **REDUNDANT**: `CoderResult` - Not an I/O target

### JMX (javax.management)
- **REDUNDANT**: `MBeanServer` - Not an I/O target
- **REDUNDANT**: Notification - Not an I/O target

### Logging Frameworks
- **REDUNDANT**: `java.util.logging.Logger` - Not an I/O target
- **REDUNDANT**: SLF4J `Logger` - Not an I/O target

### Network Protocols
- `HttpClient` - Modern HTTP request body (java.net.http)
- `WebSocket` - Send as binary frame
- **REDUNDANT**: UNIX Domain Sockets - Covered by SocketChannel (just different address)

### Serialization
- **REDUNDANT**: `Externalizable` - Uses ObjectOutput → DataOutput, covered
- **REDUNDANT**: Java Serialization - Covered by ObjectOutputStream

### Memory-Mapped I/O
- **REDUNDANT**: `FileChannel.map()` - Returns MappedByteBuffer, covered by put(ByteBuffer)

### Process I/O
- **REDUNDANT**: `Process.getOutputStream()` - Covered by OutputStream (educational: process stdin)
- **REDUNDANT**: `ProcessBuilder` - Not an I/O target, uses Process

### Console
- **REDUNDANT**: `System.console().writer()` - Covered by Writer
- **REDUNDANT**: `System.out` vs `System.err` - Covered by PrintStream

### NIO.2 (Async I/O)
- **REDUNDANT**: `AsynchronousServerSocketChannel` - Covered by AsynchronousSocketChannel via accept()
- **REDUNDANT**: Completion handlers with timeout - Already covered in async methods

### Module System
- **REDUNDANT**: Module layer - Not an I/O target
- **REDUNDANT**: Module resource - Not an I/O target

### java.net.http (HTTP Client API - Java 11+)
- `HttpRequest.BodyPublisher` - Publish hello-world-bytes as HTTP request body (`BodyPublishers.ofByteArray(byte[])`)
- `WebSocket` - Send hello-world-bytes as binary frame (`sendBinary(ByteBuffer, boolean)`)

### javax.sound.sampled (Audio I/O)
- `SourceDataLine` - Write hello-world-bytes to audio output (`write(byte[], int, int)`)
- **REDUNDANT**: `AudioInputStream` - Reading, not writing
- **REDUNDANT**: `Clip` - Uses AudioInputStream for loading

### javax.imageio.stream (Image I/O Streams)
- `ImageOutputStream` - Write hello-world-bytes to image output stream (`write(byte[])`)
- **REDUNDANT**: `ImageInputStream` - Reading, not writing

### java.sql (JDBC)
- ~~`Blob`~~ - **ADDED** to HelloWorld.java (`set(Blob, long pos)`)
- `PreparedStatement` - Bind hello-world-bytes as parameter (`setBytes(int, byte[])`)
- **Note**: Requires JDBC context/connection, may not fit the HelloWorld pattern cleanly

### java.util.BitSet
- `BitSet` - Set bits from hello-world-bytes (`valueOf(byte[])` returns new BitSet, or manual bit setting)
- **Note**: Transformation rather than I/O

### java.lang.invoke
- `VarHandle` - Low-level memory access via VarHandle operations
- **Note**: Complex setup, educational for advanced memory access patterns

### java.nio.channels (Additional)
- `Pipe` - Write to Pipe.SinkChannel (educational: inter-thread communication)
  - **REDUNDANT** as target (SinkChannel is WritableByteChannel), but useful as a pattern demo
- `ScatteringByteChannel` / `GatheringByteChannel` - Scatter/gather I/O (in MoreHelloWorld.java)

### java.util.Formatter
- `Formatter` - Format hello-world-bytes via `%s` with new String(bytes) or hex format
- **Note**: Text formatting rather than byte I/O

### java.io.Console
- **REDUNDANT**: `Console.writer()` - Covered by Writer
- **REDUNDANT**: `Console.printf()` - Text output, not byte I/O

### javax.xml.transform.stream
- **REDUNDANT**: `StreamResult` - Wraps OutputStream/Writer, already covered

### java.rmi (Remote Method Invocation)
- **REDUNDANT**: RMI streams - Use standard I/O streams

### java.util.jar
- `JarEntry` / `Manifest` - Not byte I/O targets
- **REDUNDANT**: `JarOutputStream` - Covered by OutputStream

### jdk.jfr (Java Flight Recorder)
- **REDUNDANT**: Event recording - Not a byte I/O target

### javax.net.ssl
- **REDUNDANT**: `SSLSocket` - Extends Socket, covered
- **REDUNDANT**: `SSLEngine` - Uses ByteBuffer, already covered

### java.util.UUID
- **Note**: Could construct UUID from hello-world-bytes (transformation, not I/O)
