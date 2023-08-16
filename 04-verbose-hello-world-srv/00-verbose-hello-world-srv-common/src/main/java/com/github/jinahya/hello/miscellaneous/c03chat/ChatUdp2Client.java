package com.github.jinahya.hello.miscellaneous.c03chat;

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

import com.github.jinahya.hello.HelloWorldServerConstants;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatUdp2Client {

    public static void main(String... args) throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            addr = InetAddress.getLoopbackAddress();
        }
        var address = new InetSocketAddress(addr, _ChatConstants.PORT);
        log.debug("address: {}", address);
        var executor = Executors.newScheduledThreadPool(3);
        var futures = new ArrayList<Future<?>>();
        try (var selector = Selector.open(); var client = DatagramChannel.open()) {
            log.debug("[S]: SO_RCVBUF: {}", client.getOption(StandardSocketOptions.SO_RCVBUF));
            log.debug("[S]: SO_SNFBUD: {}", client.getOption(StandardSocketOptions.SO_SNDBUF));
            var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                try {
                    client.connect(address);
                } catch (IOException ioe) {
                    connect = false;
                }
            }
            var messages = new LinkedList<ByteBuffer>();
            client.configureBlocking(false);
            var clientKey = client.register(selector, SelectionKey.OP_READ);
            futures.add(executor.scheduleAtFixedRate(
                    () -> {
                        messages.addLast(_ChatMessage.newBuffer(HelloWorldServerConstants.KEEP));
                        clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                        selector.wakeup();
                    },
                    1L,
                    ChatUdp2Server.DURATION.toSeconds() >> 1,
                    TimeUnit.SECONDS
            ));
            HelloWorldLangUtils.callWhenRead(
                    v -> !Thread.currentThread().isInterrupted(),
                    HelloWorldServerConstants.QUIT,
                    () -> {
                        clientKey.cancel();
                        assert !clientKey.isValid();
                        selector.wakeup();
                        return null;
                    },
                    l -> {
                        messages.addLast(_ChatMessage.newBuffer(_ChatUtils.prependUsername(l)));
                        clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                        selector.wakeup();
                    }
            );
            while (clientKey.isValid()) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    continue;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var selectedKey = i.next();
                    if (selectedKey.isReadable()) {
                        var channel = (DatagramChannel) selectedKey.channel();
                        var buffer = _ChatMessage.newBuffer();
                        channel.receive(buffer); // IOException
                        log.debug("buffer: {}", buffer);
                        assert !buffer.hasRemaining() : "not all bytes received";
                        _ChatMessage.printToSystemOut(buffer);
                    }
                    if (selectedKey.isWritable()) {
                        assert !messages.isEmpty();
                        var channel = (DatagramChannel) selectedKey.channel();
                        var buffer = messages.removeFirst();
                        channel.send(buffer, address);
                        assert !buffer.hasRemaining() : "not all bytes sent";
                        if (messages.isEmpty()) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        }
                    }
                }
            } // end-of-while
        } // end-of-try-with-resources
        futures.forEach(f -> f.cancel(true));
        for (executor.shutdown(); !executor.awaitTermination(8L, TimeUnit.SECONDS); ) {
        }
    } // end-of-main

    private ChatUdp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
