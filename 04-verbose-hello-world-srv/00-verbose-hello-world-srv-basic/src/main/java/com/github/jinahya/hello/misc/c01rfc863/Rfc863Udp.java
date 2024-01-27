package com.github.jinahya.hello.misc.c01rfc863;

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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.channels.DatagramChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
abstract class Rfc863Udp extends Rfc863 {

    // ------------------------------------------------------------------------------------ logBound
    static void logBound(final DatagramSocket socket) {
        Objects.requireNonNull(socket, "socket is null");
        if (!socket.isBound()) {
            throw new IllegalArgumentException("not found: " + socket);
        }
        logBound(socket.getLocalSocketAddress());
    }

    static void logBound(final DatagramChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            logBound(channel.socket());
            return;
        }
        logBound(channel.getLocalAddress());
    }

    // -------------------------------------------------------------------------------- logConnected
    static void logConnected(final DatagramSocket socket) {
        Objects.requireNonNull(socket, "socket is null");
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("not connected: " + socket);
        }
        logConnected(socket.getRemoteSocketAddress());
    }

    static void logConnected(final DatagramChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (!channel.isConnected()) {
            throw new IllegalArgumentException("not connected: " + channel);
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            logConnected(channel.socket());
            return;
        }
        logConnected(channel.getRemoteAddress());
    }
}
