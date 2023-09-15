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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.misc.c01rfc863._Rfc863Constants.HOST;

/**
 * .
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Rfc863Tcp2Server
 */
@Slf4j
class Rfc863Tcp2Client {

    // @formatter:on
    static class Attachment extends _Rfc863Attachment.Client {

        /**
         * Writes a sequence of random bytes to specified channel from {@link #buffer}.
         *
         * @param channel the channel to which bytes are written.
         * @return number of bytes written to the {@code channel}.
         * @throws IOException if an I/O error occurs.
         * @see Rfc863Tcp2Server.Attachment#readFrom(ReadableByteChannel)
         */
        int writeTo(final WritableByteChannel channel) throws IOException {
            Objects.requireNonNull(channel, "channel is null");
            if (!buffer.hasRemaining()) {
                ThreadLocalRandom.current().nextBytes(buffer.array());
                buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
            }
            final int w = channel.write(buffer);
            assert w >= 0;
            updateDigest(w);
            decreaseBytes(w);
            return w;
        }
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
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
                if (attachment.getBytes() > 0) {
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
                        if (attachment.getBytes() == 0) {
                            log.warn("no bytes to send");
                            key.cancel();
                            assert !key.isValid();
                            continue;
                        }
                        key.interestOps(SelectionKey.OP_WRITE);
                        continue;
                    }
                    if (key.isWritable()) {
                        var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        var attachment = (Attachment) key.attachment();
                        assert attachment.getBytes() > 0;
                        var w = attachment.writeTo(channel);
                        assert w > 0; // why?
                        if (attachment.getBytes() == 0) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            key.cancel();
                            assert !key.isValid();
                            attachment.logDigest();
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
