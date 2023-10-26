package com.github.jinahya.hello.util;

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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilities for for {@link com.github.jinahya.hello.misc.c01rfc863} package and
 * {@link com.github.jinahya.hello.misc.c02rfc862} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101" // class _Udp...
})
public final class _UdpUtils {

    private static final String LOG_FORMAT_BOUND = "bound to {}";

    private static final String LOG_FORMAT_CONNECTED = "connected to {}";

    private static final String LOG_FORMAT_SENDING = "sending {} byte(s) to {}";

    private static final String LOG_FORMAT_SENT = "{} byte(s) sent to {}";

    private static final String LOG_FORMAT_RECEIVED = "{} byte(s) received from {}";

    // --------------------------------------------------------------------------------------- bound
    public static <T extends DatagramSocket> T logBound(final T socket) {
        Objects.requireNonNull(socket, "socket is null");
        log.info(LOG_FORMAT_BOUND, socket.getLocalSocketAddress());
        return socket;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends DatagramChannel> T logBound(final T channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logBound(channel.socket()).getChannel();
        }
        log.info(LOG_FORMAT_BOUND, channel.getLocalAddress());
        return channel;
    }

    // ----------------------------------------------------------------------------------- connected
    public static <T extends DatagramSocket> T logConnected(final T socket) {
        Objects.requireNonNull(socket, "socket is null");
        log.info(LOG_FORMAT_CONNECTED, socket.getRemoteSocketAddress());
        return socket;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends DatagramChannel> T logConnected(final T channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logConnected(channel.socket()).getChannel();
        }
        log.info(LOG_FORMAT_CONNECTED, channel.getLocalAddress());
        return channel;
    }

    // ------------------------------------------------------------------------------------- sending
    public static DatagramPacket logSending(final DatagramPacket packet) {
        Objects.requireNonNull(packet, "packet is null");
        log.info(LOG_FORMAT_SENDING, packet.getLength(), packet.getSocketAddress());
        return packet;
    }

    public static <T extends DatagramChannel> T logSending(final T channel, final ByteBuffer buffer)
            throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isConnected()) {
            throw new IllegalArgumentException("channel is not connected");
        }
        Objects.requireNonNull(buffer, "buffer is null");
        log.info(LOG_FORMAT_SENDING, buffer.remaining(), channel.getRemoteAddress());
        return channel;
    }

    public static <T extends ByteBuffer> T logSending(final T buffer, final SocketAddress address) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(address, "address is null");
        log.info(LOG_FORMAT_SENDING, buffer.remaining(), address);
        return buffer;
    }

    // ---------------------------------------------------------------------------------------- sent

    public static DatagramPacket logSent(final DatagramPacket packet) {
        Objects.requireNonNull(packet, "packet is null");
        log.info(LOG_FORMAT_SENT, packet.getLength(), packet.getSocketAddress());
        return packet;
    }

    public static <T extends DatagramChannel> T logSent(final T channel, final ByteBuffer buffer)
            throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isConnected()) {
            throw new IllegalArgumentException("channel is not connected");
        }
        Objects.requireNonNull(buffer, "buffer is null");
        log.info(LOG_FORMAT_SENT, buffer.position(), channel.getRemoteAddress());
        return channel;
    }

    public static <T extends ByteBuffer> T logSent(final T buffer, final SocketAddress address) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(address, "address is null");
        log.info(LOG_FORMAT_SENT, buffer.position(), address);
        return buffer;
    }

    // ------------------------------------------------------------------------------------ received
    public static DatagramPacket logReceived(final DatagramPacket packet) {
        Objects.requireNonNull(packet, "packet is null");
        log.info(LOG_FORMAT_RECEIVED, packet.getLength(), packet.getSocketAddress());
        return packet;
    }

    public static <T extends DatagramChannel> T logReceived(final T channel,
                                                            final ByteBuffer buffer)
            throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isConnected()) {
            throw new IllegalArgumentException("channel is not connected");
        }
        Objects.requireNonNull(buffer, "buffer is null");
        log.info(LOG_FORMAT_RECEIVED, buffer.position(), channel.getRemoteAddress());
        return channel;
    }

    public static <T extends ByteBuffer> T logReceived(final T buffer,
                                                       final SocketAddress address) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(address, "address is null");
        log.info(LOG_FORMAT_RECEIVED, buffer.position(), address);
        return buffer;
    }

    // ---------------------------------------------------------------------------------------------

    private _UdpUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
