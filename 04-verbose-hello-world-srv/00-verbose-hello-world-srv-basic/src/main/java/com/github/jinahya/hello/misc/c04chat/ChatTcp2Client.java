package com.github.jinahya.hello.misc.c04chat;

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

import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
class ChatTcp2Client extends ChatTcp {

    // @formatter:off
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Attachment {
        private final ChatMessage.OfBuffer  writing = new ChatMessage.OfBuffer();
        private final ChatMessage.OfBuffer reading =
                new ChatMessage.OfBuffer().readyToReadFromServer();
    }
    // @formatter:on

    public static void main(final String... args) throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            addr = InetAddress.getLoopbackAddress();
        }
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            // -------------------------------------------------------------- configure-non-blocking
            client.configureBlocking(false);
            // ------------------------------------------------------------------------ connect(try)
            SelectionKey clientKey;
            if (client.connect(new InetSocketAddress(addr, PORT))) {
                clientKey = client.register(
                        selector,
                        SelectionKey.OP_READ,
                        new Attachment()
                );
            } else {
                clientKey = client.register(
                        selector,
                        SelectionKey.OP_CONNECT
                );
            }
            // ----------------------------------------------------------------------------- prepare
            final var lines = new LinkedBlockingQueue<String>();
            // -------------------------------------- read-quit!/count-down-latch|offer-to-the-queue
            JavaLangUtils.readLinesAndRunWhenTests(
                    "quit!"::equalsIgnoreCase,
                    () -> {
                        clientKey.cancel();
                        assert !clientKey.isValid();
                        selector.wakeup();
                    },
                    l -> {
                        if (l.isBlank()) {
                            return;
                        }
                        final var offered = lines.offer(l);
                        if (!offered) {
                            log.error("failed to offer!");
                        }
                        clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                        selector.wakeup();
                    }
            );
            // ----------------------------------------------------------------------- selector-loop
            while (clientKey.isValid()) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    continue;
                }
                selector.selectedKeys().clear();
                // ----------------------------------------------------------------- connect(finish)
                if (clientKey.isConnectable()) {
                    final var channel = (SocketChannel) clientKey.channel();
                    try {
                        if (channel.finishConnect()) {
                            clientKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            assert clientKey.isConnectable();
                            clientKey.attach(new Attachment());
                            clientKey.interestOpsOr(SelectionKey.OP_READ);
                        }
                    } catch (final IOException ioe) {
                        log.error("failed to connect", ioe);
                        channel.close();
                        assert !clientKey.isValid();
                        continue; // why?
                    }
                }
                // ---------------------------------------------------------------------------- read
                if (clientKey.isReadable()) {
                    final var channel = (SocketChannel) clientKey.channel();
                    assert channel == client;
                    final var attachment = (Attachment) clientKey.attachment();
                    try {
                        if (attachment.reading.read(channel) == -1) {
                            log.error("premature eof");
                            clientKey.interestOpsAnd(~SelectionKey.OP_READ);
                            assert clientKey.isReadable();
                            continue;
                        }
                        if (!attachment.reading.hasRemaining() &&
                            attachment.reading.limit() > ChatMessage.INDEX_MESSAGE_CONTENT) {
                            attachment.reading.print().readyToReadFromClient();
                        }
                    } catch (final IOException ioe) {
                        if (clientKey.isValid()) {
                            log.error("failed to read", ioe);
                        }
                    }
                }
                // --------------------------------------------------------------------------- write
                if (clientKey.isWritable()) {
                    final var channel = (SocketChannel) clientKey.channel();
                    assert channel == client;
                    final var attachment = (Attachment) clientKey.attachment();
                    if (!attachment.writing.hasRemaining()) {
                        assert !lines.isEmpty();
                        attachment.writing
                                .message(ChatMessage.prependUserName(lines.take()))
                                .readyToWriteToServer();
                    }
                    assert attachment.writing.hasRemaining();
                    try {
                        final var w = attachment.writing.write(channel);
                        assert w >= 0;
                        if (!attachment.writing.hasRemaining() && (lines.isEmpty())) {
                            clientKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        }
                    } catch (final IOException ioe) {
                        log.error("failed to write", ioe);
                        clientKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        assert clientKey.isWritable();
                    }
                }
            }
        }
    }
}
