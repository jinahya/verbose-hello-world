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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Udp2Client {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var client = DatagramChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            final var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                client.connect(_Rfc862Constants.ADDR);
                log.info("(optionally) connected to {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
            }
            client.configureBlocking(false);
            var clientKey = client.register(selector, SelectionKey.OP_WRITE);
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.ACCEPT_TIMEOUT_IN_MILLIS) == 0) {
                    break;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    assert selectedKey == clientKey;
                    if (selectedKey.isWritable()) {
                        final var channel = (DatagramChannel) selectedKey.channel();
                        assert channel == client;
                        final var attachment = ByteBuffer.allocate(
                                ThreadLocalRandom.current().nextInt(
                                        channel.getOption(StandardSocketOptions.SO_SNDBUF) + 1
                                )
                        );
                        selectedKey.attach(attachment);
                        ThreadLocalRandom.current().nextBytes(attachment.array());
                        _Rfc862Utils.logClientBytes(attachment.remaining());
                        final var w = channel.send(attachment, _Rfc862Constants.ADDR);
                        assert w == attachment.position();
                        assert !attachment.hasRemaining();
                        _Rfc862Utils.logDigest(attachment.flip());
                        selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        selectedKey.interestOpsOr(SelectionKey.OP_READ);
                    }
                    if (selectedKey.isReadable()) {
                        final var channel = (DatagramChannel) selectedKey.channel();
                        assert channel == client;
                        final var attachment = (ByteBuffer) selectedKey.attachment();
                        attachment.clear();
                        final var address = channel.receive(attachment);
                        log.debug("{} byte(s) received from {}", attachment.position(), address);
                        assert !attachment.hasRemaining();
                        selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                        selectedKey.cancel();
                        assert !selectedKey.isValid();
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
