package com.github.jinahya.hello.misc;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.function.Consumer;

@Slf4j
public abstract class _Rfc86_Attachment implements Closeable {

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with specified initial value for {@code bytes}.
     *
     * @param bytes the initial value for the {@code bytes} property.
     */
    protected _Rfc86_Attachment(final int bytes) {
        super();
        this.bytes = bytes;
    }

    // --------------------------------------------------------------------------- java.io.Closeable

    @Override
    public void close() throws IOException {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("already closed");
            }
            closed = true;
        }
    }

    public final void closeUnchecked() {
        try {
            close();
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    // --------------------------------------------------------------------------------------- bytes

    /**
     * Returns current value of {@link #bytes} property.
     *
     * @return current value of {@link #bytes} property.
     */
    public final int bytes() {
        return bytes;
    }

    /**
     * Replaces current value of {@code bytes} property with specified value, and returns updated
     * value of the {@code bytes} property.
     *
     * @param bytes new value for the {@code bytes} property.
     * @return updated value of the {@code bytes} property.
     */
    private int bytes(final int bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") < 0");
        }
        this.bytes = bytes;
        return bytes();
    }

    /**
     * Increases current value of {@code bytes} property by specified value, and returns updated
     * value of the {@code bytes} property.
     *
     * @param delta delta value for the {@code bytes} property; should be greater than or equal to
     *              zero.
     * @return updated value of the {@code bytes} property.
     */
    public final int increaseBytes(final int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta(" + delta + ") < 0");
        }
        return bytes(bytes() + delta);
    }

    /**
     * Decreases current value of {@code bytes} property by specified value, and returns updated
     * value of the {@code bytes} property.
     *
     * @param delta delta value for the {@code bytes} property; should be greater than or equal to
     *              zero.
     * @return updated value of the {@code bytes} property.
     */
    public final int decreaseBytes(final int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta(" + delta + ") < 0");
        }
        return bytes(bytes() - delta);
    }

    // -------------------------------------------------------------------------------------- buffer
    public final ByteBuffer buffer() {
        return buffer;
    }

    public ByteBuffer buffer(final Consumer<? super ByteBuffer> consumer) {
        consumer.accept(buffer);
        return buffer;
    }

    // -------------------------------------------------------------------------------------- digest

    protected void updateDigest(final int bytes, final MessageDigest digest) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        if (bytes > buffer.position()) {
            throw new IllegalArgumentException(
                    "bytes(" + bytes + ") > buffer.position(" + buffer.position() + ")");
        }
        digest.update(
                slice.position(buffer.position() - bytes)
                        .limit(buffer.position())
        );
    }

    // ---------------------------------------------------------------------------------------------
    private int bytes; // bytes to send or received

    protected final ByteBuffer buffer = _Rfc86_Utils.newBuffer();

    private final ByteBuffer slice = buffer.slice();

    private volatile boolean closed = false;
}
