package com.github.jinahya.hello.misc.c01rfc863;

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

import com.github.jinahya.hello.util.HelloWorldNetUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.misc.c01rfc863._Rfc863Constants.HOST;

@Slf4j
class Rfc863Tcp2Client {

    // @formatter:off
    static class Attachment extends Rfc863Tcp2Server.Attachment {
        Attachment() {
            super();
            bytes = _Rfc863Utils.newBytesLessThanMillion();
            _Rfc863Utils.logClientBytes(bytes);
            buffer.position(buffer.limit());
        }
//        /**
//         * Clears the {@code buffer}, randomizes the {@code buffer}'s
//         * {@link ByteBuffer#array() backing array}, and sets the {@code buffer}'s {@code limit} with
//         * smaller of {@code buffer.limit()} and {@link #bytes}.
//         */
//        @Override
//        void clearBuffer() {
//            super.clearBuffer();
//            ThreadLocalRandom.current().nextBytes(buffer.array());
//            buffer.clear().limit(Math.min(buffer.limit(), bytes));
//        }
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            HelloWorldNetUtils.printSocketOptions(SocketChannel.class, client);
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            client.configureBlocking(false);
            SelectionKey clientKey;
            if (client.connect(_Rfc863Constants.ADDR)) {
                log.info("connected (immediately) to {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
                var attachment = new Attachment();
                clientKey = client.register(selector, 0, attachment);
                if (attachment.bytes > 0) {
                    clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                } else {
                    clientKey.cancel();
                    assert !clientKey.isValid();
                }
            } else {
                clientKey = client.register(selector, SelectionKey.OP_CONNECT);
            }
            while (clientKey.isValid()) {
                if (selector.select((int) _Rfc863Constants.CONNECT_TIMEOUT_IN_MILLIS) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isConnectable()) {
                        var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        var connected = channel.finishConnect();
                        assert connected;
                        log.info("connected to {}, through {}", channel.getRemoteAddress(),
                                 channel.getLocalAddress());
                        key.interestOpsAnd(~SelectionKey.OP_CONNECT);
                        var attachment = new Attachment();
                        key.attach(attachment);
                        if (attachment.bytes > 0) {
                            key.interestOps(SelectionKey.OP_WRITE);
                        } else {
                            key.cancel();
                            assert !key.isValid();
                        }
                        continue;
                    }
                    if (key.isWritable()) {
                        var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        var attachment = (Attachment) key.attachment();
                        assert attachment.bytes > 0;
                        if (!attachment.buffer.hasRemaining()) {
                            ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                            attachment.buffer.clear().limit(Math.min(
                                    attachment.buffer.remaining(), attachment.bytes
                            ));
                        }
                        assert attachment.buffer.hasRemaining();
                        var w = channel.write(attachment.buffer);
                        assert w >= 0;
                        attachment.digest.update(
                                attachment.slice
                                        .position(attachment.buffer.position() - w)
                                        .limit(attachment.buffer.position())
                        );
                        if ((attachment.bytes -= w) == 0) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            key.cancel();
                            assert !key.isValid();
                            _Rfc863Utils.logDigest(attachment.digest);
                        }
                    }
                }
            }
        }
    }

    private Rfc863Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
