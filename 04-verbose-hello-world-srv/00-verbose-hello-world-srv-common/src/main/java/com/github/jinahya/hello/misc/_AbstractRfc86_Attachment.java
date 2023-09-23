package com.github.jinahya.hello.misc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
public abstract class _AbstractRfc86_Attachment
        implements _Rfc86_Attachment {

    /**
     * Creates a new instance with specified initial value for {@code bytes}.
     *
     * @param bytes     the initial value for the {@code bytes} property.
     * @param algorithm an algorithm for {@link MessageDigest}.
     */
    protected _AbstractRfc86_Attachment(final int bytes, final String algorithm) {
        super();
        this.bytes = bytes;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException nsae) {
            throw new IllegalArgumentException("unknown algorithm", nsae);
        }
    }

    // --------------------------------------------------------------------------- java.io.Closeable

    @Override
    public void close() throws IOException {
        // empty
    }

    @Override
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
    @Override
    public final int getBytes() {
        return bytes;
    }

    @Override
    public final int increaseBytes(final int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta(" + delta + ") < 0");
        }
        bytes += delta;
        return bytes;
    }

    @Override
    public final int decreaseBytes(final int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta(" + delta + ") < 0");
        }
        if (delta > bytes) {
            throw new IllegalArgumentException("delta(" + delta + ") > bytes(" + bytes + ")");
        }
        bytes -= delta;
        return bytes;
    }

    // -------------------------------------------------------------------------------------- buffer
    protected final ByteBuffer getBuffer() {
        return buffer;
    }

    protected ByteBuffer getBuffer(final Consumer<? super ByteBuffer> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(buffer);
        return getBuffer();
    }

    // -------------------------------------------------------------------------------------- digest

    @Override
    public int updateDigest(final int bytes) {
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
        return bytes;
    }

    @Override
    public final void logDigest() {
        _Rfc86_Utils.logDigest(digest, b -> HexFormat.of().formatHex(b));
    }

    // ---------------------------------------------------------------------------------------------
    private int bytes; // bytes to send or received

    private final ByteBuffer buffer = _Rfc86_Utils.newBuffer();

    private final ByteBuffer slice = buffer.slice();

    private final MessageDigest digest;
}
