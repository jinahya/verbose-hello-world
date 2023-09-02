package com.github.jinahya.hello.misc.c02rfc862;

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

import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp2Client {

    // @formatter:off
    private static class Attachment extends Rfc862Tcp2Server.Attachment {
        Attachment() {
            super();
            bytes = _Rfc862Utils.randomBytesLessThanOneMillion();
            buffer.position(buffer.limit());
            _Rfc862Utils.logClientBytes(bytes);
            log.info("buffer.capacity: {}", buffer.capacity());
        }
        private final ByteBuffer slice = buffer.slice();
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            client.configureBlocking(false);
            if (client.connect(_Rfc862Constants.ADDR)) {
                log.info("connected, immediately, to {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
                var attachment = new Attachment();
                var clientKey = client.register(
                        selector,
                        SelectionKey.OP_WRITE | SelectionKey.OP_READ,
                        attachment
                );
                if (attachment.bytes == 0) {
                    log.warn("bytes == 0; canceling...");
                    clientKey.cancel();
                    assert !clientKey.isValid();
                }
            } else {
                client.register(selector, SelectionKey.OP_CONNECT);
            }
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(16L)) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isConnectable()) {
                        var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        var connected = channel.finishConnect();
                        assert connected;
                        log.info("connected to {}, through {}",
                                 channel.getRemoteAddress(),
                                 channel.getLocalAddress());
                        key.interestOpsAnd(~SelectionKey.OP_CONNECT);
                        assert key.attachment() == null;
                        var attachment = new Attachment();
                        key.attach(attachment);
                        key.interestOpsOr(
                                SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                        if (attachment.bytes == 0) {
                            log.warn("zero bytes to send; canceling...");
                            key.cancel();
                        }
                        continue;
                    }
                    if (key.isWritable()) {
                        var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        var attachment = (Attachment) key.attachment();
                        assert attachment != null;
                        assert attachment.bytes > 0;
                        if (!attachment.buffer.hasRemaining()) {
                            ThreadLocalRandom.current()
                                    .nextBytes(attachment.buffer.array());
                            attachment.buffer.clear().limit(Math.min(
                                    attachment.buffer.remaining(),
                                    attachment.bytes
                            ));
                        }
                        var w = channel.write(attachment.buffer);
                        assert w >= 0;
                        attachment.digest.update(
                                attachment.slice
                                        .position(attachment.buffer.position() - w)
                                        .limit(attachment.buffer.limit())
                        );
                        if ((attachment.bytes -= w) == 0) {
                            channel.shutdownOutput();
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            _Rfc862Utils.logDigest(attachment.digest);
                        }
                    }
                    if (key.isReadable()) {
                        var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        var attachment = (Attachment) key.attachment();
                        assert attachment != null;
                        var r = channel.read(
                                attachment.slice
                                        .position(0)
                                        .limit(attachment.buffer.position())
                        );
                        if (r == -1) {
                            if (attachment.bytes > 0) {
                                throw new EOFException("unexpected eof");
                            }
                            key.interestOpsAnd(~SelectionKey.OP_READ);
                            key.cancel();
                            assert !key.isValid();
                        }
                    }
                }
            }
        }
    }

    private Rfc862Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
