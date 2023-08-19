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

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.github.jinahya.hello.miscellaneous.c01rfc863._Rfc863Constants.ADDR;

@Slf4j
class Rfc863Tcp2Client {

    private static class Attachment extends Rfc863Tcp2Server.Attachment {

    }

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            HelloWorldNetUtils.printSocketOptions(client);
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(ADDR, 0));
                log.debug("bound to {}", client.getLocalAddress());
            }
            client.configureBlocking(false);
            if (client.connect(_Rfc863Constants.ADDRESS)) {
                log.debug("connected (immediately) to {}, through {}",
                          client.getRemoteAddress(), client.getLocalAddress());
                client.register(
                        selector,
                        SelectionKey.OP_WRITE,
                        _Rfc863Utils.newBuffer()
                );
            } else {
                client.register(selector, SelectionKey.OP_CONNECT);
            }
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    continue;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isConnectable()) {
                        var channel = (SocketChannel) key.channel();
                        var connected = channel.finishConnect();
                        assert connected;
                        log.debug("connected to {}, through {}", channel.getRemoteAddress(),
                                  channel.getLocalAddress());
                        var attachment = new Attachment();
                        attachment.bytes = ThreadLocalRandom.current().nextInt(1048576);
                        _Rfc863Utils.logClientBytes(attachment.bytes);
                        attachment.buffer.position(attachment.buffer.limit());
                        key.attach(attachment);
                        if (attachment.bytes > 0) {
                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                        continue;
                    }
                    if (key.isWritable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        assert attachment.bytes > 0;
                        if (!attachment.buffer.hasRemaining()) {
                            attachment.buffer.clear();
                            ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                            attachment.buffer.limit(
                                    Math.min(attachment.buffer.limit(), attachment.bytes)
                            );
                        }
                        var w = channel.write(attachment.buffer);
                        HelloWorldSecurityUtils.updatePreceding(
                                attachment.digest,
                                attachment.buffer,
                                w
                        );
                        if ((attachment.bytes -= w) == 0) {
                            _Rfc863Utils.logDigest(attachment.digest);
                            key.cancel();
                        }
                    }
                }
            }
        }
    }

    private Rfc863Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
