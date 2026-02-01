package com.github.jinahya.hello.api;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public interface AsynchronousHelloWorld {

    /**
     * Creates a new instance of {@link AsynchronousHelloWorld} from the specified underlying
     * {@link HelloWorld} service and executor.
     *
     * @param underlying the underlying {@link HelloWorld} service.
     * @param executor   the executor to use for asynchronous operations.
     * @return a new instance of {@link AsynchronousHelloWorld}.
     * @throws NullPointerException if either {@code underlying} or {@code executor} is
     *                              {@code null}.
     */
    static AsynchronousHelloWorld from(final HelloWorld underlying, final Executor executor) {
        return new DefaultAsynchronousHelloWorld(underlying, executor);
    }

    /**
     * Creates a new instance of {@link AsynchronousHelloWorld} from the specified underlying
     * {@link HelloWorld} service using a virtual thread executor.
     * <p>
     * This is equivalent to:
     * {@snippet lang = "java":
     * from(underlying, Executors.newVirtualThreadPerTaskExecutor());
     *}
     *
     * @param underlying the underlying {@link HelloWorld} service.
     * @return a new instance of {@link AsynchronousHelloWorld}.
     * @throws NullPointerException if {@code underlying} is {@code null}.
     * @implSpec This method delegates to
     * {@link #from(HelloWorld, Executor) from(underlying, executor)} with an executor created by
     * {@link Executors#newVirtualThreadPerTaskExecutor()}.
     * @see Executors#newVirtualThreadPerTaskExecutor()
     */
    static AsynchronousHelloWorld from(final HelloWorld underlying) {
        return from(underlying, Executors.newVirtualThreadPerTaskExecutor());
    }

    // ----------------------------------------------------------------------------------- java.lang

    /**
     * Sets, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * on the specified array starting at the specified index.
     * <p>
     * The elements in the array, on successful completion, will be set as follows.
     * <pre>
     *  0  &lt;= index            index+12    &lt;= array.length
     *  ↓     ↓                       ↓       ↓
     * | |...|h|e|l|l|o|,| |w|o|r|l|d| |...| |
     * </pre>
     *
     * @param array the array on which bytes are set.
     * @param index the starting index of the {@code array} to which bytes are set.
     * @return a {@link CompletionStage} that, when completed, returns the given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code index} is negative, or ({@code index} plus
     *                                   {@value HelloWorld#BYTES}) is greater than
     *                                   {@code array.length}.
     * @implSpec Default implementation invokes, asynchronously, the underlying
     * {@link HelloWorld#set(byte[], int) set(array, index)} method.
     * @see HelloWorld#set(byte[], int)
     */
    CompletionStage<byte[]> set(byte[] array, int index);

    /**
     * Sets, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * on the specified array starting at {@code 0}.
     * <p>
     * The elements in the array, on successful completion, will be set as follows.
     * <pre>
     *  0                      12     &lt;= array.length
     *  ↓                       ↓        ↓
     * |h|e|l|l|o|,| |w|o|r|l|d| |....| |
     * </pre>
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * HelloWorldValidator.requireValid(array);
     * return set(array, 0);
     *}
     *
     * @param array the array on which bytes are set.
     * @return a {@link CompletionStage} that, when completed, returns the given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code array.length} is less than
     *                                   {@link HelloWorld#BYTES}({@value HelloWorld#BYTES}).
     * @implSpec Default implementation validates the {@code array} using
     * {@link HelloWorldValidator#requireValid(byte[])}, and then invokes
     * {@link #set(byte[], int) set(array, 0)}.
     * @see #set(byte[], int)
     * @see HelloWorldValidator#requireValid(byte[])
     */
    default CompletionStage<byte[]> set(final byte[] array) {
        HelloWorldValidator.requireValid(array);
        return set(array, 0);
    }

    /**
     * Appends, asynchronously, the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified appendable.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * Objects.requireNonNull(appendable, "appendable is null");
     * return CompletableFuture.supplyAsync(
     *         () -> {
     *             try {
     *                 return service.append(appendable);
     *             } catch (final IOException ioe) {
     *                 throw new UncheckedIOException(ioe);
     *             }
     *         },
     *         executor
     * );
     *}
     *
     * @param <T>        appendable type parameter
     * @param appendable the appendable to which bytes are appended.
     * @return a {@link CompletionStage} that, when completed, returns the given {@code appendable}.
     * If an I/O error occurs, the stage completes exceptionally with an
     * {@link UncheckedIOException} wrapping the underlying {@link IOException}.
     * @throws NullPointerException if {@code appendable} is {@code null}.
     * @implSpec Default implementation invokes, asynchronously, the underlying
     * {@link HelloWorld#append(Appendable) append(appendable)} method. Any {@link IOException}
     * thrown during the operation is wrapped in an {@link UncheckedIOException} and propagated
     * through the returned {@link CompletionStage}.
     * @see HelloWorld#append(Appendable)
     * @see UncheckedIOException
     */
    <T extends Appendable> CompletionStage<T> append(final T appendable);

    // ------------------------------------------------------------------------------------- java.io

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to the specified output stream.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * Objects.requireNonNull(stream, "stream is null");
     * return CompletableFuture.supplyAsync(
     *         () -> {
     *             try {
     *                 return service.write(stream);
     *             } catch (final IOException ioe) {
     *                 throw new UncheckedIOException(ioe);
     *             }
     *         },
     *         executor
     * );
     *}
     *
     * @param <T>    stream type parameter
     * @param stream the output stream to which bytes are written.
     * @return a {@link CompletionStage} that, when completed, returns the given {@code stream}. If
     * an I/O error occurs, the stage completes exceptionally with an {@link UncheckedIOException}
     * wrapping the underlying {@link IOException}.
     * @throws NullPointerException if {@code stream} is {@code null}.
     * @apiNote This method does not {@link OutputStream#flush() flush} the {@code stream}.
     * @implSpec Default implementation invokes, asynchronously, the underlying
     * {@link HelloWorld#write(OutputStream) write(stream)} method. Any {@link IOException} thrown
     * during the operation is wrapped in an {@link UncheckedIOException} and propagated through the
     * returned {@link CompletionStage}.
     * @see HelloWorld#write(OutputStream)
     * @see OutputStream#write(byte[])
     * @see UncheckedIOException
     */
    <T extends OutputStream> CompletionStage<T> write(final T stream);

    /**
     * Appends, asynchronously, the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the end of the specified
     * file.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * Objects.requireNonNull(file, "file is null");
     * return CompletableFuture
     *         .supplyAsync(() -> {
     *             try {
     *                 return new FileOutputStream(file, true);
     *             } catch (final IOException ioe) {
     *                 throw new UncheckedIOException(ioe);
     *             }
     *         })
     *         .thenCompose(s -> write(s).thenApply(r -> {
     *             try {
     *                 r.flush();
     *                 r.close();
     *             } catch (final IOException ioe) {
     *                 throw new UncheckedIOException(ioe);
     *             }
     *             return file;
     *         }));
     *}
     *
     * @param <T>  file type parameter
     * @param file the file to which bytes are appended.
     * @return a {@link CompletionStage} that, when completed, returns the given {@code file}. If an
     * I/O error occurs, the stage completes exceptionally with an {@link UncheckedIOException}
     * wrapping the underlying {@link IOException}.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @implSpec Default implementation creates, asynchronously, a new {@link FileOutputStream} with
     * {@code file} in {@link FileOutputStream#FileOutputStream(File, boolean) appending mode},
     * invokes the {@link #write(OutputStream) write(stream)} method with it, and then
     * {@link OutputStream#flush() flushes} and {@link OutputStream#close() closes} the stream. Any
     * {@link IOException} thrown during these operations is wrapped in an
     * {@link UncheckedIOException} and propagated through the returned {@link CompletionStage}.
     * @see java.io.FileOutputStream#FileOutputStream(File, boolean)
     * @see #write(OutputStream)
     * @see UncheckedIOException
     */
    default <T extends File> CompletionStage<T> append(final T file) {
        Objects.requireNonNull(file, "file is null");
        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return new FileOutputStream(file, true);
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                })
                .thenCompose(s -> write(s).thenApply(r -> {
                    try {
                        r.flush();
                        r.close();
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                    return file;
                }))
                ;
    }

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to the specified writer.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * Objects.requireNonNull(writer, "writer is null");
     * return CompletableFuture.supplyAsync(
     *         () -> {
     *             try {
     *                 return service.write(writer);
     *             } catch (final IOException ioe) {
     *                 throw new UncheckedIOException(ioe);
     *             }
     *         },
     *         executor
     * );
     *}
     *
     * @param <T>    writer type parameter
     * @param writer the writer to which bytes are written.
     * @return a {@link CompletionStage} that, when completed, returns the given {@code writer}. If
     * an I/O error occurs, the stage completes exceptionally with an {@link UncheckedIOException}
     * wrapping the underlying {@link IOException}.
     * @throws NullPointerException if {@code writer} is {@code null}.
     * @implSpec Default implementation invokes, asynchronously, the underlying
     * {@link HelloWorld#write(Writer) write(writer)} method, which internally calls
     * {@link HelloWorld#append(Appendable) append(writer)}. Any {@link IOException} thrown during
     * the operation is wrapped in an {@link UncheckedIOException} and propagated through the
     * returned {@link CompletionStage}.
     * @see HelloWorld#write(Writer)
     * @see HelloWorld#append(Appendable)
     * @see UncheckedIOException
     */
    <T extends Writer> CompletionStage<T> write(final T writer);

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to the specified data output.
     *
     * @param <T>    data output type parameter
     * @param output the data output to which bytes are written
     * @return a {@link CompletionStage} that, when completed, returns the given {@code output}
     */
    <T extends java.io.DataOutput> CompletionStage<T> write(final T output);

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to the specified random access file starting at its current file pointer.
     *
     * @param <T>  random access file type parameter
     * @param file the random access file to which bytes are written
     * @return a {@link CompletionStage} that, when completed, returns the given {@code file}
     */
    <T extends java.io.RandomAccessFile> CompletionStage<T> write(final T file);

    // ------------------------------------------------------------------------------------ java.net

    /**
     * Sends, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * through the specified socket.
     *
     * @param <T>    socket type parameter
     * @param socket the socket through which bytes are sent
     * @return a {@link CompletionStage} that, when completed, returns the given {@code socket}
     */
    <T extends java.net.Socket> CompletionStage<T> send(final T socket);

    // ------------------------------------------------------------------------------------ java.nio

    /**
     * Puts, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * into the specified byte buffer.
     *
     * @param <T>    byte buffer type parameter
     * @param buffer the byte buffer into which bytes are put
     * @return a {@link CompletionStage} that, when completed, returns the given {@code buffer}
     */
    <T extends java.nio.ByteBuffer> CompletionStage<T> put(final T buffer);

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to the specified writable byte channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written
     * @return a {@link CompletionStage} that, when completed, returns the given {@code channel}
     */
    <T extends java.nio.channels.WritableByteChannel> CompletionStage<T> write(final T channel);

    /**
     * Appends, asynchronously, the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the end of the specified
     * path to a file.
     *
     * @param <T>  path type parameter
     * @param path the path to a file to which bytes are appended
     * @return a {@link CompletionStage} that, when completed, returns the given {@code path}
     */
    <T extends java.nio.file.Path> CompletionStage<T> append(final T path);

    // --------------------------------------------------------------------------- java.nio.channels

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * channel, and then, notifies a completion (or a failure) to the specified handler with the
     * specified attachment.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * Objects.requireNonNull(channel, "channel is null");
     * Objects.requireNonNull(handler, "handler is null");
     * final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
     * channel.write( // @highlight region
     *         buffer,                                    // <src>
     *         null,                                      // <attachment>
     *         new CompletionHandler<Integer, Object>() { // <handler>
     *                 @Override
     *                 public void completed(final Integer result, final Object a) {
     *                     if (!buffer.hasRemaining()) {
     *                         handler.completed(channel, attachment);
     *                         return;
     *                     }
     *                     channel.write(
     *                             buffer, // <src>
     *                             a,      // <attachment>
     *                             this    // <handler>
     *                     );
     *                 }
     *                 @Override
     *                 public void failed(final Throwable exc, final Object a) {
     *                     handler.failed(exc, attachment);
     *                 }
     *         }
     * ); // @end
     *}
     *
     * @param <T>        channel type parameter
     * @param channel    the channel to which bytes are written.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException either {@code channel} or {@code handler} is {@code null}.
     * @see HelloWorld#put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     */
    <T extends AsynchronousByteChannel, A> void write(
            final T channel,
            @Nullable final A attachment,
            final CompletionHandler<? super T, ? super A> handler);

    /**
     * Sends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * socket channel, and then, notifies a completion (or a failure) to the specified handler with
     * the specified attachment.
     *
     * @param channel    the channel to which the <a
     *                   href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> is sent.
     * @param attachment an attachment.
     * @param handler    the handler to be notified with a completion (or a failure).
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @implSpec Default implementation invokes
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler)} method with
     * {@code channel}, {@code attachment}, and {@code handler}.
     * @deprecated Invoke, directly, the
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler)} method with
     * {@code channel}, {@code attachment}, and {@code handler}.
     */
    @屋上架屋("AsynchronousSocketChannel implements AsynchronousByteChannel")
    @Deprecated(forRemoval = true)
    default <T extends AsynchronousSocketChannel, A> void send(
            final T channel, final A attachment,
            final CompletionHandler<? super T, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        write(channel, attachment, handler);
    }
}
