package com.github.jinahya.hello.miscellaneous.c02rfc862;

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

import com.github.jinahya.hello.util.HelloWorldSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Udp2Client {

    private static class Attachment
            extends Rfc862Udp2Server.Attachment {

    }

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var client = DatagramChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.debug("(optionally) bound to {}", client.getLocalAddress());
            }
            var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                try {
                    client.connect(_Rfc862Constants.ADDRESS);
                    log.debug("(optionally) connected to {}, through {}", client.getRemoteAddress(),
                              client.getLocalAddress());
                } catch (SocketException se) {
                    log.warn("failed to connect", se);
                    connect = false;
                }
            }
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_WRITE);
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isWritable()) {
                        var channel = (DatagramChannel) key.channel();
                        var attachment = new Attachment();
                        key.attach(attachment);
                        attachment.buffer = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(
                                channel.getOption(StandardSocketOptions.SO_SNDBUF)
                        ));
                        ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                        var w = channel.send(attachment.buffer, _Rfc862Constants.ADDRESS);
                        log.debug("{} byte(s) sent to {}", w, _Rfc862Constants.ADDRESS);
                        assert w == attachment.buffer.position();
                        assert !attachment.buffer.hasRemaining();
                        HelloWorldSecurityUtils.updatePreceding(
                                attachment.digest, attachment.buffer
                        );
                        _Rfc862Utils.logDigest(attachment.digest);
                        key.interestOpsAnd(~SelectionKey.OP_WRITE);
                        key.interestOpsOr(SelectionKey.OP_READ);
                    }
                    if (key.isReadable()) {
                        var channel = (DatagramChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        attachment.buffer.flip();
                        attachment.address = channel.receive(attachment.buffer);
                        log.debug("{} byte(s) received from {}", attachment.buffer.position(),
                                  attachment.address);
                        assert !attachment.buffer.hasRemaining();
                        key.interestOpsAnd(~SelectionKey.OP_READ);
                        key.cancel();
                    }
                }
            }
        }
    }

    private Rfc862Udp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
