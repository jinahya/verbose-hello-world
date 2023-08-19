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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import java.util.function.Function;

/**
 * Utilities for {@link java.nio} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class HelloWorldNioUtils {

    /**
     * Adjust specified byte buffer's {@link ByteBuffer#limit() limit} if its
     * {@link ByteBuffer#remaining()} is greater than specified value.
     *
     * @param byteBuffer   the byte buffer whose {@link ByteBuffer#limit() limit} is adjusted.
     * @param maxRemaining the maximum {@link ByteBuffer#remaining() remaining} value that the
     *                     {@code buffer} should have.
     * @return given {@code buffer}.
     */
    public static ByteBuffer adjustLimit(ByteBuffer byteBuffer, int maxRemaining) {
        Objects.requireNonNull(byteBuffer, "byteBuffer is null");
        if (maxRemaining < 0) {
            throw new IllegalArgumentException("maxRemaining(" + maxRemaining + ") is negative");
        }
        if (byteBuffer.remaining() > maxRemaining) {
            byteBuffer.limit(byteBuffer.position() + maxRemaining);
        }
        return byteBuffer;
    }

    public static <B extends ByteBuffer, R> R flipApplyAndRestore(
            B buffer, Function<? super B, ? extends R> function) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(function, "function is null");
        var l = buffer.limit();
        var p = buffer.position();
        buffer.flip(); // limit -> position, position -> zero
        try {
            return function.apply(buffer);
        } finally {
            buffer.limit(l).position(p);
        }
    }

    public static <B extends ByteBuffer> int flipReadAndRestore(B buffer,
                                                                ReadableByteChannel channel) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(channel, "channel is null");
        return flipApplyAndRestore(
                buffer,
                b -> {
                    try {
                        return channel.read(b);
                    } catch (IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                }
        );
    }

    private HelloWorldNioUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
