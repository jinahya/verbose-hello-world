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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Objects;

@Slf4j
final class _Rfc863Utils {

    /**
     * Logs specified client bytes.
     *
     * @param bytes the client bytes to log.
     */
    static int logClientBytes(final int bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("sending {} bytes", bytes);
        return bytes;
    }

    /**
     * Logs specified server bytes.
     *
     * @param bytes the server bytes to log.
     */
    static long logServerBytes(final long bytes) {
        if (bytes < 0L) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("{} bytes received (and discarded)", bytes);
        return bytes;
    }

    private static void logServerBytes(final int bytes, final SocketAddress address) {
        log.info("{} bytes received from {}, (and discarded)", bytes, address);
    }

    static void logServerBytes(final ByteBuffer buffer, final SocketAddress address) {
        Objects.requireNonNull(buffer, "buffer is null");
        logServerBytes(buffer.position(), address);
    }

    static void logServerBytes(final DatagramPacket packet) {
        Objects.requireNonNull(packet, "packet is null");
        logServerBytes(packet.getLength(), packet.getSocketAddress());
    }

    /**
     * Returns a new message digest of {@link _Rfc863Constants#ALGORITHM}.
     *
     * @return a new message digest of {@link _Rfc863Constants#ALGORITHM}.
     * @see _Rfc863Constants#ALGORITHM
     */
    static MessageDigest newDigest() {
        return _Rfc86_Utils.newDigest(_Rfc863Constants.ALGORITHM);
    }

    static void logDigest(final MessageDigest digest) {
        _Rfc86_Utils.logDigest(digest, _Rfc863Constants.PRINTER);
    }

    static void logDigest(final byte[] array, final int offset, final int length) {
        _Rfc86_Utils.logDigest(_Rfc863Constants.ALGORITHM, array, offset, length,
                               _Rfc863Constants.PRINTER);
    }

    static void logDigest(final ByteBuffer buffer) {
        _Rfc86_Utils.logDigest(_Rfc863Constants.ALGORITHM, buffer, _Rfc863Constants.PRINTER);
    }

    /**
     * Creates a new instance.
     */
    private _Rfc863Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
