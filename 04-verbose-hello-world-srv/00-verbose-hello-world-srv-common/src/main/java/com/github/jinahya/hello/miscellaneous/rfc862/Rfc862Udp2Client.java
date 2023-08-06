package com.github.jinahya.hello.miscellaneous.rfc862;

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
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Udp2Client {

    private static final int PACKET_LENGTH =
            ThreadLocalRandom.current().nextInt(Rfc862Udp2Server.MAX_PACKET_LENGTH);

    private static class Attachment extends Rfc862Udp2Server.Attachment {

        Attachment() {
            super();
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.limit(ThreadLocalRandom.current().nextInt(buffer.limit()));
            address = _Rfc862Constants.ENDPOINT;
        }
    }

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var client = DatagramChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.debug("[C] bound to {}", client.getLocalAddress());
            }
            var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                client.connect(_Rfc862Constants.ENDPOINT);
                log.debug("[C] connected to {}, through {}", client.getRemoteAddress(),
                          client.getLocalAddress());
            }
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_WRITE, new Attachment());
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isWritable()) {
                        var channel = (DatagramChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        var sent = channel.send(attachment.buffer, attachment.address);
                        log.debug("[S] {} byte(s) sent to {}, through {}", sent, attachment.address,
                                  channel.getLocalAddress());
                        assert !attachment.buffer.hasRemaining();
                        key.interestOps(SelectionKey.OP_READ);
                    }
                    if (key.isReadable()) {
                        var channel = (DatagramChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        attachment.buffer.flip();
                        attachment.address = channel.receive(attachment.buffer);
                        log.debug("[C] {} byte(s) received from {}", attachment.buffer.position(),
                                  attachment.address);
                        assert !attachment.buffer.hasRemaining();
                        key.cancel();
                        HelloWorldSecurityUtils.updatePreceding(
                                attachment.digest, attachment.buffer, attachment.buffer.position()
                        );
                        log.debug("[S] digest: {}",
                                  Base64.getEncoder().encodeToString(attachment.digest.digest())
                        );
                    }
                }
            }
        }
    }

    private Rfc862Udp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
