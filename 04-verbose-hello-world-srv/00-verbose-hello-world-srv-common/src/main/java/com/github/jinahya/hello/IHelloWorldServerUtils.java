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

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import static java.lang.Integer.parseInt;
import static java.lang.System.in;
import static java.lang.System.setIn;
import static java.lang.Thread.currentThread;
import static java.net.InetAddress.getByName;
import static java.net.InetAddress.getLocalHost;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Objects.requireNonNull;
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
            final String[] args,
            final IntFunction<
                    ? extends Function<
                            ? super InetAddress,
                            ? extends R>> function)
            throws UnknownHostException {
        requireNonNull(args, "args is null");
        requireNonNull(function, "function is null");
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
            final String[] args,
            final BiFunction<
                    ? super Integer, ? super InetAddress, ? extends R> function)
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
    static SocketAddress parseEndpoint(final String... args)
            throws UnknownHostException {
        return parseEndpoint(args, (p, h) -> new InetSocketAddress(h, p));
    }

    /**
     * Returns a (started) thread which reads "{@code quit\n}" from {@link
     * System#in}.
     */
    static Thread readQuit() {
        final var thread = new Thread(() -> {
            final var reader = new BufferedReader(new InputStreamReader(in));
            try {
                for (String l; (l = reader.readLine()) != null; ) {
                    if (l.trim().equalsIgnoreCase("quit")) {
                        log.debug("read 'quit'. breaking out...");
                        break;
                    }
                }
            } catch (final IOException ioe) {
                log.error("failed to read 'quit'", ioe);
            }
        });
        thread.start();
        log.debug("thread for reading 'quit' started");
        return thread;
    }

    /**
     * Starts a new thread which reads "{@code quit\n}" from {@link System#in}
     * and {@link Closeable#close() closes} specified closeable.
     *
     * @param closeable the closeable to close.
     */
    static void readQuitAndClose(final Closeable closeable) {
        if (closeable == null) {
            throw new NullPointerException("closeable is null");
        }
        new Thread(() ->{
            final var thread = readQuit();
            try {
                thread.join();
            } catch (final InterruptedException ie) {
                log.warn("interrupted while joining the 'quit'-reading thread");
            }
            log.debug("closing {}", closeable);
            try {
                closeable.close();
            } catch (final IOException ioe) {
                log.error("failed to close {}", closeable, ioe);
            }
        }).start();
    }

    /**
     * {@link Callable#call() Calls} callable and writes "{@code quit\n}" to a
     * pipe connected to {@link System#in}.
     *
     * @param callable the callable to call.
     * @throws IOException if an I/O error occurs.
     */
    static void callAndWriteQuit(final Callable<Void> callable)
            throws IOException {
        requireNonNull(callable, "callable is null");
        final var in_ = in;
        try (var pos = new PipedOutputStream();
             var pis = new PipedInputStream(pos)) {
            setIn(pis);
            try {
                callable.call();
            } catch (final Exception e) {
                log.error("failed to call {}", callable, e);
            }
            log.debug("writing 'quit'...");
            pos.write("quit\n".getBytes(US_ASCII));
            pos.flush();
        } finally {
            setIn(in_);
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
    static void shutdownAndAwaitTermination(final ExecutorService executor,
                                            final long timeout,
                                            final TimeUnit unit) {
        requireNonNull(executor, "executor is null");
        if (timeout <= 0L) {
            throw new IllegalArgumentException(
                    "timeout(" + timeout + ") is not positive");
        }
        requireNonNull(unit, "unit is null");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeout, unit)) {
                log.warn("executor has not terminated in specified time!");
            }
        } catch (final InterruptedException ie) {
            log.error("interrupted while awaiting executor terminated", ie);
            currentThread().interrupt();
        }
    }

    /**
     * Creates a new instance which is impossible.
     */
    private IHelloWorldServerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
