package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv4
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

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.ByteBuffer.allocate;
import static java.util.ServiceLoader.load;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * A class whose {@link #main(String[])} method accepts socket connections and sends {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldMain extends AbstractHelloWorldMain {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The main method of this program which accepts socket connections and sends {@code hello, world} to clients.
     *
     * @param args an array of command line arguments
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while getting result.
     * @throws ExecutionException   if failed to to execute.
     */
    public static void main(final String... args) throws IOException, InterruptedException, ExecutionException {
        final HelloWorld helloWorld = load(HelloWorld.class).iterator().next();
        log.info("localhost: {}", InetAddress.getLocalHost());
        final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        server.bind(null);
        log.info("bound to {}", server.getLocalAddress());
        readAndClose(server); // reads "quit" from System.in and closes the server.
        final CompletableFuture<Void> printer = connectAndPrintAsynchronous(server);
        final ExecutorService executor = newCachedThreadPool();
        while (server.isOpen()) {
            final AsynchronousSocketChannel client = server.accept().get();
            final Runnable runnable = () -> {
                final ByteBuffer buffer = allocate(BYTES);
                helloWorld.put(buffer);
                buffer.flip();
                while (buffer.hasRemaining()) {
                    try {
                        final int written = client.write(buffer).get();
                    } catch (InterruptedException | ExecutionException e) {
                        log.error("failed to send", e);
                    }
                }
                try {
                    client.close();
                } catch (final IOException ioe) {
                    log.error("failed to close: " + client, ioe);
                }
            };
            final CompletableFuture<Void> writer = runAsync(runnable, executor);
        }
        executor.shutdown();
        executor.awaitTermination(10L, TimeUnit.SECONDS);
        final Void printed = printer.get();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private HelloWorldMain() {
        super();
    }
}
