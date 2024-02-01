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
import java.util.concurrent.ArrayBlockingQueue;

@Slf4j
class ChatTcp2Client extends ChatTcp {

    // @formatter:off
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Attachment {
        private final ChatMessage.OfBuffer  writing = new ChatMessage.OfBuffer();
        private final ChatMessage.OfBuffer reading = new ChatMessage.OfBuffer();
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
            // -------------------------------------- read-quit!/count-down-latch|offer-to-the-queue
            final var lines = new ArrayBlockingQueue<String>(8);
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
                final var selectedKeys = selector.selectedKeys();
                assert selectedKeys.size() == 1;
                final var key = selectedKeys.iterator().next();
                assert key == clientKey;
                selectedKeys.clear();
                // ----------------------------------------------------------------- connect(finish)
                if (key.isConnectable()) {
                    final var channel = (SocketChannel) key.channel();
                    try {
                        if (channel.finishConnect()) {
                            key.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            assert key.isConnectable();
                            key.attach(new Attachment());
                            key.interestOpsOr(SelectionKey.OP_READ);
                            assert !key.isReadable();
                        }
                    } catch (final IOException ioe) {
                        channel.close();
                        assert !key.isValid();
                        continue; // why?
                    }
                }
                // ---------------------------------------------------------------------------- read
                if (key.isReadable()) {
                    final var channel = (SocketChannel) key.channel();
                    assert channel == client;
                    final var attachment = (Attachment) key.attachment();
                    try {
                        if (attachment.reading.read(channel) == -1) {
                            log.error("premature eof");
                            channel.close();
                            assert !key.isValid();
                            continue;
                        }
                        if (!attachment.reading.hasRemaining() &&
                            attachment.reading.limit() > ChatMessage.INDEX_MESSAGE_CONTENT) {
                            attachment.reading.print().readyToRead();
                        }
                    } catch (final IOException ioe) {
                        if (key.isValid()) {
                            log.error("failed to read", ioe);
                        }
                    }
                }
                // --------------------------------------------------------------------------- write
                if (key.isWritable()) {
                    final var channel = (SocketChannel) key.channel();
                    assert channel == client;
                    final var attachment = (Attachment) key.attachment();
                    if (!attachment.writing.hasRemaining()) {
                        assert !lines.isEmpty();
                        attachment.writing
                                .message(ChatMessage.prependUserName(lines.take()))
                                .readyToWrite();
                    }
                    assert attachment.writing.hasRemaining();
                    final var w = attachment.writing.write(channel);
                    assert w >= 0;
                    if (!attachment.writing.hasRemaining()) {
                        if (lines.isEmpty()) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                        }
                    }
                }
            }
        }
    }
}
