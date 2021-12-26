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
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.AsynchronousChannelGroup.withCachedThreadPool;
import static java.util.ServiceLoader.load;
import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * A class whose {@link #main(String[])} method accepts socket connections and sends {@code hello,
 * world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldMain extends AbstractHelloWorldMain {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The main method of this program which accepts socket connections and sends {@code hello,
     * world} to clients.
     *
     * @param args an array of command line arguments
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while awaiting termination
     */
    public static void main(final String... args) throws IOException, InterruptedException {
        final HelloWorld helloWorld = load(HelloWorld.class).iterator().next();
        log.info("localhost: {}", InetAddress.getLocalHost());
        final AsynchronousChannelGroup group = withCachedThreadPool(newCachedThreadPool(), 10);
        final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        server.bind(null);
        log.info("bound to {}", server.getLocalAddress());
        readAndClose(server); // reads "quit" from System.in and closes the server.
        connectAndPrintAsynchronous2(
                server); // connects to the server and prints received hello-world-bytes.
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(final AsynchronousSocketChannel client, final Object attachment) {
                server.accept(null, this);
                final ByteBuffer buffer = allocate(BYTES);
                helloWorld.put(buffer);
                buffer.flip();
                client.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(final Integer written, final ByteBuffer attachment) {
                        if (!attachment.hasRemaining()) {
                            try {
                                client.close();
                            } catch (final IOException ioe) {
                                log.error("failed to close client", ioe);
                            }
                            return;
                        }
                        client.write(buffer, buffer, this);
                    }

                    @Override
                    public void failed(final Throwable exc, final ByteBuffer attachment) {
                        log.error("failed to write", exc);
                    }
                });
            }

            @Override
            public void failed(final Throwable exc, final Object attachment) {
                if (!server.isOpen()) {
                    return;
                }
                log.error("failed to accept", exc);
            }
        });
        final boolean terminated = group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private HelloWorldMain() {
        super();
    }
}
