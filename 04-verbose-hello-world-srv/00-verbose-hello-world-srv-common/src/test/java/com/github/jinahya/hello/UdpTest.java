package com.github.jinahya.hello;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class UdpTest {

    private static final int MAX_LENGTH =
            (-1 >>> Rfc768Constants.HEADER_LENGTH_SIZE)
            - Rfc768Constants.HEADER_BYTES
            - Rfc791Constants.MIN_HEADER_BYTES;

    @Test
    void __DatagramSocket()
            throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            var sendBufferSize = socket.getSendBufferSize();
            log.debug("sendBufferSize: {}", sendBufferSize);
            assertTrue(sendBufferSize >= MAX_LENGTH);
            var receiveBufferSize = socket.getReceiveBufferSize();
            log.debug("receiveBufferSize: {}", receiveBufferSize);
            assertTrue(receiveBufferSize >= MAX_LENGTH);
        }
    }

    @Test
    void __DatagramChanel()
            throws Exception {
        try (DatagramChannel channel = DatagramChannel.open()) {
            var sendBufferSize = channel.getOption(
                    StandardSocketOptions.SO_SNDBUF);
            log.debug("sendBufferSize: {}", sendBufferSize);
            assertTrue(sendBufferSize >= MAX_LENGTH);
            var receiveBufferSize = channel.getOption(
                    StandardSocketOptions.SO_RCVBUF);
            log.debug("receiveBufferSize: {}", receiveBufferSize);
            assertTrue(receiveBufferSize >= MAX_LENGTH);
        }
    }
}
