package com.github.jinahya.hello.misc;

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

import java.net.DatagramSocket;
import java.nio.channels.DatagramChannel;
import java.util.Objects;

/**
 * Utilities for for {@link com.github.jinahya.hello.misc.c01rfc863} package and
 * {@link com.github.jinahya.hello.misc.c02rfc862} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class _UdpUtils {

    public static <T extends DatagramSocket> T logBound(final T socket) {
        Objects.requireNonNull(socket, "socket is null");
        log.debug("bound to {}", socket.getLocalSocketAddress());
        return socket;
    }

    public static <T extends DatagramChannel> T logBound(final T channel) {
        Objects.requireNonNull(channel, "channel is null");
        logBound(channel.socket());
        return channel;
    }

    public static <T extends DatagramSocket> T logConnected(final T socket) {
        Objects.requireNonNull(socket, "socket is null");
        log.debug("connected to {}", socket.getRemoteSocketAddress());
        return socket;
    }

    public static <T extends DatagramChannel> T logConnected(final T channel) {
        Objects.requireNonNull(channel, "channel is null");
        logConnected(channel.socket());
        return channel;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private _UdpUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
