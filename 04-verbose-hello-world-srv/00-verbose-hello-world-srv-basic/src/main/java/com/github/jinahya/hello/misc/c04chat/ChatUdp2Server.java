package com.github.jinahya.hello.misc.c04chat;

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

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatUdp2Server extends ChatUdp {

    static final Duration DURATION_TO_KEEP_ADDRESSES = ChatUdp1Server.DURATION_TO_KEEP_ADDRESSES;

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var server = DatagramChannel.open()) {
            log.debug("[S]: SO_RCVBUF: {}", server.getOption(StandardSocketOptions.SO_RCVBUF));
            log.debug("[S]: SO_SNFBUD: {}", server.getOption(StandardSocketOptions.SO_SNDBUF));
            server.bind(new InetSocketAddress(
                    InetAddress.getByName("0.0.0.0"), PORT
            ));
            log.debug("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            var serverKey = server.register(selector, SelectionKey.OP_READ);
            JavaLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        serverKey.cancel();
                        assert !serverKey.isValid();
                        selector.wakeup();
                        return null;
                    },
                    l -> {                         // <consumer>
                        // does nothing
                    }
            );
            var addresses = new HashMap<SocketAddress, Instant>();
            var buffers = new LinkedList<ByteBuffer>();
            while (serverKey.isValid()) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    continue;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext();
                     i.remove()) {
                    var selectedKey = i.next();
                    if (selectedKey.isReadable()) {
                        var channel = (DatagramChannel) selectedKey.channel();
                        var buffer = _Message.OfBuffer.empty();
                        var address = channel.receive(
                                buffer.clear()); // IOException
                        assert !buffer.hasRemaining() : "not all bytes received";
                        addresses.put(address, Instant.now());
                        if (!HelloWorldServerUtils.isKeep(
                                _Message.OfBuffer.getMessage(buffer))) {
                            var offered = buffers.offer(buffer.clear());
                            assert offered : "failed to offer";
                            selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                        }
                    }
                    if (selectedKey.isWritable()) {
                        assert !buffers.isEmpty();
                        var buffer = buffers.removeFirst();
                        var instantToKeep = Instant.now()
                                .minus(DURATION_TO_KEEP_ADDRESSES);
                        for (var j = addresses.entrySet().iterator();
                             j.hasNext(); ) {
                            var entry = j.next();
                            if (entry.getValue().isBefore(instantToKeep)) {
                                j.remove();
                                continue;
                            }
                            server.send(buffer.clear(),
                                        entry.getKey()); // IOException
                            assert !buffer.hasRemaining() : "not all bytes sent";
                        }
                        if (buffers.isEmpty()) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        }
                    }
                }
            }
        }
    }

    private ChatUdp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
