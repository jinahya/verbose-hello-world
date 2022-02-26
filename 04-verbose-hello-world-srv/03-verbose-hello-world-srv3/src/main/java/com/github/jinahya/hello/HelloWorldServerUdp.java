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
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Executors;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerUdp extends AbstractHelloWorldServer {

    private void handle(Set<SelectionKey> keys) throws IOException {
        for (var key : keys) {
            var channel = (DatagramChannel) key.channel();
            if (key.isReadable()) {
                var address = channel.receive(ByteBuffer.allocate(0));
                if (address != null) {
                    log.debug("received from {}", address);
                    key.interestOps(key.interestOps() & ~OP_READ);
                    key.interestOps(key.interestOps() | OP_WRITE);
                    key.attach(address);
                }
                continue;
            }
            if (key.isWritable()) {
                var src = ByteBuffer.allocate(HelloWorld.BYTES);
                service().put(src);
                // TODO: flip the src!
                final var target = (SocketAddress) key.attachment();
                if (channel.send(src, target) == src.capacity()) {
                    key.interestOps(key.interestOps() & ~OP_WRITE);
                    key.interestOps(key.interestOps() | OP_READ);
                }
                continue;
            }
            log.warn("unhandled key: {}", key);
        }
        keys.clear();
    }

    @Override
    protected void openInternal(SocketAddress endpoint, Path dir) throws IOException {
        server = DatagramChannel.open();
        if (endpoint instanceof InetSocketAddress && ((InetSocketAddress) endpoint).getPort() > 0) {
            server.socket().setReuseAddress(true);
        }
        try {
            server.bind(endpoint);
        } catch (IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        log.info("server bound to {}", server.getLocalAddress());
        if (dir != null) {
            var port = server.socket().getLocalPort();
            IHelloWorldServerUtils.writePortNumber(dir, port);
        }
        var thread = new Thread(() -> {
            var executor = Executors.newCachedThreadPool();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    var address = server.receive(ByteBuffer.allocate(0));
                    executor.submit(() -> {
                        log.debug("[S] received from {}", address);
                        var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                        service().put(buffer);
                        if (buffer.position() > 0) { // // TODO: peel off!
                            buffer.flip();
                        }
                        var sent = server.send(buffer, address);
                        assert sent == buffer.capacity();
                        log.debug("[S] sent to {}", address);
                        return null;
                    });
                } catch (IOException ioe) {
                    if (!server.isOpen()) {
                        break;
                    }
                    log.error("failed to receive", ioe);
                }
            } // end-of-while
            IHelloWorldServerUtils.shutdownAndAwaitTermination(executor);
        });
        thread.start();
        log.debug("[S] server thread started");
    }

    @Override
    protected void closeInternal() throws IOException {
        if (server == null || !server.isOpen()) {
            return;
        }
        server.close();
    }

    private DatagramChannel server;
}
