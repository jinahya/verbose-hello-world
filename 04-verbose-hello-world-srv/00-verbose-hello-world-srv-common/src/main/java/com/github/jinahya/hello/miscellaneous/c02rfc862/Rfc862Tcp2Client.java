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

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp2Client {

    private static class Attachment
            extends Rfc862Tcp2Server.Attachment {

        Attachment() {
            super();
            bytes = ThreadLocalRandom.current().nextInt(1048576);
            buffer.position(buffer.limit());
            _Rfc862Utils.logClientBytesSending(bytes);
            log.info("buffer.capacity: {}", buffer.capacity());
        }

        private final ByteBuffer slice = buffer.slice();
    }

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.info("bound to {}", client.getLocalAddress());
            }
            client.configureBlocking(false);
            if (client.connect(_Rfc862Constants.ADDRESS)) {
                log.info("connected (immediately) to {}, through {}",
                         client.getRemoteAddress(), client.getLocalAddress());
                var attachment = new Attachment();
                var clientKey = client.register(selector, SelectionKey.OP_READ, attachment);
                if (attachment.bytes > 0) {
                    clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                }
            } else {
                client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_CONNECT);
            }
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isConnectable()) {
                        assert key.attachment() == null;
                        var channel = (SocketChannel) key.channel();
                        var connected = channel.finishConnect();
                        assert connected;
                        log.info("connected to {}, through {}", channel.getRemoteAddress(),
                                 channel.getLocalAddress());
                        key.interestOpsAnd(~SelectionKey.OP_CONNECT);
                        var attachment = new Attachment();
                        key.attach(attachment);
                        if (attachment.bytes == 0) {
                            key.cancel();
                        } else {
                            key.interestOpsOr(SelectionKey.OP_WRITE);
                        }
                        continue;
                    }
                    if (key.isWritable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        assert attachment.bytes > 0;
                        if (!attachment.buffer.hasRemaining()) {
                            ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                            attachment.buffer.clear().limit(Math.min(
                                    attachment.buffer.remaining(),
                                    attachment.bytes
                            ));
                        }
                        var w = channel.write(attachment.buffer);
                        assert w >= 0;
                        HelloWorldSecurityUtils.updatePreceding(
                                attachment.digest,
                                attachment.buffer,
                                w
                        );
                        if ((attachment.bytes -= w) == 0) {
                            channel.shutdownOutput(); // IOException
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            _Rfc862Utils.logDigest(attachment.digest);
                        }
                    }
                    if (key.isReadable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        var r = channel.read(
                                attachment.slice.position(0).limit(attachment.buffer.position())
                        );
                        if (r == -1) {
                            if (attachment.bytes > 0) {
                                throw new EOFException("unexpected eof");
                            }
                            key.interestOpsAnd(~SelectionKey.OP_READ);
                            key.cancel();
                        }
                    }
                }
            }
        }
    }

    private Rfc862Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
