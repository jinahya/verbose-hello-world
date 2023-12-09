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

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp2Server {

    // @formatter:off
    static class ChatTcp2ServerAttachment {
        final ByteBuffer buffer = _ChatMessage.OfBuffer.empty();
        final List<ByteBuffer> buffers = new LinkedList<>();
    }
    // @formatter:on

    public static void main(String... args)
            throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(InetAddress.getByName("::"),
                                              _ChatConstants.PORT));
            log.debug("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            JavaLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        serverKey.cancel();
                        assert !serverKey.isValid();
                        selector.wakeup();
                        return null;
                    },
                    l -> {                         // <consumer>
                        // does nothing
                    }
            );
            while (serverKey.isValid()) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    continue;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext();
                     i.remove()) {
                    var key = i.next();
                    if (key.isAcceptable()) {
                        var channel = (ServerSocketChannel) key.channel();
                        assert channel == server;
                        var client = channel.accept(); // IOException
                        log.debug("accepted from {} through {}",
                                  client.getRemoteAddress(),
                                  client.getLocalAddress());
                        var attachment = new ChatTcp2ServerAttachment();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ,
                                        attachment);
                        continue;
                    }
                    if (key.isReadable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (ChatTcp2ServerAttachment) key.attachment();
                        var r = channel.read(attachment.buffer);
                        if (r == -1) {
                            channel.close();
                            assert !key.isValid();
                            continue;
                        }
                        assert r > 0;
                        if (!attachment.buffer.hasRemaining()) {
                            selector.keys().stream()
                                    .filter(k -> k.channel() instanceof SocketChannel)
                                    .filter(SelectionKey::isValid)
                                    .forEach(k -> {
                                        ((ChatTcp2ServerAttachment) k.attachment()).buffers.add(
                                                ByteBuffer.wrap(
                                                        attachment.buffer.array())
                                        );
                                        k.interestOpsOr(SelectionKey.OP_WRITE);
                                    });
                            attachment.buffer.clear();
                        }
                    }
                    if (key.isWritable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (ChatTcp2ServerAttachment) key.attachment();
                        assert !attachment.buffers.isEmpty();
                        var buffer = attachment.buffers.get(0);
                        assert buffer.hasRemaining();
                        var w = channel.write(buffer);
                        assert w > 0;
                        if (!buffer.hasRemaining()) {
                            attachment.buffers.remove(buffer);
                            if (attachment.buffers.isEmpty()) {
                                key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            }
                        }
                    }
                }
            }
            selector.keys().stream()
                    .filter(k -> k.channel() instanceof SocketChannel)
                    .filter(SelectionKey::isValid)
                    .forEach(k -> {
                        var channel = k.channel();
                        try {
                            channel.close();
                            assert !k.isValid();
                        } catch (IOException ioe) {
                            log.error("failed to close {}", channel, ioe);
                        }
                    });
        }
    }
}
