package com.github.jinahya.hello.util;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Utilities for {@link java.nio.ByteBuffer}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class HelloWorldByteBufferUtils {

    /**
     * Adjust specified byte buffer's {@link ByteBuffer#limit() limit} if its
     * {@link ByteBuffer#remaining()} is greater than specified value.
     *
     * @param byteBuffer   the byte buffer whose {@link ByteBuffer#limit() limit} is adjusted.
     * @param maxRemaining the maximum {@link ByteBuffer#remaining() remaining} value that the
     *                     {@code buffer} should have.
     * @return given {@code buffer}.
     */
    public static ByteBuffer adjustRemaining(ByteBuffer byteBuffer, int maxRemaining) {
        Objects.requireNonNull(byteBuffer, "byteBuffer is null");
        if (maxRemaining < 0) {
            throw new IllegalArgumentException("maxRemaining(" + maxRemaining + ") is negative");
        }
        return byteBuffer.limit(
                byteBuffer.position() + Math.min(byteBuffer.remaining(), maxRemaining)
        );
    }

    private HelloWorldByteBufferUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
