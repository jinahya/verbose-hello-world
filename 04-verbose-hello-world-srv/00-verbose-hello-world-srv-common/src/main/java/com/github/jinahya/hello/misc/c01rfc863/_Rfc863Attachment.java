package com.github.jinahya.hello.misc.c01rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
abstract class _Rfc863Attachment implements Closeable {

    abstract static class Server extends _Rfc863Attachment {

        /**
         * Creates a new instance.
         */
        Server() {
            super(0);
        }

        @Override
        public void close() throws IOException {
            _Rfc863Utils.logServerBytes(getBytes());
            super.close();
        }
    }

    abstract static class Client extends _Rfc863Attachment {

        /**
         * Creates a new instance.
         */
        Client() {
            super(_Rfc863Utils.newBytesSome());
            _Rfc863Utils.logClientBytes(getBytes());
        }
    }

    /**
     * Creates a new instance.
     */
    private _Rfc863Attachment(final int bytes) {
        super();
        this.bytes = bytes;
    }

    // --------------------------------------------------------------------------- java.io.Closeable

    @Override
    public void close() throws IOException {
        _Rfc863Utils.logDigest(digest);
    }

    final void closeUnchecked() {
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
    final int getBytes() {
        return bytes;
    }

    private void setBytes(final int bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") < 0");
        }
        this.bytes = bytes;
    }

    private int bytes(final int bytes) {
        setBytes(bytes);
        return getBytes();
    }

    final int increaseBytes(final int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta(" + delta + ") < 0");
        }
        return bytes(getBytes() + delta);
    }

    final int decreaseBytes(final int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta(" + delta + ") < 0");
        }
        return bytes(getBytes() - delta);
    }

    // -------------------------------------------------------------------------------------- buffer

    /**
     * Returns {@link #buffer} configured for reading.
     *
     * @return the {@link #buffer} with non-zero remaining.
     */
    final ByteBuffer getBufferForReading() {
        if (!buffer.hasRemaining()) {
            buffer.clear();
        }
        assert buffer.hasRemaining();
        return buffer;
    }

    /**
     * Returns {@link #buffer} configured for writing.
     *
     * @return the {@link #buffer} with non-zero remaining.
     */
    final ByteBuffer getBufferForWriting() {
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), bytes));
        }
        assert buffer.hasRemaining();
        return buffer;
    }

    // -------------------------------------------------------------------------------------- digest

    /**
     * Updates specified number of bytes preceding current position of {@code buffer} to
     * {@code digest}.
     *
     * @param bytes the number of bytes preceding current position of the {@code buffer} to be
     *              updated to the {@code digest}.
     */
    final void updateDigest(final int bytes) {
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

    private final ByteBuffer buffer = _Rfc863Utils.newBuffer();

    private final ByteBuffer slice = buffer.slice();

    private final MessageDigest digest = _Rfc863Utils.newDigest();
}
