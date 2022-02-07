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
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.System.in;
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
        requireNonNull(args, "args is null");
        var port = 0;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        var addr = getLocalHost();
        if (args.length > 1) {
            addr = getByName(args[1]);
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
        requireNonNull(function, "function is null");
        final var parsed = parseSocketAddress(args);
        final var port = ((InetSocketAddress) parsed).getPort();
        final var addr = ((InetSocketAddress) parsed).getAddress();
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
        new Thread(() -> {
            final var reader = new BufferedReader(new InputStreamReader(in));
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
        }).start();
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
        requireNonNull(callable, "callable is null");
        final var thread = new Thread(() -> {
            try {
                callable.call();
            } catch (final Exception e) {
                log.debug("failed to call {}", callable, e);
            }
        });
        thread.start();
        thread.join();
        try (var pos = new PipedOutputStream();
             var pis = new PipedInputStream(pos)) {
            final var in = System.in;
            try {
                System.setIn(pis);
                log.debug("writing 'quit'...");
                pos.write("quit\n".getBytes(US_ASCII));
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
     * @deprecated do not use this!
     */
    @Deprecated
    static HelloWorld loadHelloWorld() {
        return load(HelloWorld.class).iterator().next();
    }

//    static void clients(final int count,
//                        final Supplier<? extends IHelloWorldClient> supplier,
//                        final Consumer<? super String> consumer)
//            throws InterruptedException {
//        if (count <= 0) {
//            throw new IllegalArgumentException(
//                    "count(" + count + ") is not positive");
//        }
//        requireNonNull(supplier, "supplier is null");
//        requireNonNull(consumer, "consumer is null");
//        for (int i = 0; i < count; i++) {
//            final var client = supplier.get();
//            final byte[] bytes;
//            try {
//                bytes = client.call();
//            } catch (final Exception e) {
//                log.error("failed to call {}", client, e);
//            }
//            final var string = new String(bytes, US_ASCII);
//            consumer.accept(string);
//        }
//    }

    /**
     * Creates a new instance which is impossible.
     */
    private IHelloWorldServerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
