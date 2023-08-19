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
class Rfc863Udp2Client {

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var client = DatagramChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc863Constants.ADDR, 0));
                log.debug("client bound to {}", client.getLocalAddress());
            }
            var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                try {
                    client.connect(_Rfc863Constants.ADDRESS);
                    log.debug("connected to {}, through {}", client.getRemoteAddress(),
                              client.getLocalAddress());
                } catch (SocketException se) {
                    log.warn("failed to connect to {}", _Rfc863Constants.ADDRESS, se);
                    connect = false;
                }
            }
            client.configureBlocking(false);
            var clientKey = client.register(selector, SelectionKey.OP_WRITE);
            if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                return;
            }
            var selectedKey = selector.selectedKeys().iterator().next();
            assert selectedKey == clientKey;
            assert selectedKey.isWritable();
            var channel = (DatagramChannel) selectedKey.channel();
            assert channel == client;
            var capacity = ThreadLocalRandom.current().nextInt(
                    channel.getOption(StandardSocketOptions.SO_SNDBUF) + 1
            );
            var buffer = ByteBuffer.allocate(capacity);
            ThreadLocalRandom.current().nextBytes(buffer.array());
            _Rfc863Utils.logClientBytes(buffer.remaining());
            var digest = _Rfc863Utils.newDigest();
            var w = channel.send(buffer, _Rfc863Constants.ADDRESS);
            assert w == buffer.position();
            assert !buffer.hasRemaining();
            HelloWorldSecurityUtils.updatePreceding(digest, buffer);
            _Rfc863Utils.logDigest(digest);
            selectedKey.cancel();
            if (connect) {
                client.disconnect(); // UncheckedIOException
            }
        }
    }

    private Rfc863Udp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
