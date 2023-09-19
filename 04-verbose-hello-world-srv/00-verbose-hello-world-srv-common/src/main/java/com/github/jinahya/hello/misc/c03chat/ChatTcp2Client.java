package com.github.jinahya.hello.misc.c03chat;

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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp2Client {

    // @formatter:off
    private static class ChatTcp2ClientAttachment extends ChatTcp2Server.ChatTcp2ServerAttachment {
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            addr = InetAddress.getLoopbackAddress();
        }
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            SelectionKey clientKey;
            client.configureBlocking(false);
            if (client.connect(
                    new InetSocketAddress(addr, _ChatConstants.PORT))) {
                log.debug("(immediately) connected to {}, through {}",
                          client.getRemoteAddress(),
                          client.getLocalAddress());
                clientKey = client.register(selector, SelectionKey.OP_READ,
                                            new ChatTcp2ClientAttachment());
            } else {
                clientKey = client.register(selector, SelectionKey.OP_CONNECT);
            }
            JavaLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        clientKey.cancel();
                        assert !clientKey.isValid();
                        selector.wakeup();
                        return null;
                    },
                    l -> {                         // <consumer>
                        var attachment = ((ChatTcp2ClientAttachment) clientKey.attachment());
                        if (attachment == null) { // not connected yet.
                            return;
                        }
                        var buffer = _ChatMessage.OfBuffer.of(
                                _ChatUtils.prependUsername(l));
                        attachment.buffers.add(buffer);
                        clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                        selector.wakeup();
                    }
            );
            while (clientKey.isValid()) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    continue;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext();
                     i.remove()) {
                    var key = i.next();
                    if (key.isConnectable()) {
                        var channel = (SocketChannel) key.channel();
                        var connected = channel.finishConnect();
                        assert connected;
                        log.debug("connected to {}, through {}",
                                  channel.getRemoteAddress(),
                                  channel.getLocalAddress());
                        key.interestOpsAnd(~SelectionKey.OP_CONNECT);
                        key.attach(new ChatTcp2ClientAttachment());
                        key.interestOpsOr(SelectionKey.OP_READ);
                        continue;
                    }
                    if (key.isReadable()) {
                        var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        var attachment = (ChatTcp2ClientAttachment) key.attachment();
                        var r = channel.read(attachment.buffer);
                        if (r == -1) {
                            channel.close();
                            assert !clientKey.isValid();
                            continue;
                        }
                        if (!attachment.buffer.hasRemaining()) {
                            _ChatMessage.OfBuffer.printToSystemOut(
                                    attachment.buffer);
                            attachment.buffer.clear();
                        }
                    }
                    if (key.isWritable()) {
                        var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        var attachment = (ChatTcp2ClientAttachment) key.attachment();
                        assert !attachment.buffers.isEmpty();
                        var buffer = attachment.buffers.get(0);
                        assert buffer.hasRemaining();
                        var w = channel.write(buffer);
                        assert w > 0;
                        if (!buffer.hasRemaining()) {
                            attachment.buffers.remove(0);
                            if (attachment.buffers.isEmpty()) {
                                key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            }
                        }
                    }
                }
            }
        }
    }
}
