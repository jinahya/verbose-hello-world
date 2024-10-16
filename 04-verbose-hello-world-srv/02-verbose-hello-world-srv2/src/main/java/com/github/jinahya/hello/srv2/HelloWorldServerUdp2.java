package com.github.jinahya.hello.srv2;

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

import com.github.jinahya.hello.AbstractHelloWorldServer;
import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.util.HelloWorldServerUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.concurrent.Executors;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerUdp2 extends AbstractHelloWorldServer {

    @Override
    protected void openInternal(SocketAddress endpoint, Path dir) throws IOException {
        socket = new DatagramSocket(null);
        if (endpoint instanceof InetSocketAddress
            && ((InetSocketAddress) endpoint).getPort() > 0) {
            socket.setReuseAddress(true);
        }
        try {
            socket.bind(endpoint);
            log.info("[S] server bound to {}", socket.getLocalSocketAddress());
        } catch (IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        if (dir != null) {
            HelloWorldServerUtils.writePortNumber(dir, socket.getLocalPort());
        }
        new Thread(() -> {
            var executor = Executors.newCachedThreadPool();
            while (!Thread.currentThread().isInterrupted()) {
                var received = new DatagramPacket(new byte[1], 1);
                try {
                    socket.receive(received);
                    var future = executor.submit(() -> {
                        var address = received.getSocketAddress();
                        log.debug("[S] received from {}", address);
                        var array = new byte[HelloWorld.BYTES];
                        service().set(array);
                        var sending = new DatagramPacket(array, array.length,
                                                         address);
                        try {
                            socket.send(sending);
                            log.debug("[S] sent to {}", address);
                        } catch (IOException ioe) {
                            if (!socket.isClosed()) {
                                log.error("failed to send", ioe);
                            }
                        }
                    });
                } catch (IOException ioe) {
                    if (socket.isClosed()) {
                        break;
                    }
                    log.error("failed to receive", ioe);
                }
            } // end-of-while
            HelloWorldServerUtils.shutdownAndAwaitTermination(executor);
        }).start();
        log.debug("[S] server thread started");
    }

    @Override
    protected void closeInternal() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            log.debug("server closed");
        }
    }

    private DatagramSocket socket;
}
