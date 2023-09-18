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

import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.function.Function;

@Slf4j
final class _Rfc862Utils extends _Rfc86_Utils {

    // --------------------------------------------------------------------------------------- bytes
    static void logClientBytes(final int bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("sending (and getting echoed back) {} byte(s)", bytes);
    }

    static void logServerBytes(final int bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("received (and echoed back) {} byte(s)", bytes);
    }

    // -------------------------------------------------------------------------------------- digest
    static MessageDigest newDigest() {
        return newDigest(_Rfc862Constants.ALGORITHM);
    }

    private static Function<? super byte[], ? extends CharSequence> PRINTER =
            b -> Base64.getEncoder().encodeToString(b);

    static void logDigest(final MessageDigest digest) {
        logDigest(digest, PRINTER);
    }

    static void logDigest(final byte[] array, final int offset, final int length) {
        logDigest(_Rfc862Constants.ALGORITHM, array, offset, length, PRINTER);
    }

    static void logDigest(final ByteBuffer buffer) {
        logDigest(_Rfc862Constants.ALGORITHM, buffer, PRINTER);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private _Rfc862Utils() {
        super();
        throw new AssertionError("instantiation is not allowed");
    }
}
