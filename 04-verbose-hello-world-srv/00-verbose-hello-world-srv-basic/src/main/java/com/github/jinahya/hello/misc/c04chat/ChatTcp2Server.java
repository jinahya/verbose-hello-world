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

import com.github.jinahya.hello.util.JavaIoCloseableUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

@Slf4j
class ChatTcp2Server extends ChatTcp {

    // @formatter:off
    static class Attachment {
        private _ChatMessage.OfBuffer reading;
        final List<_ChatMessage.OfBuffer> writings = new LinkedList<>();
         _ChatMessage.OfBuffer writing;
    }
    // @formatter:on

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR);
            // ----------------------------------------------------- configure-non-blocking/register
            final var serverKey = server.configureBlocking(false).register(
                    selector,
                    SelectionKey.OP_ACCEPT
            );
            // -------------------------------------------------------- read-quit!/cancel-server-key
            JavaLangUtils.readLinesAndRunWhenTests(
                    QUIT::equalsIgnoreCase,
                    () -> {
                        serverKey.cancel();
                        assert !serverKey.isValid();
                        selector.wakeup();
                    }
            );
            // ----------------------------------------------------------------------- selector-loop
            while (serverKey.isValid()) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    continue;
                }
                // ------------------------------------------------------------------------- process
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var key = i.next();
                    // ---------------------------------------------------------------------- accept
                    if (key.isAcceptable()) {
                        ((ServerSocketChannel) key.channel())
                                .accept()
                                .configureBlocking(false)
                                .register(selector, SelectionKey.OP_READ, new Attachment());
                    }
                    // ---------------------------------------------------------------------- reader
                    if (key.isReadable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var attachment = (Attachment) key.attachment();
                        if (attachment.reading == null) {
                            attachment.reading = new _ChatMessage.OfBuffer().readyToReadFromClient();
                        }
                        try {
                            if (attachment.reading.read(channel) == -1) {
                                JavaIoCloseableUtils.closeSilently(channel);
                                continue;
                            }
                        } catch (final IOException ioe) {
                            log.error("failed to read from {}", channel, ioe);
                        }
                        if (!attachment.reading.hasRemaining()) {
                            selector.keys().stream()
                                    .filter(k -> k.channel() instanceof SocketChannel)
                                    .filter(SelectionKey::isValid)
                                    .forEach(k -> {
                                        ((Attachment) k.attachment()).writings.add(
                                                _ChatMessage.OfBuffer
                                                        .copyOf(attachment.reading)
                                                        .readyToWriteToClient()
                                        );
                                        k.interestOpsOr(SelectionKey.OP_WRITE);
                                    });
                            attachment.reading.readyToReadFromClient();
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (key.isWritable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var attachment = (Attachment) key.attachment();
                        assert !attachment.writings.isEmpty();
                        if (attachment.writing == null) {
                            attachment.writing = attachment.writings.removeFirst();
                        }
                        try {
                            attachment.writing.write(channel);
                            if (!attachment.writing.hasRemaining()) {
                                attachment.writing = null;
                                if (attachment.writings.isEmpty()) {
                                    key.interestOpsAnd(~SelectionKey.OP_WRITE);
                                }
                            }
                        } catch (final IOException ioe) {
                            log.error("failed to write to {}", channel, ioe);
                            JavaIoCloseableUtils.closeSilently(channel);
                            key.cancel();
                        }
                    }
                }
            }
            // ------------------------------------------------------------------- close-all-clients
            selector.keys().stream()
                    .map(SelectionKey::channel)
                    .forEach(JavaIoCloseableUtils::closeSilently);
        }
    }
}
