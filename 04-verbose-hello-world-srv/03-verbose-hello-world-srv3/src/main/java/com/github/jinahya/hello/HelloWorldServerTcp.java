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
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerTcp extends AbstractHelloWorldServer {

    @Override
    protected void openInternal(SocketAddress endpoint, Path dir) throws IOException {
        server = ServerSocketChannel.open();
        if (endpoint instanceof InetSocketAddress && ((InetSocketAddress) endpoint).getPort() > 0) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
        }
        try {
            server.bind(endpoint);
        } catch (IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        log.info("server bound to {}", server.getLocalAddress());
        if (dir != null) {
            HelloWorldServerUtils.writePortNumber(dir, server.socket().getLocalPort());
        }
        var thread = new Thread(() -> {
            var executor = Executors.newCachedThreadPool();
            var service = new ExecutorCompletionService<Void>(executor);
            var monitor = HelloWorldServerUtils.startMonitoringCompletedTasks(service);
            while (!Thread.currentThread().isInterrupted()) {
                SocketChannel client;
                try {
                    client = server.accept();
                } catch (IOException ioe) {
                    if (!server.isOpen()) {
                        break;
                    }
                    log.error("failed to accept", ioe);
                    continue;
                }
                var future = service.submit(() -> {
                    var address = client.getRemoteAddress();
                    log.debug("[S] accepted from {}", address);
                    var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    service().put(buffer);
                    if (buffer.position() > 0) { // TODO: peel off!
                        buffer.flip();
                    }
                    while (buffer.hasRemaining()) {
                        client.write(buffer);
                    }
                    log.debug("[S] written to {}", address);
                    return null;
                });
            } // end-of-while
            HelloWorldServerUtils.shutdownAndAwaitTermination(executor);
            monitor.interrupt();
        });
        thread.start();
        log.debug("server thread started");
    }

    @Override
    protected void closeInternal() throws IOException {
        if (server != null && server.isOpen()) {
            server.close();
            log.debug("[S] server closed");
        }
    }

    private ServerSocketChannel server;
}
