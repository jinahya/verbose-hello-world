package com.github.jinahya.hello.misc.c03calc;

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

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp3Server {

    private static void close(final SelectionKey key) {
        final var channel = key.channel();
        try {
            channel.close();
            assert !key.isValid();
        } catch (final IOException ioe) {
            log.debug("failed to close {}", channel, ioe);
            key.cancel();
        }
        assert !key.isValid();
    }

    private static void sub(final Selector selector) {
        final var executor = Executors.newFixedThreadPool(_CalcConstants.SERVER_THREADS << 1);
        while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
            try {
                if (selector.select(_CalcConstants.SELECT_TIMEOUT_MILLIS) == 0) {
                    continue;
                }
            } catch (final IOException ioe) {
                log.error("failed to select", ioe);
                continue;
            }
            for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                final var selectedKey = i.next();
                i.remove();
                if (selectedKey.isAcceptable()) {
                    final var channel = (ServerSocketChannel) selectedKey.channel();
                    final SocketChannel client;
                    try {
                        client = channel.accept();
                    } catch (final IOException ioe) {
                        log.debug("failed to accept", ioe);
                        continue;
                    }
                    try {
                        client.configureBlocking(false).register(
                                selector,
                                SelectionKey.OP_READ,
                                _CalcMessage.newInstanceForServer()
                        );
                    } catch (final IOException ioe) {
                        log.error("failed to configure/register", ioe);
                        close(selectedKey);
                    }
                }
                if (selectedKey.isReadable()) {
                    final var channel = (SocketChannel) selectedKey.channel();
                    final var attachment = (_CalcMessage) selectedKey.attachment();
                    try {
                        if (!attachment.read(channel).hasRemaining()) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                            attachment.apply().readyToSendResult();
                            selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                        }
                    } catch (final IOException ioe) {
                        log.error("failed to read", ioe);
                        close(selectedKey);
                        continue;
                    }
                }
                if (selectedKey.isWritable()) {
                    final var channel = (SocketChannel) selectedKey.channel();
                    final var attachment = (_CalcMessage) selectedKey.attachment();
                    try {
                        if (!attachment.write(channel).hasRemaining()) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                            close(selectedKey);
                        }
                    } catch (final IOException ioe) {
                        log.error("failed to write", ioe);
                        close(selectedKey);
                        continue;
                    }
                }
            }
        }
        executor.shutdown();
        try {
            final boolean terminated = executor.awaitTermination(10L, TimeUnit.SECONDS);
            assert terminated : "executor hasn't been terminated";
        } catch (final InterruptedException ie) {
            log.error("interrupted while awaiting executor to be terminated", ie);
            Thread.currentThread().interrupt();
        }
    }

    public static void main(final String... args)
            throws IOException {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            // -------------------------------------------------------------------------------- bind
            server.bind(_CalcConstants.ADDR, _CalcConstants.SERVER_BACKLOG);
            _TcpUtils.logBound(server);
            // ---------------------------------------------------------------------------- register
            server.configureBlocking(false);
            final var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            // ------------------------------------------------------------ start daemon for "quit!"
            JavaLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        server.close();
                        assert !serverKey.isValid();
                        selector.wakeup();
                        return null;
                    }
            );
            // --------------------------------------------------------------------------------- sub
            sub(selector);
        }
    }
}
