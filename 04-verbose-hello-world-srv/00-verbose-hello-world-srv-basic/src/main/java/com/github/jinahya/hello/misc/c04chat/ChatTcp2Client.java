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
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
class ChatTcp2Client extends ChatTcp {

    public static void main(final String... args) throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (final ArrayIndexOutOfBoundsException aioobe) {
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
                        SelectionKey.OP_READ
                );
            } else {
                clientKey = client.register(
                        selector,
                        SelectionKey.OP_CONNECT
                );
            }
            // ----------------------------------------------------------------------------- prepare
            final _ChatMessage.OfBuffer reading
                    = new _ChatMessage.OfBuffer().readyToReadFromServer();
            final _ChatMessage.OfBuffer writing = new _ChatMessage.OfBuffer();
            final var lines = new LinkedBlockingQueue<String>(8);
            // ----------------------------------- read-quit!/count-down-latch-or-offer-to-the-queue
            JavaLangUtils.readLinesAndRunWhenTests(
                    QUIT::equalsIgnoreCase,
                    () -> {
                        clientKey.cancel();
                        assert !clientKey.isValid();
                        selector.wakeup();
                    },
                    l -> {
                        if (l.isBlank()) {
                            return;
                        }
                        if (lines.offer(l)) {
                            clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                            selector.wakeup();
                        }
                    }
            );
            // ----------------------------------------------------------------------- selector-loop
            while (clientKey.isValid()) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    continue;
                }
                assert selector.selectedKeys().size() == 1;
                assert selector.selectedKeys().contains(clientKey);
                selector.selectedKeys().clear();
                // ----------------------------------------------------------------- connect(finish)
                if (clientKey.isConnectable() && (client.finishConnect())) {
                    clientKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                    clientKey.interestOpsOr(SelectionKey.OP_READ);
                }
                // ---------------------------------------------------------------------------- read
                if (clientKey.isReadable()) {
                    if ((reading.read(client)) == -1) {
                        log.error("premature eof");
                        clientKey.interestOpsAnd(~SelectionKey.OP_READ);
                        continue;
                    }
                    if (!reading.hasRemaining()) {
                        reading.print().readyToReadFromServer();
                    }
                }
                // --------------------------------------------------------------------------- write
                if (clientKey.isWritable()) {
                    if (!writing.hasRemaining()) {
                        assert !lines.isEmpty(); // why?
                        writing.timestamp(Instant.now())
                                .message(_ChatMessage.prependUserName(lines.take()))
                                .readyToWriteToServer();
                    }
                    assert writing.hasRemaining();
                    writing.write(client);
                    if (!writing.hasRemaining()) {
                        clientKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                    }
                    if (!lines.isEmpty()) {
                        clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }
}
