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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A utility class for Hello World servers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class IHelloWorldServerUtils {

    /**
     * Parses specified command line arguments and returns a socket address to
     * bind.
     *
     * @param args the command line arguments.
     * @return a socket address to bind.
     * @throws UnknownHostException if {@code args[1]} is not known as an
     *                              address.
     */
    static SocketAddress parseSocketAddress(final String[] args)
            throws UnknownHostException {
        Objects.requireNonNull(args, "args is null");
        int port = 0;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        InetAddress addr = InetAddress.getLoopbackAddress();
        if (args.length > 1) {
            addr = InetAddress.getByName(args[1]);
        }
        return new InetSocketAddress(addr, port);
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
    static <R> R applySocketAddress(
            final String[] args,
            final Function<
                    ? super Integer,
                    ? extends Function<
                            ? super InetAddress,
                            ? extends R>> function)
            throws UnknownHostException {
        Objects.requireNonNull(args, "args is null");
        Objects.requireNonNull(function, "function is null");
        int port = 0;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        InetAddress addr = InetAddress.getLoopbackAddress();
        if (args.length > 1) {
            addr = InetAddress.getByName(args[1]);
        }
        return function.apply(port).apply(addr);
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
    static <R> R applySocketAddress(
            final String[] args,
            final BiFunction<
                    ? super Integer,
                    ? super InetAddress, ? extends R> function)
            throws UnknownHostException {
        return applySocketAddress(args, p -> a -> function.apply(p, a));
    }

    /**
     * Starts a new thread which reads "{@code quit\n}" from {@link System#in}
     * and {@link Closeable#close() closes} specified closeable.
     *
     * @param closeable the closeable to close.
     */
    static void readQuitAndClose(final Closeable closeable) {
        if (closeable == null) {
            throw new NullPointerException("server is null");
        }
        final Thread thread = new Thread(() -> {
            final BufferedReader reader
                    = new BufferedReader(new InputStreamReader(System.in));
            try {
                for (String line; (line = reader.readLine()) != null; ) {
                    if (line.trim().equalsIgnoreCase("quit")) {
                        log.debug("read 'quit'. breaking out...");
                        break;
                    }
                }
            } catch (final IOException ioe) {
                log.error("failed to read 'quit'", ioe);
            }
            log.debug("closing {}", closeable);
            try {
                closeable.close();
            } catch (final IOException ioe) {
                log.error("failed to close {}", closeable, ioe);
            }
        });
        thread.start();
        log.debug("thread for reading 'quit' started");
    }

    /**
     * Invokes {@link Callable#call()} method on specified callable and writes
     * "{@code quit\n}" to a pipe connected to {@link System#in}.
     *
     * @param callable the callable to call.
     * @throws InterruptedException when interrupted while executing {@code
     *                              callable}.
     * @throws IOException          if an I/O error occurs.
     */
    static void writeQuitToClose(final Callable<Void> callable)
            throws InterruptedException, IOException {
        Objects.requireNonNull(callable, "callable is null");
        final Thread thread = new Thread(() -> {
            try {
                callable.call();
            } catch (final Exception e) {
                log.debug("failed to call {}", callable, e);
            }
        });
        thread.start();
        thread.join();
        try (PipedOutputStream pos = new PipedOutputStream();
             PipedInputStream pis = new PipedInputStream(pos)) {
            final InputStream in = System.in;
            try {
                System.setIn(pis);
                log.debug("writing 'quit'...");
                pos.write("quit\n".getBytes(StandardCharsets.US_ASCII));
                pos.flush();
            } finally {
                System.setIn(in);
            }
        }
    }

    /**
     * Loads and returns an instance of {@link HelloWorld} interface.
     *
     * @return an instance of {@link HelloWorld} interface.
     */
    static HelloWorld loadHelloWorld() {
        return ServiceLoader.load(HelloWorld.class).iterator().next();
    }

    /**
     * Creates a new instance which is impossible.
     */
    private IHelloWorldServerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
