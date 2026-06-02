package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import org.mockito.*;
import org.mockito.internal.matchers.*;

import java.nio.*;

import static org.mockito.ArgumentMatchers.*;

/**
 * Provides {@link ArgumentMatcher Mockito argument matchers} reused across {@link HelloWorld}
 * tests.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class HelloWorld__ArgumentMatchers {

    /**
     * Matches a non-{@code null} {@link ByteBuffer} whose
     * {@linkplain ByteBuffer#capacity() capacity} equals {@value HelloWorld#BYTES}.
     */
    private static class NonNullByteBufferOf12CapacityWithLimit12
            implements ArgumentMatcher<ByteBuffer> {

        @Override
        public boolean matches(final ByteBuffer argument) {
            return notNull.matches(argument)
                   && argument.capacity() == HelloWorld.BYTES
                   && argument.limit() == HelloWorld.BYTES;
        }

        private final NotNull<ByteBuffer> notNull = new NotNull<>(ByteBuffer.class);
    }

    /**
     * Matches a non-{@code null} {@link ByteBuffer} whose
     * {@linkplain ByteBuffer#capacity() capacity} equals {@value HelloWorld#BYTES} and which has at
     * least one {@linkplain ByteBuffer#hasRemaining() remaining} byte.
     */
    private static class NonNullByteBufferOf12CapacityWithLimit12HasRemaining
            extends NonNullByteBufferOf12CapacityWithLimit12 {

        @Override
        public boolean matches(final ByteBuffer argument) {
            return super.matches(argument) && argument.hasRemaining();
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns an {@code argThat}-generated {@link ByteBuffer} placeholder that matches a
     * non-{@code null} buffer whose {@linkplain ByteBuffer#capacity() capacity} equals
     * {@value HelloWorld#BYTES}.
     *
     * @return a {@code ByteBuffer} placeholder produced by
     * {@link ArgumentMatchers#argThat(ArgumentMatcher)}.
     * @see ByteBuffer#capacity()
     * @see #buffer12WithRemaining()
     */
    public static ByteBuffer buffer12() {
        return argThat(new NonNullByteBufferOf12CapacityWithLimit12());
    }

    /**
     * Returns an {@code argThat}-generated {@link ByteBuffer} placeholder that matches a
     * non-{@code null} buffer whose {@linkplain ByteBuffer#capacity() capacity} equals
     * {@value HelloWorld#BYTES} and which has at least one
     * {@linkplain ByteBuffer#hasRemaining() remaining} byte.
     *
     * @return a {@code ByteBuffer} placeholder produced by
     * {@link ArgumentMatchers#argThat(ArgumentMatcher)}.
     * @see ByteBuffer#capacity()
     * @see ByteBuffer#hasRemaining()
     * @see #buffer12()
     */
    public static ByteBuffer buffer12WithRemaining() {
        return argThat(new NonNullByteBufferOf12CapacityWithLimit12HasRemaining());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Suppresses instantiation.
     */
    private HelloWorld__ArgumentMatchers() {
        throw new AssertionError("instantiation is not allowed");
    }
}
