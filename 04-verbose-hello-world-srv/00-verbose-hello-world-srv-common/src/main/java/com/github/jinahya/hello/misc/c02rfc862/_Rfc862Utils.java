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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Objects;

@Slf4j
final class _Rfc862Utils {

    // --------------------------------------------------------------------------------------- bytes
    static int logClientBytes(final int bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("sending (and getting echoed-back) {} byte(s)", bytes);
        return bytes;
    }

    static long logServerBytes(final long bytes) {
        if (bytes < 0L) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("{} byte(s) received (and echoed-back)", bytes);
        return bytes;
    }

    // -------------------------------------------------------------------------------------- digest
    static MessageDigest newDigest() {
        return _Rfc86_Utils.newDigest(_Rfc862Constants.ALGORITHM);
    }

    static void logDigest(final MessageDigest digest) {
        Objects.requireNonNull(digest, "digest is null");
        _Rfc86_Utils.logDigest(digest, _Rfc862Constants.PRINTER);
    }

    static void logDigest(final byte[] array, final int offset, final int length) {
        _Rfc86_Utils.logDigest(_Rfc862Constants.ALGORITHM, array, offset, length,
                               _Rfc862Constants.PRINTER);
    }

    static void logDigest(final ByteBuffer buffer) {
        _Rfc86_Utils.logDigest(_Rfc862Constants.ALGORITHM, buffer, _Rfc862Constants.PRINTER);
    }

    // ---------------------------------------------------------------------------------------------

    private _Rfc862Utils() {
        super();
        throw new AssertionError("instantiation is not allowed");
    }
}
