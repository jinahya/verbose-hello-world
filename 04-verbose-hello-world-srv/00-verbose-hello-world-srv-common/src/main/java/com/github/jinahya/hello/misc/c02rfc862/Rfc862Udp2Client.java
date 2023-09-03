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

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Udp2Client {

    // @formatter:off
    static class Attachment extends Rfc862Udp2Server.Attachment {
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var client = DatagramChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                try {
                    client.connect(_Rfc862Constants.ADDR);
                    log.info("(optionally) connected to {}, through {}",
                             client.getRemoteAddress(), client.getLocalAddress());
                } catch (SocketException se) {
                    log.warn("failed to connect", se);
                    connect = false;
                }
            }
            client.configureBlocking(false);
            var clientKey = client.register(selector, SelectionKey.OP_WRITE);
            while (clientKey.isValid()) {
                if (selector.select(_Rfc862Constants.ACCEPT_TIMEOUT_IN_MILLIS) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    assert key == clientKey;
                    if (key.isWritable()) {
                        var channel = (DatagramChannel) key.channel();
                        assert channel == client;
                        var attachment = new Attachment();
                        attachment.buffer = ByteBuffer.allocate(
                                ThreadLocalRandom.current().nextInt(
                                        channel.getOption(StandardSocketOptions.SO_SNDBUF) + 1
                                )
                        );
                        attachment.address = _Rfc862Constants.ADDR;
                        key.attach(attachment);
                        ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                        _Rfc862Utils.logClientBytes(attachment.buffer.remaining());
                        var w = channel.send(attachment.buffer, attachment.address);
                        assert w == attachment.buffer.position();
                        assert !attachment.buffer.hasRemaining();
                        _Rfc862Utils.logDigest(attachment.buffer.flip());
                        key.interestOpsAnd(~SelectionKey.OP_WRITE);
                        key.interestOpsOr(SelectionKey.OP_READ);
                    }
                    if (key.isReadable()) {
                        var channel = (DatagramChannel) key.channel();
                        assert channel == client;
                        var attachment = (Attachment) key.attachment();
                        attachment.buffer.clear();
                        attachment.address = channel.receive(attachment.buffer);
                        log.info("{} byte(s) received from {}", attachment.buffer.position(),
                                 attachment.address);
                        assert !attachment.buffer.hasRemaining();
                        key.interestOpsAnd(~SelectionKey.OP_READ);
                        key.cancel();
                        assert !key.isValid();
                    }
                }
            }
            if (connect) {
                client.disconnect();
            }
        }
    }

    private Rfc862Udp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
