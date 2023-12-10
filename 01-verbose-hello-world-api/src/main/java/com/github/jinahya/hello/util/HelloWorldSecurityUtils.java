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

import com.github.jinahya.hello.util.java.nio.JavaNioUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A utility class for {@link java.security} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldSecurityUtils {

    /**
     * Updates specified message digest with specified number of bytes of specified byte buffer
     * preceding its current {@link ByteBuffer#position() position}.
     *
     * @param digest the message digest to be updated.
     * @param buffer the byte buffer whose content to be updated to the {@code digest}.
     * @param count  the number of bytes, preceding {@code buffer}'s current position, to be updated
     *               to the {@code digest}.
     */
    public static void updatePreceding(MessageDigest digest, ByteBuffer buffer,
                                       final int count) {
        Objects.requireNonNull(digest, "digest is null");
        Objects.requireNonNull(buffer, "buffer is null");
        if (count < 0) {
            throw new IllegalArgumentException(
                    "bytes(" + count + ") is negative");
        }
        if (count == 0) {
            return;
        }
        if (count > buffer.position()) {
            throw new BufferUnderflowException();
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            digest.update(buffer.slice(buffer.position() - count, count));
            return;
        }
        if (buffer.hasArray()) {
            digest.update(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position() - count,
                    count
            );
        } else {
            JavaNioUtils.flipAcceptAndRestore(buffer,
                                              b -> digest.update(b));
        }
    }

    /**
     * Updates specified message digest with all preceding bytes of specified byte buffer's current
     * position.
     *
     * @param digest the message digest to be updated.
     * @param buffer the byte buffer whose content to be updated to the {@code digest}.
     * @apiNote This method invokes
     * {@link #updatePreceding(MessageDigest, ByteBuffer, int) updatePreceding(digest, buffer,
     * bytes)} method with {@code digest}, {@code buffer}, and {@code buffer.position()}.
     */
    public static void updateAllPreceding(MessageDigest digest,
                                          ByteBuffer buffer) {
        updatePreceding(digest, buffer, buffer.position());
    }

    private HelloWorldSecurityUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
