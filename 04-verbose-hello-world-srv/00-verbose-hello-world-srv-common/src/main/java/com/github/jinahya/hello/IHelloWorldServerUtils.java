package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.sun.nio.file.SensitivityWatchEventModifier;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.lang.System.setIn;
import static java.lang.Thread.currentThread;
import static java.net.InetAddress.getByName;
import static java.net.InetAddress.getLocalHost;
import static java.util.ServiceLoader.load;

/**
 * A utility class for Hello World servers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class IHelloWorldServerUtils {

    /**
     * Parses specified command line arguments and applies them to specified
     * function.
     *
     * @param args     the command line arguments.
     * @param function the function to apply.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @throws UnknownHostException if {@code args[1]} is not known as an
     *                              address.
     */
    static <R> R parseEndpoint(
            String[] args,
            IntFunction<
                    ? extends Function<
                            ? super InetAddress,
                            ? extends R>> function)
            throws UnknownHostException {
        Objects.requireNonNull(args, "args is null");
        Objects.requireNonNull(function, "function is null");
        var port = 0;
        if (args.length > 0) {
            port = parseInt(args[0]);
        }
        var host = getLocalHost();
        if (args.length > 1) {
            host = getByName(args[1]);
        }
        return function.apply(port).apply(host);
    }

    /**
     * Parses specified command line arguments and applies them to specified
     * function.
     *
     * @param args     the command line arguments.
     * @param function the function to apply.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @throws UnknownHostException if {@code args[1]} is not known as an
     *                              address.
     */
    static <R> R parseEndpoint(
            String[] args,
            BiFunction<? super Integer, ? super InetAddress, ? extends R> function)
            throws UnknownHostException {
        return parseEndpoint(args, p -> h -> function.apply(p, h));
    }

    /**
     * Parses specified command line arguments and returns a socket address to
     * bind.
     *
     * @param args the command line arguments.
     * @return a socket address to bind.
     * @throws UnknownHostException if {@code args[1]} is not known as an
     *                              address.
     */
    static SocketAddress parseEndpoint(String... args)
            throws UnknownHostException {
        return parseEndpoint(args, (p, h) -> new InetSocketAddress(h, p));
    }

    private static void readLineUntil(Reader source,
                                      Predicate<? super String> matcher)
            throws IOException {
        Objects.requireNonNull(source, "source is null");
        Objects.requireNonNull(matcher, "matcher is null");
        Stream<String> s;
        var reader = new BufferedReader(source); // no try-with-resources
        for (String line; (line = reader.readLine()) != null; ) {
            if (matcher.test(line)) {
                break;
            }
        }
    }

    private static void readLineUntil(InputStream source, Charset charset,
                                      Predicate<? super String> matcher)
            throws IOException {
        readLineUntil(new InputStreamReader(source, charset), matcher);
    }

    private static void readLineUntil(InputStream source,
                                      Predicate<? super String> matcher)
            throws IOException {
        readLineUntil(source, Charset.defaultCharset(), matcher);
    }

    /**
     * Keeps reading lines from {@link System#in} until it reads {@code
     * 'quit'}.
     *
     * @throws IOException if an I/O error occurs.
     */
    static void readQuit() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        for (String line; !Thread.currentThread().isInterrupted()
                          && (line = reader.readLine()) != null; ) {
            log.debug("line: {}", line);
            if (line.strip().equalsIgnoreCase("quit")) {
                break;
            }
        }
    }

    /**
     * Starts a new daemon thread which keeps reading a line from {@link
     * System#in} until it reads {@code 'quit'}.
     */
    static Thread startReadingQuit() {
        var thread = new Thread(() -> {
            var reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                for (String line; (line = reader.readLine()) != null; ) {
                    if (line.strip().equalsIgnoreCase("quit")) {
                        break;
                    }
                }
            } catch (IOException ioe) {
                log.error("failed to read a line", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    /**
     * Starts a new thread which reads "{@code quit\n}" from {@link System#in}
     * and {@link Closeable#close() closes} specified closeable.
     *
     * @param closeable the closeable to close.
     * @return a new started thread.
     */
    static Thread startReadingQuitAndClose(Closeable closeable) {
        if (closeable == null) {
            throw new NullPointerException("closeable is null");
        }
        var thread = new Thread(() -> {
            try {
                startReadingQuit().join();
            } catch (InterruptedException ie) {
                log.error("interrupted while reading 'quit'", ie);
            }
            try {
                closeable.close();
            } catch (IOException ioe) {
                log.error("failed to close {}", closeable, ioe);
            }
        });
        thread.start();
        return thread;
    }

    static void submitAndWriteQuit(Callable<Void> task) {
        Objects.requireNonNull(task, "task is null");
        var in = System.in;
        try (var pos = new PipedOutputStream();
             var pis = new PipedInputStream(pos)) {
            setIn(pis);
            var executor = Executors.newSingleThreadExecutor();
            var future = executor.submit(task);
            pos.write("quit\n".getBytes(StandardCharsets.US_ASCII));
            pos.flush();
            try {
                future.get();
            } catch (InterruptedException ie) {
                log.error("interrupted while executing", ie);
                Thread.currentThread().interrupt();
            } catch (ExecutionException ee) {
                log.error("failed to execute", ee);
            }
            IHelloWorldServerUtils.shutdownAndAwaitTermination(executor);
        } catch (IOException ioe) {
            log.error("failed to write 'quit'", ioe);
        } finally {
            setIn(in);
        }
    }

    /**
     * Loads and returns an instance of {@link HelloWorld} interface.
     *
     * @return an instance of {@link HelloWorld} interface.
     * @deprecated do not use this!
     */
    @Deprecated
    static HelloWorld loadHelloWorld() {
        return load(HelloWorld.class).iterator().next();
    }

    /**
     * {@link ExecutorService#shutdown() Shuts down} specified executor service
     * and awaits its termination for specified amount of time.
     *
     * @param executor the executor service to shut down.
     * @param timeout  a timeout for awaiting termination.
     * @param unit     a time unit for awaiting termination.
     * @see ExecutorService#shutdown()
     * @see ExecutorService#awaitTermination(long, TimeUnit)
     */
    static void shutdownAndAwaitTermination(ExecutorService executor,
                                            long timeout,
                                            TimeUnit unit) {
        Objects.requireNonNull(executor, "executor is null");
        if (timeout <= 0L) {
            throw new IllegalArgumentException(
                    "timeout(" + timeout + ") is not positive");
        }
        Objects.requireNonNull(unit, "unit is null");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeout, unit)) {
                log.warn("executor has not terminated for {} in {}: {}",
                         timeout, unit, executor);
            }
        } catch (InterruptedException ie) {
            log.error("interrupted while awaiting executor terminated", ie);
            currentThread().interrupt();
        }
    }

    /**
     * {@link ExecutorService#shutdown() Shuts down} specified executor service
     * and awaits its termination for 8 seconds.
     *
     * @param executor the executor service to shut down.
     * @see ExecutorService#shutdown()
     * @see ExecutorService#awaitTermination(long, TimeUnit)
     */
    static void shutdownAndAwaitTermination(ExecutorService executor) {
        shutdownAndAwaitTermination(executor, 8L, TimeUnit.SECONDS);
    }

    static void setAndNotify(ThreadLocal<Integer> port) {
        Objects.requireNonNull(port, "port is null");
    }

    /**
     * The name of the file to which a port number is written.
     */
    private static final String PORT_TXT = "port.txt";

    /**
     * Writes specified port number to a file newly created in specified
     * directory.
     *
     * @param dir  the directory in which a new file is created.
     * @param port the port number to be written.
     * @return a path to the file whose content is given {@code port} written in
     * big-endian byte order; the path's size is {@value
     * java.lang.Short#BYTES}.
     * @throws IOException if an I/O error occurs.
     * @see #readPortNumber(Path)
     * @see #readPortNumber(Path, IntConsumer)
     */
    static void writePortNumber(Path dir, int port)
            throws IOException {
        if (!Files.isDirectory(Objects.requireNonNull(dir, "dir is null"))) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("invalid port : " + port);
        }
        var tmp = Files.createTempFile(dir, null, null);
        try (var channel = FileChannel.open(tmp, StandardOpenOption.WRITE)) {
            var src = ByteBuffer.allocate(Short.BYTES);
            src.asShortBuffer().put((short) port);
            while (src.hasRemaining()) {
                channel.write(src);
            }
            channel.force(false);
        }
        Files.move(tmp, dir.resolve(PORT_TXT), StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * Starts watching specified directory and returns a port number when it's
     * written on a file of predefined name.
     *
     * @param dir the directory to watch.
     * @return a port number read.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while polling.
     * @see #writePortNumber(Path, int)
     * @see #readPortNumber(Path, IntConsumer)
     */
    static int readPortNumber(Path dir)
            throws IOException, InterruptedException {
        if (!Files.isDirectory(Objects.requireNonNull(dir, "dir is null"))) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        try (var service = FileSystems.getDefault().newWatchService()) {
            dir.register(service,
                         new WatchEvent.Kind[] {
                                 StandardWatchEventKinds.ENTRY_CREATE},
                         SensitivityWatchEventModifier.HIGH);
            while (!Thread.currentThread().isInterrupted()) {
                var key = service.poll(1L, TimeUnit.SECONDS);
                if (key == null) {
                    continue;
                }
                assert key.isValid();
                boolean created = false;
                for (var event : key.pollEvents()) {
                    assert event.kind() == StandardWatchEventKinds.ENTRY_CREATE;
                    Path context = (Path) event.context();
                    if (PORT_TXT.equals(context.getFileName().toString())) {
                        created = true;
                        break;
                    }
                }
                if (created) {
                    break;
                }
            } // end-of-while
            var path = dir.resolve(PORT_TXT);
            assert Files.isRegularFile(path);
            try (var channel = FileChannel.open(
                    path, StandardOpenOption.READ)) {
                var buffer = ByteBuffer.allocate(Short.BYTES);
                while (buffer.hasRemaining()) {
                    if (channel.read(buffer) == -1) {
                        throw new EOFException("unexpected eof");
                    }
                }
                buffer.flip();
                return buffer.asShortBuffer().get() & 0xFFFF;
            }
        } // end-of-try-with-resources; WatchService
    }

    /**
     * Reads a port number from a file of predefined name written in specified
     * directory and accepts the port number to specified consumer.
     *
     * @param dir      the directory from which a port number is read.
     * @param consumer the consumer accepts the port number.
     * @see #readPortNumber(Path)
     */
    static void readPortNumber(Path dir, IntConsumer consumer) {
        try {
            var port = readPortNumber(dir);
            consumer.accept(port);
        } catch (IOException ioe) {
            log.error("failed to read port number", ioe);
        } catch (InterruptedException ie) {
            log.error("interrupted while reading port number", ie);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Starts a new daemon thread which reads a port number from a file of
     * predefined name written in specified directory and accepts the port
     * number to specified consumer.
     *
     * @param dir      the directory from which a port number is read.
     * @param consumer the consumer accepts the port number.
     * @return a new started thread.
     * @see #readPortNumber(Path)
     */
    static Thread startReadingPortNumber(Path dir, IntConsumer consumer) {
        var thread = new Thread(() -> {
            readPortNumber(dir, consumer);
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    /**
     * The name of the file to which a port number is written.
     */
    private static final String ENDPOINT_TXT = "endpoint.txt";

//    /**
//     * Writes specified port number to a file newly created in specified
//     * directory.
//     *
//     * @param dir      the directory in which a new file is created.
//     * @param endpoint the socket address to write.
//     * @return a path to the file whose content is given {@code port} written in
//     * big-endian byte order; the path's size is {@value
//     * java.lang.Short#BYTES}.
//     * @throws IOException if an I/O error occurs.
//     * @see #readPortNumber(Path, long, TimeUnit)
//     */
//    static Path writeSocketAddress(final Path dir, final SocketAddress endpoint)
//            throws IOException {
//        if (!Files.isDirectory(Objects.requireNonNull(dir, "dir is null"))) {
//            throw new IllegalArgumentException("not a directory: " + dir);
//        }
//        Objects.requireNonNull(endpoint, "endpoint is null");
//        if (port <= 0) {
//            throw new IllegalArgumentException(
//                    "port(" + port + ") is not positive");
//        }
//        if (port > 65535) {
//            throw new IllegalArgumentException("port(" + port + ") > 65535");
//        }
//        final var tmp = Files.createTempFile(dir, null, null);
//        try (var channel = FileChannel.open(tmp, StandardOpenOption.WRITE)) {
//            final var src = ByteBuffer.allocate(Short.BYTES);
//            src.asShortBuffer().put((short) port);
//            while (src.hasRemaining()) {
//                channel.write(src);
//            }
//            channel.force(false);
//        }
//        return Files.move(tmp, dir.resolve(PORT_TXT));
//    }
//
//    /**
//     * Starts watching specified directory and returns a port number when it's
//     * written on a predefined file.
//     *
//     * @param dir     the directory to watch.
//     * @param timeout a polling timeout.
//     * @param unit    a polling time unit.
//     * @return a port number read.
//     * @throws IOException          if an I/O error occurs.
//     * @throws InterruptedException if interrupted while polling.
//     * @see #writePortNumber(Path, int)
//     */
//    static int readPortNumber(final Path dir, final long timeout,
//                              final TimeUnit unit)
//            throws IOException, InterruptedException {
//        if (!Files.isDirectory(Objects.requireNonNull(dir, "dir is null"))) {
//            throw new IllegalArgumentException("not a directory: " + dir);
//        }
//        try (var service = FileSystems.getDefault().newWatchService()) {
//            dir.register(service, StandardWatchEventKinds.ENTRY_CREATE);
//            for (boolean flag = false; !flag; ) {
//                final var key = service.poll(timeout, unit);
//                if (key == null) {
//                    continue;
//                }
//                assert key.isValid();
//                for (var event : key.pollEvents()) {
//                    assert event.kind() == StandardWatchEventKinds.ENTRY_CREATE;
//                    final Path file = (Path) event.context();
//                    if (PORT_TXT.equals(file.getFileName().toString())) {
//                        flag = true;
//                    }
//                }
//            } // end-of-while
//        }
//        final var file = dir.resolve(PORT_TXT);
//        assert Files.isRegularFile(file);
//        try (var channel = FileChannel.open(file, StandardOpenOption.READ)) {
//            final var dst = ByteBuffer.allocate(Short.BYTES);
//            while (dst.hasRemaining()) {
//                if (channel.read(dst) == -1) {
//                    throw new EOFException("unexpected eof");
//                }
//            }
//            dst.flip();
//            return dst.asShortBuffer().get() & 0xFFFF;
//        }
//    }

    /**
     * Creates a new instance which is impossible.
     */
    private IHelloWorldServerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
