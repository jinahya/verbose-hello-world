package com.github.jinahya.hello.misc.c01rfc863;

import com.github.jinahya.hello.misc._Rfc86_Attachment;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
abstract class _Rfc863Attachment extends _Rfc86_Attachment {

    abstract static class Server extends _Rfc863Attachment {

        /**
         * Creates a new instance.
         */
        Server() {
            super(0);
        }

        @Override
        public void close() throws IOException {
            _Rfc863Utils.logServerBytes(bytes());
            super.close();
        }
    }

    abstract static class Client extends _Rfc863Attachment {

        /**
         * Creates a new instance.
         */
        Client() {
            super(_Rfc863Utils.randomBytes());
            _Rfc863Utils.logClientBytes(bytes());
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private _Rfc863Attachment(final int bytes) {
        super(bytes);
    }

    // --------------------------------------------------------------------------- java.io.Closeable

    @Override
    public void close() throws IOException {
        _Rfc863Utils.logDigest(digest);
        super.close();
    }

    // --------------------------------------------------------------------------------------- bytes

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
            buffer.clear().limit(Math.min(buffer.limit(), bytes()));
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
        updateDigest(bytes, digest);
    }

    // ---------------------------------------------------------------------------------------------

    private final MessageDigest digest = _Rfc863Utils.newDigest();
}
