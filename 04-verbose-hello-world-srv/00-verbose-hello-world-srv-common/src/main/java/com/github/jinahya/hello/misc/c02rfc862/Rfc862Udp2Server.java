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

import com.github.jinahya.hello.util.HelloWorldSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Udp2Server {

    // @formatter:off
    static class Attachment {
        ByteBuffer buffer;
        SocketAddress address;
        final MessageDigest digest = _Rfc862Utils.newDigest();
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var server = DatagramChannel.open()) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_READ);
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isReadable()) {
                        var channel = (DatagramChannel) key.channel();
                        var attachment = new Attachment();
                        attachment.buffer = ByteBuffer.allocate(
                                channel.getOption(StandardSocketOptions.SO_RCVBUF)
                        );
                        key.attach(attachment);
                        attachment.address = channel.receive(attachment.buffer);
                        log.info("{} byte(s) received from {}", attachment.buffer.position(),
                                 attachment.address);
                        key.interestOpsAnd(~SelectionKey.OP_READ);
                        key.interestOpsOr(SelectionKey.OP_WRITE);
                    }
                    if (key.isWritable()) {
                        var channel = (DatagramChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        attachment.buffer.flip();
                        var w = channel.send(attachment.buffer, attachment.address);
                        log.info("{} byte(s) sent to {}", w, attachment.address);
                        HelloWorldSecurityUtils.updateAllPreceding(
                                attachment.digest, attachment.buffer
                        );
                        _Rfc862Utils.logDigest(attachment.digest);
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
