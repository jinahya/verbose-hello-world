package com.github.jinahya.hello.miscellaneous.c01rfc863;

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

    static long soTimeoutInMillis() {
        return _Rfc863Constants.SO_TIMEOUT.toMillis();
    }

    static int soTimeoutInMillisAsInt() {
        return Math.toIntExact(soTimeoutInMillis());
    }

    /**
     * Returns a new array of bytes whose length is between {@code 1} and {@code 1024}, both
     * inclusive.
     *
     * @return a new array of bytes.
     */
    static byte[] newArray() {
        return new byte[ThreadLocalRandom.current().nextInt(1024) + 1];
    }

    /**
     * Returns a new byte buffer wrapping a result of {@link #newArray()}.
     *
     * @return a new byte buffer.
     */
    static ByteBuffer newBuffer() {
        return ByteBuffer.wrap(newArray());
    }

    static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance(_Rfc863Constants.ALGORITHM);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(
                    "failed to create a message digest with " + _Rfc863Constants.ALGORITHM,
                    nsae
            );
        }
    }

    private static String getDigest(MessageDigest digest) {
        return HexFormat.of().formatHex(digest.digest());
    }

    static void logClientBytes(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("sending {} bytes...", bytes);
    }

    static void logServerBytes(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("{} bytes received (and discarded)", bytes);
    }

    static void logDigest(MessageDigest digest) {
        Objects.requireNonNull(digest, "digest is null");
        log.info("digest: {}", getDigest(digest));
    }

    private _Rfc863Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
