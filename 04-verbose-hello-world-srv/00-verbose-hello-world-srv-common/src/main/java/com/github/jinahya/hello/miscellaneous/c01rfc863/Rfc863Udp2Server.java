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

import com.github.jinahya.hello.util.HelloWorldSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
class Rfc863Udp2Server {

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var server = DatagramChannel.open()) {
            server.bind(_Rfc863Constants.ADDRESS);
            log.info("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            var serverKey = server.register(selector, SelectionKey.OP_READ);
            if (selector.select(TimeUnit.SECONDS.toMillis(16L)) == 0) {
                return;
            }
            var key = selector.selectedKeys().iterator().next();
            assert key == serverKey;
            assert key.isReadable();
            var channel = (DatagramChannel) key.channel();
            assert channel == server;
            var buffer = ByteBuffer.allocate(channel.getOption(StandardSocketOptions.SO_RCVBUF));
            var source = channel.receive(buffer);
            assert source != null;
            _Rfc863Utils.logServerBytes(buffer.position());
            var digest = _Rfc863Utils.newDigest();
            digest.update(buffer.flip());
            _Rfc863Utils.logDigest(digest);
            key.cancel();
            assert !key.isValid();
        }
    }

    private Rfc863Udp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
