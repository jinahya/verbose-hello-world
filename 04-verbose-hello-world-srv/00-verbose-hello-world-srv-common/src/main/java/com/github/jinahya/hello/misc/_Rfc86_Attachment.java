package com.github.jinahya.hello.misc;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

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
        // empty
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
    public final int getBytes() {
        return bytes;
    }

    /**
     * Replaces current value of {@code bytes} property with specified value.
     *
     * @param bytes new value for the {@code bytes} property.
     */
    private void setBytes(final int bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") < 0");
        }
        this.bytes = bytes;
    }

    /**
     * Replaces current value of {@code bytes} property with specified value, and returns updated
     * value of the {@code bytes} property.
     *
     * @param bytes new value for the {@code bytes} property.
     * @return updated value of the {@code bytes} property.
     */
    private int bytes(final int bytes) {
        setBytes(bytes);
        return getBytes();
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
        return bytes(getBytes() + delta);
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
        return bytes(getBytes() - delta);
    }

    // -------------------------------------------------------------------------------------- buffer

//    /**
//     * Returns {@link #buffer} configured for reading.
//     *
//     * @return the {@link #buffer} with non-zero remaining.
//     */
//    final ByteBuffer getBufferForReading() {
//        if (!buffer.hasRemaining()) {
//            buffer.clear();
//        }
//        assert buffer.hasRemaining();
//        return buffer;
//    }
//
//    /**
//     * Returns {@link #buffer} configured for writing.
//     *
//     * @return the {@link #buffer} with non-zero remaining.
//     */
//    final ByteBuffer getBufferForWriting() {
//        if (!buffer.hasRemaining()) {
//            ThreadLocalRandom.current().nextBytes(buffer.array());
//            buffer.clear().limit(Math.min(buffer.limit(), bytes));
//        }
//        assert buffer.hasRemaining();
//        return buffer;
//    }

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
}
