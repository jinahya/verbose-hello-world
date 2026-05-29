package com.github.jinahya.hello.api.util;

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

import java.nio.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Helpers for {@link MessageDigest java.security.MessageDigest} — currently a single
 * {@link ByteBuffer}-aware {@code update} that feeds the digest with a trailing slice of the
 * buffer in whichever shape ({@code array}-backed, {@code slice()}-view, or rewound view) is
 * available without copying.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S4274" // > Replace this assert with a proper check.
})
public final class JavaSecurityMessageDigestUtils {

    // ------------------------------------------------------------------------------- MessageDigest

    /**
     * Feeds the trailing {@code bytes}-length window — i.e. the {@code bytes} bytes immediately
     * preceding {@code buffer}'s current {@link ByteBuffer#position() position} — into
     * {@code digest}. Uses the buffer's backing array when available, otherwise a
     * {@link ByteBuffer#slice(int, int) slice()}-view or a rewound view (chosen randomly to
     * exercise both paths under tests). The buffer's position and limit are restored before
     * return.
     *
     * @param digest the {@link MessageDigest} to update; must not be {@code null}.
     * @param buffer the byte buffer whose recent bytes are fed to the {@code digest}; must not be
     *               {@code null}.
     * @param bytes  the number of bytes preceding {@code buffer.position()} to feed; must satisfy
     *               {@code 0 <= bytes <= buffer.position()}.
     * @throws NullPointerException     if either {@code digest} or {@code buffer} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code bytes} is negative or greater than
     *                                  {@code buffer.position()}.
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
        if (ThreadLocalRandom.current().nextBoolean()) { // TODO: remove?
            digest.update(buffer.slice(buffer.position() - bytes, bytes));
            return;
        }
        final var limit = buffer.limit();
        buffer.flip().position(buffer.limit() - bytes);
        digest.update(buffer);
        assert !buffer.hasRemaining(); // java:S4274
        buffer.limit(limit);
    }

    // ---------------------------------------------------------------------------------------------
    private JavaSecurityMessageDigestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
