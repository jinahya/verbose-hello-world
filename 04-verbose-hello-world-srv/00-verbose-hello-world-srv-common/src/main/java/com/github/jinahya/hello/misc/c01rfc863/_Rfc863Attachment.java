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

    protected abstract static class Server extends _Rfc863Attachment {

        Server() {
            super(0);
        }
    }

    protected abstract static class Client extends _Rfc863Attachment {

        Client() {
            super(_Rfc863Utils.newBytesLessThanMillion());
            buffer.position(buffer.limit());
            logClientBytes();
        }
    }

    /**
     * Creates a new instance.
     */
    _Rfc863Attachment(final int bytes) {
        super();
        this.bytes = bytes;
    }

    // --------------------------------------------------------------------------- java.io.Closeable
    @Override
    public void close() throws IOException {
        // empty
    }

    final void closeUnchecked() {
        try {
            close();
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    // --------------------------------------------------------------------------------------- bytes
    int getBytes() {
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

    int increaseBytes(final int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta(" + delta + ") < 0");
        }
        return bytes(getBytes() + delta);
    }

    int decreaseBytes(final int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta(" + delta + ") < 0");
        }
        return bytes(getBytes() - delta);
    }

    void logClientBytes() {
        _Rfc863Utils.logClientBytes(bytes);
    }

    void logServerBytes() {
        _Rfc863Utils.logServerBytes(bytes);
    }

    // -------------------------------------------------------------------------------------- buffer
    ByteBuffer getBufferForReading() {
        if (!buffer.hasRemaining()) {
            buffer.clear();
        }
        assert buffer.hasRemaining();
        return buffer;
    }

    ByteBuffer getBufferForWriting() {
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
     * @return given {@code bytes}.
     */
    int updateDigest(final int bytes) {
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

    /**
     * Logs out the final result of {@code digest}.
     *
     * @see _Rfc863Utils#logDigest(MessageDigest)
     */
    void logDigest() {
        _Rfc863Utils.logDigest(digest);
    }

    // ---------------------------------------------------------------------------------------------
    private int bytes; // bytes to send or received

    final ByteBuffer buffer = _Rfc863Utils.newBuffer();

    final ByteBuffer slice = buffer.slice();

    final MessageDigest digest = _Rfc863Utils.newDigest();
}
