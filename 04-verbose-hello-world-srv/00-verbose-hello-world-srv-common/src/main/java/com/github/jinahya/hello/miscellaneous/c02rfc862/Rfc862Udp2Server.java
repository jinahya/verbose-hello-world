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

import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Udp2Server {

    static final int MAX_PACKET_LENGTH = 1024;

    static class Attachment {

        final ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKET_LENGTH);

        SocketAddress address;

        final MessageDigest digest = _Rfc862Utils.newMessageDigest();
    }

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var server = DatagramChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(_Rfc862Constants.ENDPOINT);
            log.debug("[S] bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_READ, new Attachment());
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isReadable()) {
                        var channel = (DatagramChannel) key.channel();
                        var attachment = new Attachment();
                        key.attach(attachment);
                        attachment.address = channel.receive(attachment.buffer);
                        log.debug("[S] {} byte(s) received from {}", attachment.buffer.position(),
                                  attachment.address);
                        key.interestOps(SelectionKey.OP_WRITE);
                        HelloWorldSecurityUtils.updatePreceding(
                                attachment.digest, attachment.buffer, attachment.buffer.position()
                        );
                        log.debug("[S] digest: {}",
                                  Base64.getEncoder().encodeToString(attachment.digest.digest())
                        );
                    }
                    if (key.isWritable()) {
                        var channel = (DatagramChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        assert attachment != null;
                        assert attachment.buffer != null;
                        assert attachment.address != null;
                        attachment.buffer.flip();
                        var sent = channel.send(attachment.buffer, attachment.address);
                        assert !attachment.buffer.hasRemaining();
                        log.debug("[S] {} byte(s) sent back to {}", sent, attachment.address);
                        key.cancel();
                    }
                }
            }
        }
    }

    private Rfc862Udp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
