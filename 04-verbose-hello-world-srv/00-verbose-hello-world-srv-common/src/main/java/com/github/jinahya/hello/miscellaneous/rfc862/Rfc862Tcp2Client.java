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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp2Client {

    private static class Attachment extends Rfc862Tcp2Server.Attachment {

        Attachment() {
            super();
            bytes = ThreadLocalRandom.current().nextInt(1048576);
            clearBuffer();
        }

        void clearBuffer() {
            buffer.clear();
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.limit(Math.min(buffer.limit(), bytes));
        }
    }

    public static void main(String... args) throws IOException, InterruptedException {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.debug("[C] bound to {}", client.getLocalAddress());
            }
            client.configureBlocking(false);
            if (client.connect(_Rfc862Constants.ENDPOINT)) {
                log.debug("connected (immediately) to {}, through {}",
                          client.getRemoteAddress(), client.getLocalAddress());
                var attachment = new Attachment();
                log.debug("[C] sending {} bytes: ", attachment.bytes);
                client.register(selector,
                                attachment.buffer.hasRemaining() ? SelectionKey.OP_WRITE : 0,
                                attachment);
            } else {
                client.register(selector, SelectionKey.OP_CONNECT);
            }
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isConnectable()) {
                        var channel = (SocketChannel) key.channel();
                        var connected = channel.finishConnect();
                        assert connected;
                        log.debug("[C] connected to {}, through {}", channel.getRemoteAddress(),
                                  channel.getLocalAddress());
                        var attachment = new Attachment();
                        log.debug("[C] sending {} bytes: ", attachment.bytes);
                        key.attach(attachment);
                        if (attachment.buffer.hasRemaining()) {
                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                        continue;
                    }
                    if (key.isWritable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        assert attachment.bytes > 0;
                        if (!attachment.buffer.hasRemaining()) {
                            attachment.clearBuffer();
                        }
                        var written = channel.write(attachment.buffer);
                        log.trace("[C] - written: {}", written);
                        if ((attachment.bytes -= written) == 0) {
                            channel.shutdownOutput();
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                        }
                        if (written > 0) {
                            key.interestOpsOr(SelectionKey.OP_READ);
                        }
                    }
                    if (key.isReadable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        var limit = attachment.buffer.limit();
                        var position = attachment.buffer.position();
                        attachment.buffer.flip(); // limit -> position, position -> zero
                        var read = channel.read(attachment.buffer);
                        log.trace("[C] - read: {}", read);
                        if (read == -1) {
                            assert attachment.bytes == 0;
                            channel.shutdownInput();
                            key.interestOpsAnd(~SelectionKey.OP_READ);
                            key.cancel();
                            log.debug("[C] digest: {}",
                                      HexFormat.of().formatHex(attachment.digest.digest()));
                            continue;
                        }
                        assert read > 0;
                        HelloWorldSecurityUtils.updatePreceding(
                                attachment.digest, attachment.buffer, read
                        );
                        attachment.buffer.limit(limit).position(position);
                        if (attachment.bytes > 0) {
                            key.interestOpsOr(SelectionKey.OP_WRITE);
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
