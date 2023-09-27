package com.github.jinahya.hello.misc.c02rfc862;

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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

@Slf4j
class Rfc862Udp2Server {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = DatagramChannel.open()) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_READ);
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.READ_TIMEOUT_IN_MILLIS) == 0) {
                    break;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    if (selectedKey.isReadable()) {
                        final var channel = (DatagramChannel) selectedKey.channel();
                        assert channel == server;
                        final var attachment = new Rfc862Udp2ServerAttachment(selectedKey);
                        selectedKey.attach(attachment);
                        final var address = attachment.receive();
                        assert address != null;
                        assert selectedKey.isValid();
                        final var interestOps = selectedKey.interestOps();
                        assert (interestOps & SelectionKey.OP_READ) == 0;
                        assert (interestOps & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE;
                    }
                    if (selectedKey.isWritable()) {
                        final var attachment = (Rfc862Udp2ServerAttachment) selectedKey.attachment();
                        final var s = attachment.send();
                        assert !selectedKey.isValid();
                    }
                }
            }
        }
    }

    private Rfc862Udp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
