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

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class _Rfc863Utils {

    // -------------------------------------------------------------------------------- array/buffer

    /**
     * Returns a new array of bytes whose length is between 1 and 1024, both inclusive.
     *
     * @return a new array of bytes.
     */
    private static byte[] array() {
        return new byte[ThreadLocalRandom.current().nextInt(1024) + 1];
    }

    /**
     * Returns a new array of bytes whose length is between {@code 1} and {@code 1024}, both
     * inclusive.
     *
     * @return a new non-empty array of bytes.
     */
    static byte[] newArray() {
        final var array = array();
        log.debug("array.length: {}", array.length);
        return array;
    }

    /**
     * Returns a new byte buffer {@link ByteBuffer#wrap(byte[]) wraps} a result of
     * {@link #newArray()}.
     *
     * @return a new byte buffer {@link ByteBuffer#wrap(byte[]) wraps} a result of
     * {@link #newArray()}.
     * @see #newArray()
     */
    static ByteBuffer newBuffer() {
        final var buffer = ByteBuffer.wrap(array());
        log.debug("buffer.capacity: {}", buffer.capacity());
        return buffer;
    }

    // --------------------------------------------------------------------------------------- bytes

    /**
     * Returns a new {@code int} greater than or equals to {@code 0} and less than specified value.
     *
     * @param maxExclusive the maximum value, exclusive; must be positive.
     * @return a new {@code int} greater than or equals to {@code 0} and less than
     * {@code maxExclusive}.
     */
    private static int newBytes(final int maxExclusive) {
        if (maxExclusive <= 0) {
            throw new IllegalArgumentException(
                    "maxExclusive(" + maxExclusive + ") is not positive");
        }
        return ThreadLocalRandom.current().nextInt(maxExclusive);
    }

    /**
     * Returns a new {@code int} between {@code 0}(inclusive) and {@code 65536}(exclusive).
     *
     * @return a new {@code int} between {@code 0}(inclusive) and {@code 65536}(exclusive).
     */
    static int newBytes() {
        return newBytes(65536);
    }

    /**
     * Logs specified client bytes.
     *
     * @param bytes the client bytes to log.
     */
    static void logClientBytes(final long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("sending {} bytes", bytes);
    }

    /**
     * Logs specified server bytes.
     *
     * @param bytes the server bytes to log.
     */
    static void logServerBytes(final long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("{} bytes received (and discarded)", bytes);
    }

    // -------------------------------------------------------------------------------------- digest

    /**
     * Returns a new message digest of {@link _Rfc863Constants#ALGORITHM}.
     *
     * @return a new message digest of {@link _Rfc863Constants#ALGORITHM}.
     * @see _Rfc863Constants#ALGORITHM
     */
    static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance(_Rfc863Constants.ALGORITHM);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(
                    "failed to create a message digest with "
                    + _Rfc863Constants.ALGORITHM,
                    nsae
            );
        }
    }

    static void logDigest(final MessageDigest digest) {
        Objects.requireNonNull(digest, "digest is null");
        log.info("digest: {}", HexFormat.of().formatHex(digest.digest()));
    }

    static void logDigest(final byte[] array, final int offset, final int length) {
        final var digest = newDigest();
        digest.update(array, offset, length);
        logDigest(digest);
    }

    static void logDigest(final ByteBuffer buffer) {
        final var digest = newDigest();
        digest.update(buffer);
        logDigest(digest);
    }

    // ----------------------------------------------------------------------------------------- key

//    static void logKey(final SelectionKey key) {
//        Objects.requireNonNull(key, "key is null");
//        log.debug(
//                """
//                        key: {}
//                        \tconnectable: {}\tacceptable: {}\treadable: {}\twritable; {}""",
//                key,
//                key.isConnectable(),
//                key.isAcceptable(),
//                key.isReadable(),
//                key.isWritable()
//        );
//    }

    private _Rfc863Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
