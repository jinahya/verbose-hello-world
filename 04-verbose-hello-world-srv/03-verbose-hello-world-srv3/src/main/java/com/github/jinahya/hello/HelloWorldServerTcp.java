package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv1
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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.file.Path;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.IHelloWorldServerUtils.shutdownAndAwaitTermination;
import static com.github.jinahya.hello.IHelloWorldServerUtils.writePortNumber;
import static java.lang.Boolean.TRUE;
import static java.net.StandardSocketOptions.SO_REUSEADDR;
import static java.nio.ByteBuffer.allocate;
import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerTcp
        extends AbstractHelloWorldServer {

    @Override
    protected void openInternal(SocketAddress endpoint, Path dir)
            throws IOException {
        server = ServerSocketChannel.open();
        if (endpoint instanceof InetSocketAddress
            && ((InetSocketAddress) endpoint).getPort() > 0) {
            server.setOption(SO_REUSEADDR, TRUE);
        }
        try {
            server.bind(endpoint);
        } catch (IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        log.info("server bound to {}", server.getLocalAddress());
        if (dir != null) {
            writePortNumber(dir, server.socket().getLocalPort());
        }
        var thread = new Thread(() -> {
            var executor = newCachedThreadPool();
            while (server.isOpen()) {
                try {
                    var client = server.accept();
                    executor.submit(() -> {
                        log.debug("[S] accepted from {}",
                                  client.getRemoteAddress());
                        var buffer = allocate(BYTES);
                        service().put(buffer);
                        if (buffer.position() > 0) { // TODO: peel off!
                            buffer.flip();
                        }
                        while (buffer.hasRemaining()) {
                            client.write(buffer);
                        }
                        return null;
                    });
                } catch (IOException ioe) {
                    if (!server.isOpen()) {
                        break;
                    }
                    log.error("failed to accept", ioe);
                }
            } // end-of-while
            shutdownAndAwaitTermination(executor);
        });
        thread.start();
        log.debug("server thread started");
    }

    @Override
    protected void closeInternal() throws IOException {
        if (server != null && !server.isOpen()) {
            server.close();
        }
    }

    private ServerSocketChannel server;
}
