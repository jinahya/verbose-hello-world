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
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp2Server {

    static class Attachment {

        final ByteBuffer buffer = _Rfc862Utils.newByteBuffer();

        int bytes = 0;

        final MessageDigest digest = _Rfc862Utils.newMessageDigest();
    }

    public static void main(String... args) throws IOException, InterruptedException {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(_Rfc862Constants.ENDPOINT);
            log.debug("[S] bound to {}", server.getLocalAddress());
            server.socket().setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isAcceptable()) {
                        var client = ((ServerSocketChannel) key.channel()).accept();
                        log.debug("[S] accepted from {}, through {}", client.getRemoteAddress(),
                                  client.getLocalAddress());
                        var attachment = new Attachment();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ, attachment);
                        key.cancel();
                        continue;
                    }
                    if (key.isReadable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        var read = channel.read(attachment.buffer);
                        log.trace("[S] - read: {}", read);
                        if (read == -1) {
                            channel.shutdownInput();
                            key.interestOpsAnd(~SelectionKey.OP_READ);
                            if (attachment.buffer.position() == 0) {
                                log.debug("[S] byte(s) received and echoed back: {}",
                                          attachment.bytes);
                                log.debug("[S] digest: {}",
                                          HexFormat.of().formatHex(attachment.digest.digest()));
                                key.cancel();
                                continue;
                            }
                        } else {
                            attachment.bytes += read;
                            HelloWorldSecurityUtils.updatePreceding(
                                    attachment.digest, attachment.buffer, read
                            );
                        }
                        if (attachment.buffer.position() > 0) {
                            key.interestOpsOr(SelectionKey.OP_WRITE);
                        }
                    }
                    if (key.isWritable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        assert attachment.buffer.position() > 0;
                        attachment.buffer.flip(); // limit -> position; position -> zero
                        var written = channel.write(attachment.buffer);
                        log.trace("[S] - written: {}", written);
                        attachment.buffer.compact(); // limit -> position, position -> zero
                        if (attachment.buffer.position() == 0) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            if ((key.interestOps() & SelectionKey.OP_READ) == 0) {
                                channel.shutdownOutput();
                                key.cancel();
                                log.debug("[S] byte(s) received and echoed back: {}",
                                          attachment.bytes);
                                log.debug("[S] digest: {}",
                                          HexFormat.of().formatHex(attachment.digest.digest()));
                            }
                        }
                    }
                }
            }
        }
    }

    private Rfc862Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
