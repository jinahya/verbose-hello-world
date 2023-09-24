package com.github.jinahya.hello.misc.c01rfc863.real;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import com.github.jinahya.hello.util.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Semaphore;

@Slf4j
class Rfc863TcpNonBlockingClient {

    public static void main(final String... args) throws Exception {
        final var carrier = Stopwatch.startStopwatch();
        final var semaphore = new Semaphore(_Rfc863Constants.CLIENT_THREADS);
        try (var selector = Selector.open()) {
            final Thread thread = new Thread(() -> {
                for (int i = 0; i < _Rfc863Constants.CLIENT_COUNT; ) {
                    if (!semaphore.tryAcquire()) {
                        continue;
                    }
                    SocketChannel client = null;
                    try {
                        client = SocketChannel.open();
                        client.configureBlocking(false);
                        if (client.connect(_Rfc863Constants.ADDR)) {
                            final var clientKey = client.register(selector,
                                                                  SelectionKey.OP_WRITE);
                            clientKey.attach(
                                    new Rfc863TcpNonBlockingClientAttachment(clientKey,
                                                                             semaphore)
                            );
                        } else {
                            final var clientKey = client.register(
                                    selector, SelectionKey.OP_CONNECT
                            );
                        }
                        selector.wakeup();
                        i++;
                    } catch (final IOException e) {
                        e.printStackTrace();
                        if (client != null) {
                            try {
                                client.close();
                            } catch (IOException ioe2) {
                                ioe2.printStackTrace();
                            }
                        }
                    }
                }
                log.debug("all open");
            });
            thread.start();
            while (thread.isAlive() || selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc863Constants.SELECT_TIMEOUT) == 0) {
                    continue;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    if (selectedKey.isConnectable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        if (channel.finishConnect()) {
                            selectedKey.attach(
                                    new Rfc863TcpNonBlockingClientAttachment(selectedKey, semaphore)
                            );
                            selectedKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                            continue;
                        }
                    }
                    if (selectedKey.isWritable()) {
                        final var attachment =
                                (Rfc863TcpNonBlockingClientAttachment) selectedKey.attachment();
                        final var w = attachment.write();
                        assert w >= 0;
                    }
                }
            }
            thread.join();
            log.debug("????????????????");
            log.debug("????????????????");
            log.debug("????????????????");
            log.debug("????????????????");
            log.debug("????????????????");
        }
        log.debug("duration: {}", Stopwatch.stopStopwatch(carrier));
    }

    private Rfc863TcpNonBlockingClient() {
        throw new AssertionError("instantiation is not allowed");
    }
}
