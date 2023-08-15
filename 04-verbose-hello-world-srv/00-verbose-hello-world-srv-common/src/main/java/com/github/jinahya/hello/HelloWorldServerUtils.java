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
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
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

import static java.lang.System.setIn;
import static java.util.ServiceLoader.load;

/**
 * A utility class for Hello World servers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldServerUtils {

    /**
     * Parses specified arguments and does currying to specified function.
     *
     * @param args     the arguments to parse.
     * @param function the function to apply.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @throws UnknownHostException if {@code args[1]} is not known as an address.
     */
    static <R> R parseEndpoint(
            String[] args,
            IntFunction<? extends Function<? super InetAddress, ? extends R>> function)
            throws UnknownHostException {
        Objects.requireNonNull(args, "args is null");
        Objects.requireNonNull(function, "function is null");
        var port = 0; // any available
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
            if (port < 0 || port > 65535) {
                throw new IllegalArgumentException("invalid port number parsed: " + port);
            }
        }
        var host = InetAddress.getByName("0.0.0.0"); // all network interfaces
        if (args.length > 1) {
            try {
                host = InetAddress.getByName(args[1]);
            } catch (UnknownHostException uhe) {
                log.error("failed to determine an address from {}", args[1]);
                throw uhe;
            }
        }
        return function.apply(port).apply(host);
    }

    /**
     * Parses specified arguments and applies a port and a host to specified function.
     *
     * @param args     the arguments to parse.
     * @param function the function to apply.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @throws UnknownHostException if {@code args[1]} is not known as an address.
     */
    static <R> R parseEndpoint(
            String[] args,
            BiFunction<? super Integer, ? super InetAddress, ? extends R> function)
            throws UnknownHostException {
        Objects.requireNonNull(args, "args is null");
        Objects.requireNonNull(function, "function is null");
        return parseEndpoint(args, p -> h -> function.apply(p, h));
    }

    /**
     * Parses specified arguments and returns a socket address.
     *
     * @param args the command line arguments.
     * @return a socket address to bind.
     * @throws UnknownHostException if {@code args[1]} is not known as an address.
     */
    public static SocketAddress parseEndpoint(String... args) throws UnknownHostException {
        Objects.requireNonNull(args, "args is null");
        return parseEndpoint(args, (p, h) -> new InetSocketAddress(h, p));
    }

    private static void readLineUntil(Reader source, Predicate<? super String> matcher)
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

//    private static void readLineUntil(InputStream source, Charset charset,
//                                      Predicate<? super String> matcher)
//            throws IOException {
//        readLineUntil(new InputStreamReader(source, charset), matcher);
//    }
//
//    private static void readLineUntil(InputStream source, Predicate<? super String> matcher)
//            throws IOException {
//        readLineUntil(source, Charset.defaultCharset(), matcher);
//    }

    /**
     * Keeps reading lines from {@link System#in} until it reads {@code 'quit'}.
     *
     * @throws IOException if an I/O error occurs.
     */
    // https://stackoverflow.com/q/49520625/330457
    // https://stackoverflow.com/q/6008177/330457
    static void readQuitFromStandardInput() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        for (String line; (line = reader.readLine()) != null; ) {
            if (line.strip().equalsIgnoreCase("quit")) {
                break;
            }
        }
    }

    /**
     * Starts a new <em>daemon</em> thread which keeps reading lines from {@link System#in} until it
     * reads {@code 'quit'}.
     *
     * @see #readQuitFromStandardInput()
     */
    static Thread startReadingQuitFromStandardInput() {
        var thread = new Thread(() -> {
            try {
                readQuitFromStandardInput();
            } catch (IOException ioe) {
                log.error("failed to read 'quit' from the standard input", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    /**
     * Starts a new thread which reads "{@code quit\n}" from {@link System#in} and
     * {@link Closeable#close() closes} specified closeable.
     *
     * @param closeable the closeable to close.
     * @return a new started thread.
     */
    static Thread startReadingQuitFromStandardInputAndClose(Closeable closeable) {
        if (closeable == null) {
            throw new NullPointerException("closeable is null");
        }
        var thread = new Thread(() -> {
            try {
                startReadingQuitFromStandardInput().join();
            } catch (InterruptedException ie) {
                log.error("interrupted while reading 'quit'", ie);
                Thread.currentThread().interrupt();
            }
            try {
                closeable.close();
            } catch (IOException ioe) {
                log.error("failed to close {}", closeable, ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    /**
     * Submits specified task into an executor and writes {@code "quit\n"} to a pipe synced to
     * {@link System#in}.
     *
     * @param task the task to submit.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while waiting the {@code task} to finish.
     * @throws ExecutionException   if failed to execute the {@code task}.
     */
    static <T> T submitAndWriteQuit(Callable<? extends T> task)
            throws IOException, InterruptedException, ExecutionException {
        Objects.requireNonNull(task, "task is null");
        var in = System.in;
        try (var pos = new PipedOutputStream();
             var pis = new PipedInputStream(pos)) {
            setIn(pis);
            var executor = Executors.newSingleThreadExecutor();
            try {
                var future = executor.submit(task);
                pos.write("quit\n".getBytes(StandardCharsets.US_ASCII));
                pos.flush();
                return future.get();
            } finally {
                HelloWorldServerUtils.shutdownAndAwaitTermination(executor);
            }
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
     * {@link ExecutorService#shutdown() Shuts down} specified executor service and awaits its
     * termination for specified amount of time.
     *
     * @param executor the executor service to shut down.
     * @param timeout  a timeout for awaiting termination.
     * @param unit     a time unit for awaiting termination.
     * @throws InterruptedException if interrupted while waiting.
     * @see ExecutorService#shutdown()
     * @see ExecutorService#awaitTermination(long, TimeUnit)
     */
    static void shutdownAndAwaitTermination(ExecutorService executor, long timeout, TimeUnit unit)
            throws InterruptedException {
        Objects.requireNonNull(executor, "executor is null");
        if (timeout <= 0L) {
            throw new IllegalArgumentException(
                    "timeout(" + timeout + ") is not positive");
        }
        Objects.requireNonNull(unit, "unit is null");
        executor.shutdown();
        if (!executor.awaitTermination(timeout, unit)) {
            log.warn("executor has not been terminated for {} {}: {}", timeout, unit, executor);
        }
    }

    /**
     * {@link ExecutorService#shutdown() Shuts down} specified executor service and awaits its
     * termination for 8 seconds.
     *
     * @param executor the executor service to shut down.
     * @see ExecutorService#shutdown()
     * @see ExecutorService#awaitTermination(long, TimeUnit)
     */
    static void shutdownAndAwaitTermination(ExecutorService executor) {
        try {
            shutdownAndAwaitTermination(executor, 8L, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            log.error("interrupted while waiting", ie);
        }
    }

    static void monitorCompletedTasks(CompletionService<?> service) throws InterruptedException {
        Objects.requireNonNull(service, "service is null");
        while (!Thread.currentThread().isInterrupted()) {
            var future = service.poll(1L, TimeUnit.SECONDS);
            if (future == null) {
                continue;
            }
            assert future.isDone();
            //assert !future.isCancelled(); // who knows?
            try {
                var result = future.get();
                log.debug("completed: {}, result: {}", future, result);
            } catch (CancellationException ce) {
                log.debug("canceled: {}", future, ce);
            } catch (ExecutionException ee) {
                log.debug("failed: {}", future, ee);
            }
        } // end-of-while
    }

    /**
     * Starts a new daemon thread polls completed tasks from specified service.
     *
     * @param service the service from which completed tasks are polled.
     * @return a new started thread.
     */
    static Thread startMonitoringCompletedTasks(CompletionService<?> service) {
        Objects.requireNonNull(service, "service is null");
        var thread = new Thread(() -> {
            try {
                monitorCompletedTasks(service);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    /**
     * The name of the file to which a port number is written.
     */
    private static final String PORT_TXT = "port.txt";

    /**
     * Writes specified port number to a file newly created in specified directory.
     *
     * @param dir  the directory in which a new file is created.
     * @param port the port number to be written.
     * @throws IOException if an I/O error occurs.
     * @see #readPortNumber(Path)
     * @see #readPortNumber(Path, IntConsumer)
     */
    static void writePortNumber(Path dir, int port) throws IOException {
        if (!Files.isDirectory(Objects.requireNonNull(dir, "dir is null"))) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("invalid port : " + port);
        }
        var tmp = Files.createTempFile(dir, null, null);
        try (var channel = FileChannel.open(tmp, StandardOpenOption.WRITE)) {
            var buffer = ByteBuffer.allocate(Short.BYTES);
            buffer.asShortBuffer().put((short) port);
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            channel.force(false);
        }
        var path = Files.move(tmp, dir.resolve(PORT_TXT), StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * Starts watching specified directory and returns a port number when it's written on a file of
     * predefined name.
     *
     * @param dir the directory to watch.
     * @return a port number read.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while polling.
     * @see #writePortNumber(Path, int)
     */
    static int readPortNumber(Path dir) throws IOException, InterruptedException {
        if (!Files.isDirectory(Objects.requireNonNull(dir, "dir is null"))) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        try (var service = FileSystems.getDefault().newWatchService()) {
            dir.register(service, new WatchEvent.Kind[] {StandardWatchEventKinds.ENTRY_CREATE},
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
            try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
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
     * Reads a port number from a file of predefined name written in specified directory and accepts
     * the port number to specified consumer.
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
     * Starts a thread which reads a port number from a file created in specified directory and
     * accept the port number to specified consumer.
     *
     * @param dir      the directory from which a port number is read.
     * @param consumer the consumer accepts the port number.
     * @see #readPortNumber(Path)
     */
    static Thread startReadingPortNumber(Path dir, IntConsumer consumer) {
        if (!Files.isDirectory(Objects.requireNonNull(dir, "dir is null"))) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        Objects.requireNonNull(consumer, "consumer is null");
        var thread = new Thread(() -> {
            try {
                var port = readPortNumber(dir);
                consumer.accept(port);
            } catch (IOException ioe) {
                log.error("failed to read port number in " + dir, ioe);
            } catch (InterruptedException ie) {
                log.error("interrupted while reading port number in " + dir, ie);
                Thread.currentThread().interrupt();
            }
        });
        thread.start();
        return thread;
    }

    /**
     * Causes the current thread to wait until specified latch has counted down to zero for
     * specified amount of time.
     *
     * @param latch   the latch to await.
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the {@code timeout} argument
     * @return {@code true} if the count reached zero and {@code false} if the waiting time elapsed
     * before the count reached zero
     * @throws InterruptedException if the current thread is interrupted while waiting
     * @see CountDownLatch#await(long, TimeUnit)
     */
    public static boolean await(CountDownLatch latch, long timeout, TimeUnit unit)
            throws InterruptedException {
        Objects.requireNonNull(latch, "latch is null");
        var reached = latch.await(timeout, unit);
        if (!reached) {
            log.error("latch hasn't reached zero for {} {}: {}", timeout, unit, latch);
        }
        return reached;
    }

    /**
     * Causes the current thread to wait until specified latch has counted down to zero for a
     * minute.
     *
     * @param latch the latch to await.
     * @return {@code true} if the count reached zero and {@code false} if the waiting time elapsed
     * before the count reached zero
     * @throws InterruptedException if the current thread is interrupted while waiting
     * @see #await(CountDownLatch, long, TimeUnit)
     */
    public static boolean await(CountDownLatch latch) throws InterruptedException {
        return await(latch, 1L, TimeUnit.MINUTES);
    }

    public static boolean isQuit(String string) {
        Objects.requireNonNull(string, "string is null");
        return string.strip().equalsIgnoreCase(HelloWorldServerConstants.QUIT);
    }

    public static boolean isKeep(String string) {
        Objects.requireNonNull(string, "string is null");
        return string.strip().equalsIgnoreCase(HelloWorldServerConstants.KEEP);
    }

    /**
     * Creates a new instance which is impossible.
     */
    private HelloWorldServerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
