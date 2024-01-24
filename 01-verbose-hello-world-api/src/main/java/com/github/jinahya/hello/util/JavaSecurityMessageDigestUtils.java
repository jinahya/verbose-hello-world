package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
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

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class JavaSecurityMessageDigestUtils {

    // ------------------------------------------------------------------------------- MessageDigest

    /**
     * Updates specified message digest with specified number of bytes preceding specified byte
     * buffer's current position.
     *
     * @param digest the message digest to update.
     * @param buffer the byte buffer whose bytes are updated to the {@code digest}.
     * @param bytes  the number of bytes preceding the {@code buffer}'s current {@code position} to
     *               be updated to the {@code digest}; must be not negative nor greater than
     *               {@code buffer.position}.
     */
    public static void updateDigest(final MessageDigest digest, final ByteBuffer buffer,
                                    final int bytes) {
        Objects.requireNonNull(digest, "digest is null");
        Objects.requireNonNull(buffer, "buffer is null");
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") < 0");
        }
        if (bytes > buffer.position()) {
            throw new IllegalArgumentException(
                    "bytes(" + bytes + ") > buffer.position(" + buffer.position() + ")");
        }
        if (buffer.hasArray()) {
            digest.update(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position() - bytes,
                    bytes
            );
            return;
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            digest.update(buffer.slice(buffer.position() - bytes, bytes));
            return;
        }
        final var limit = buffer.limit();
        buffer.limit(buffer.position()).position(buffer.position() - bytes);
        digest.update(buffer);
        buffer.position(buffer.limit()).limit(limit);
    }

    // ---------------------------------------------------------------------------------------------
    private JavaSecurityMessageDigestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
