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

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class _Rfc862Utils {

    // -------------------------------------------------------------------------------- array/buffer

    static byte[] newArray() {
        return new byte[ThreadLocalRandom.current().nextInt(1024) + 1024];
    }

    static ByteBuffer newBuffer() {
        return ByteBuffer.wrap(newArray());
    }

    static int randomBytesLessThan(int maxBytes) {
        if (maxBytes <= 0) {
            throw new IllegalArgumentException(
                    "maxBytes(" + maxBytes + ") is not positive");
        }
        return ThreadLocalRandom.current().nextInt(
                Math.min(65536, maxBytes)
        );
    }

    static int randomBytesLessThanOneMillion() {
        return randomBytesLessThan(1048576);
    }
    // --------------------------------------------------------------------------------------- bytes

    /**
     * Returns a new {@code int} greater than or equals to {@code 0} and less than specified value.
     *
     * @param maxExclusive the maximum value, exclusive.
     * @return a new {@code int} greater than or equals to {@code 0} and less than
     * {@code maxExclusive}.
     */
    static int newBytes(final int maxExclusive) {
        if (maxExclusive <= 0) {
            throw new IllegalArgumentException(
                    "maxExclusive(" + maxExclusive + ") is not positive");
        }
        return ThreadLocalRandom.current().nextInt(maxExclusive);
    }

    private static final int MAX_BYTES = 1048576;

    /**
     * Returns a new {@code int} between {@code 0}(inclusive) and {@value #MAX_BYTES}(inclusive).
     *
     * @return a new {@code int} between {@code 0}(inclusive) and {@value #MAX_BYTES}(inclusive).
     */
    static int newBytes() {
        return newBytes(MAX_BYTES);
    }

    static void logClientBytes(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException(
                    "bytes(" + bytes + ") is negative");
        }
        log.info("sending (and receiving back) {} bytes...", bytes);
    }

    static void logServerBytes(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException(
                    "bytes(" + bytes + ") is negative");
        }
        log.info("{} bytes received (and sent back)", bytes);
    }

    // -------------------------------------------------------------------------------------- digest

    /**
     * Returns a new message digest of {@link _Rfc862Constants#ALGORITHM}.
     *
     * @return a new message digest of {@link _Rfc862Constants#ALGORITHM}.
     * @see _Rfc862Constants#ALGORITHM
     */
    static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance(_Rfc862Constants.ALGORITHM);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(
                    "failed to create a message digest with "
                    + _Rfc862Constants.ALGORITHM,
                    nsae
            );
        }
    }

    static byte[] logDigest(MessageDigest digest) {
        Objects.requireNonNull(digest, "digest is null");
        var result = digest.digest();
        log.info("digest: {}", Base64.getEncoder().encodeToString(result));
        return result;
    }

    static byte[] logDigest(byte[] array, int offset, int length) {
        var digest = newDigest();
        digest.update(array, offset, length);
        return logDigest(digest);
    }

    static byte[] logDigest(ByteBuffer buffer) {
        var digest = newDigest();
        digest.update(buffer);
        return logDigest(digest);
    }

    // ----------------------------------------------------------------------------------------- key
    static void logKey(SelectionKey key) {
        Objects.requireNonNull(key, "key is null");
        log.debug(
                """
                        key: {}
                        \tconnectable: {}\tacceptable: {}\treadable: {}\twritable; {}""",
                key,
                key.isConnectable(),
                key.isAcceptable(),
                key.isReadable(),
                key.isWritable()
        );
    }

    private _Rfc862Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
