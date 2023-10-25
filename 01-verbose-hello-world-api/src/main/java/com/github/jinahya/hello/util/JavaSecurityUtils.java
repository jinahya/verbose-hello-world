package com.github.jinahya.hello.util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class JavaSecurityUtils {

    // ------------------------------------------------------------------------------- MessageDigest

    /**
     * Updates specified message digest with specified number of bytes preceding specified byte
     * buffer's current position.
     *
     * @param digest the message digest to update.
     * @param buffer the byte buffer whose bytes are updated to the {@code digest}.
     * @param bytes  the number of bytes preceding the {@code buffer}'s current {@code position} to
     *               be updated to the {@code digest}.
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
        if (ThreadLocalRandom.current().nextBoolean()) {
            digest.update(buffer.slice(buffer.position() - bytes, bytes));
            return;
        }
        final var position = buffer.position();
        final var limit = buffer.limit();
        buffer.position(position - bytes).limit(position);
        digest.update(buffer);
        buffer.position(position).limit(limit);
    }

    // ---------------------------------------------------------------------------------------------
    private JavaSecurityUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
