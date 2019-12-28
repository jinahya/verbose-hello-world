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
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.ServiceLoader.load;
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
     * @throws InterruptedException when interrupted while awaiting an executor.
     */
    public static void main(final String[] args) throws IOException, InterruptedException {
        final HelloWorld helloWorld = load(HelloWorld.class).iterator().next();
        log.info("localhost: {}", InetAddress.getLocalHost());
        final ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(true);
        server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
        log.info("bound to {}", server.socket().getLocalSocketAddress());
        readAndClose(server);
        connectAndPrintBlocking(server);
        final ExecutorService executorService = newCachedThreadPool();
        while (!server.socket().isClosed()) {
            try {
                final SocketChannel client = server.accept();
                assert client.isBlocking();
                executorService.submit(() -> {
                    try {
                        try (SocketChannel c = client) {
                            // TODO: Implement!
                        }
                    } catch (final IOException ioe) {
                        log.error("failed to send", ioe);
                    }
                });
            } catch (final IOException ioe) {
                if (server.socket().isClosed()) {
                    break;
                }
                log.debug("failed to work", ioe);
            }
        }
        executorService.shutdown();
        executorService.awaitTermination(10L, TimeUnit.SECONDS);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private HelloWorldMain() {
        super();
    }
}
