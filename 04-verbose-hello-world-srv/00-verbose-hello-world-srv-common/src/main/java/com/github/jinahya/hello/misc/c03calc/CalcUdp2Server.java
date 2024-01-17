package com.github.jinahya.hello.misc.c03calc;

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
import com.github.jinahya.hello.util._UdpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

@Slf4j
class CalcUdp2Server {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = DatagramChannel.open()) {
            // ------------------------------------------------------------------------------- reuse
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            // ----------------------------------------------------------------------- bind/register
            final var serverKey = _UdpUtils
                    .logBound(server.bind(_CalcConstants.ADDR))
                    .configureBlocking(false)
                    .register(
                            selector,
                            SelectionKey.OP_READ,
                            new CalcUdp2ServerAttachment()
                    );
            JavaLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit,
                    () -> {
                        server.close();
                        assert !serverKey.isValid();
                        selector.wakeup();
                        return null;
                    }
            );
            // ------------------------------------------------------------------------------ select
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_CalcConstants.SELECT_TIMEOUT_MILLIS) == 0) {
                    continue;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var selectedKey = i.next();
                    i.remove();
                    if (selectedKey.isReadable()) {
                        final var channel = (DatagramChannel) selectedKey.channel();
                        final var attachment = (CalcUdp2ServerAttachment) selectedKey.attachment();
                        attachment.receiveRequest(channel);
                        selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                    }
                    if (selectedKey.isWritable()) {
                        final var channel = (DatagramChannel) selectedKey.channel();
                        final var attachment = (CalcUdp2ServerAttachment) selectedKey.attachment();
                        attachment.sendResult(channel);
                        if (attachment.isMessagesEmpty()) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        }
                    }
                }
            }
        }
    }

    private CalcUdp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
