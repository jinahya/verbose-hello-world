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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
class Rfc863Udp2Server {

    static final int CAPACITY = 1024;

    public static void main(String... args) throws IOException {
        try (var selector = Selector.open();
             var server = DatagramChannel.open()) {
            server.bind(_Rfc863Constants.ENDPOINT);
            log.debug("[S] bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(CAPACITY));
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isReadable()) {
                        var channel = (DatagramChannel) key.channel();
                        var buffer = (ByteBuffer) key.attachment();
                        var source = channel.receive(buffer);
                        log.debug("[S] {} byte(s) received from {}", buffer.position(), source);
                        channel.close();
                        key.cancel();
                        var digest = _Rfc863Utils.newMessageDigest();
                        buffer.flip();
                        digest.update(buffer);
                        log.debug("[S] digest: {}", HexFormat.of().formatHex(digest.digest()));
                    }
                }
            }
        }
    }

    private Rfc863Udp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
