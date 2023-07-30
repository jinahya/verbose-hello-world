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
import java.util.Objects;

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

    private HelloWorldNioUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
