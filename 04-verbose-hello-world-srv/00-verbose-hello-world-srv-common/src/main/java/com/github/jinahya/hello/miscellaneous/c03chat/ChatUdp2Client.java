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
import com.github.jinahya.hello.HelloWorldServerUtils;
import com.github.jinahya.hello.miscellaneous.c03chat._ChatMessage.OfBuffer;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatUdp2Client {

    private static final Duration PERIOD_TO_SEND_KEEP =
            ChatUdp1Server.DURATION_TO_KEEP_ADDRESSES.dividedBy(2L);

    static {
        assert PERIOD_TO_SEND_KEEP.toSeconds() > 0;
    }

    public static void main(String... args)
            throws Exception {
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
        try (var selector = Selector.open();
             var client = DatagramChannel.open()) {
            log.debug("[S]: SO_RCVBUF: {}", client.getOption(StandardSocketOptions.SO_RCVBUF));
            log.debug("[S]: SO_SNFBUD: {}", client.getOption(StandardSocketOptions.SO_SNDBUF));
            var queue = new LinkedList<ByteBuffer>();
            client.configureBlocking(false);
            var clientKey = client.register(selector, SelectionKey.OP_READ);
            futures.add(executor.scheduleAtFixedRate(
                    () -> {                    // command
                        queue.addLast(OfBuffer.of(HelloWorldServerConstants.KEEP));
                        clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                        selector.wakeup();
                    },
                    PERIOD_TO_SEND_KEEP.toSeconds(), // <initialDelay>
                    PERIOD_TO_SEND_KEEP.toSeconds(), // <period>
                    TimeUnit.SECONDS           // <unit>
            ));
            HelloWorldLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        clientKey.cancel();
                        assert !clientKey.isValid();
                        selector.wakeup();
                        return null;
                    },
                    l -> {                         // <consumer>
                        queue.addLast(OfBuffer.of(_ChatUtils.prependUsername(l)));
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
                        var buffer = _ChatMessage.OfBuffer.empty();
                        channel.receive(buffer); // IOException
                        assert !buffer.hasRemaining() : "not all bytes received";
                        OfBuffer.printToSystemOut(buffer);
                    }
                    if (selectedKey.isWritable()) {
                        assert !queue.isEmpty();
                        var channel = (DatagramChannel) selectedKey.channel();
                        var buffer = queue.removeFirst();
                        channel.send(buffer, address);
                        assert !buffer.hasRemaining() : "not all bytes sent";
                        if (queue.isEmpty()) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        }
                    }
                }
            }
        }
        futures.forEach(f -> f.cancel(true));
        executor.shutdown();
        if (!executor.awaitTermination(8L, TimeUnit.SECONDS)) {
            log.error("executor has not been terminated");
        }
    }

    private ChatUdp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
