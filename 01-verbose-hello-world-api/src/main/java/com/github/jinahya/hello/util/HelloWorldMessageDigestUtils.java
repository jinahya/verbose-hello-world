package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-srv
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A utility class for {@link MessageDigest}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldMessageDigestUtils {

    /**
     * Updates specified message digest with specified number of bytes of specified byte buffer
     * preceding its current {@link ByteBuffer#position() position}.
     *
     * @param digest the message digest to be updated.
     * @param buffer the byte buffer.
     * @param bytes  the number of preceding bytes before {@code buffer}'s current
     *               {@link ByteBuffer#position() position} to be updated to the {@code digest}.
     */
    public static void updatePreceding(MessageDigest digest, ByteBuffer buffer, final int bytes) {
        Objects.requireNonNull(digest, "digest is null");
        Objects.requireNonNull(buffer, "buffer is null");
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        if (bytes > buffer.position()) {
            throw new BufferUnderflowException();
        }
        if (ThreadLocalRandom.current().nextBoolean() && buffer.hasArray()) {
            digest.update(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position() - bytes,
                    bytes
            );
            return;
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            var position = buffer.position();
            var limit = buffer.limit();
            digest.update(buffer.position(position - bytes).limit(position));
            buffer.limit(limit).position(position);
            return;
        }
        digest.update(buffer.slice(buffer.position() - bytes, bytes));
    }

    private HelloWorldMessageDigestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
