package com.github.jinahya.hello.miscellaneous.c02rfc862;

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
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class _Rfc862Utils {

    static long soTimeInMillis() {
        return _Rfc862Constants.SO_TIMEOUT.toMillis();
    }

    static int soTimeoutInMillisAsInt() {
        return Math.toIntExact(soTimeInMillis());
    }

    static byte[] newArray() {
        return new byte[ThreadLocalRandom.current().nextInt(1024) + 1024];
    }

    static ByteBuffer newBuffer() {
        return ByteBuffer.wrap(newArray());
    }

    private static final String ALGORITHM = "SHA-256";

    static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("failed to create a message digest for " + ALGORITHM, nsae);
        }
    }

    private static String getDigest(MessageDigest digest) {
        return Base64.getEncoder().encodeToString(digest.digest());
    }

    private static long requireValidBytes(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        return bytes;
    }

    static void logClientBytesSending(long bytes) {
        requireValidBytes(bytes);
        log.info("sending (and getting echoed-back) {} bytes...", bytes);
    }

    static void logServerBytesSent(long bytes) {
        requireValidBytes(bytes);
        log.info("{} bytes received and echoed-back", bytes);
    }

    static void logDigest(MessageDigest digest) {
        Objects.requireNonNull(digest, "digest is null");
        log.info("digest: {}", getDigest(digest));
    }

    private _Rfc862Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
