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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
abstract class Rfc863 {

    // ------------------------------------------------------------------------------ host/port/addr

    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    private static final int RFC863_PORT = 9;

    private static final int PORT = RFC863_PORT + 50000;

    static final InetSocketAddress ADDR = new InetSocketAddress(HOST, PORT);

    // -------------------------------------------------------------------------------------- digest

    /**
     * Returns a new message digest implements {@code SHA-1} algorithm.
     *
     * @return a new message digest.
     */
    static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (final NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }
    }

    static void logDigest(final MessageDigest digest) {
        log.info("digest: {}", HexFormat.of().formatHex(digest.digest()));
    }

    // ------------------------------------------------------------------------------------ logBytes

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

    // ------------------------------------------------------------------------------------ logBound
    static void logBound(final SocketAddress address) {
        Objects.requireNonNull(address, "address is null");
        log.info("bound to {}", address);
    }

    // -------------------------------------------------------------------------------- logConnected
    static void logConnected(final SocketAddress address) {
        Objects.requireNonNull(address, "address is null");
        log.info("connected to {}", address);
    }
}
