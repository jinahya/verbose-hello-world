package com.github.jinahya.hello.miscellaneous.c01rfc863;

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
import com.github.jinahya.hello.util.HelloWorldSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863Tcp2Server {

    static class Attachment {

        final ByteBuffer buffer = _Rfc863Utils.newBuffer();

        int bytes;

        final MessageDigest digest = _Rfc863Utils.newDigest();
    }

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            HelloWorldNetUtils.printSocketOptions(server);
            server.bind(_Rfc863Constants.ADDRESS, 1);
            log.debug("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    continue;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isAcceptable()) {
                        var channel = (ServerSocketChannel) key.channel();
                        var client = channel.accept();
                        log.debug("accepted from {}, through {}", client.getRemoteAddress(),
                                  client.getLocalAddress());
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ, new Attachment());
                        key.cancel();
                        assert !key.isValid();
                        continue;
                    }
                    if (key.isReadable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        if (!attachment.buffer.hasRemaining()) {
                            attachment.buffer.clear();
                        }
                        var r = channel.read(attachment.buffer);
                        if (r == -1) {
                            _Rfc863Utils.logServerBytes(attachment.bytes);
                            _Rfc863Utils.logDigest(attachment.digest);
                            channel.close();
                            assert !key.isValid();
                            continue;
                        }
                        attachment.bytes += r;
                        HelloWorldSecurityUtils.updatePreceding(
                                attachment.digest,
                                attachment.buffer,
                                r
                        );
                    }
                }
            }
        }
    }

    private Rfc863Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
