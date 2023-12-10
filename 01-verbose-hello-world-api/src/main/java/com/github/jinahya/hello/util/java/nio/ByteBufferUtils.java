package com.github.jinahya.hello.util.java.nio;

import com.github.jinahya.hello.util.java.lang.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Random;

public final class ByteBufferUtils {

    public static ByteBuffer randomized(final ByteBuffer buffer, final Random random) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(random, "random is null");
        if (buffer.hasArray()) {
            ArrayUtils.randomize(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position(),
                    buffer.remaining(),
                    random
            );
            return buffer;
        }
        final var src = new byte[buffer.remaining()];
        ArrayUtils.randomize(
                src,
                0,
                src.length,
                random
        );
        return buffer.put(buffer.position(), src);
    }

    private ByteBufferUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
