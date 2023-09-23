package com.github.jinahya.hello.misc.c02rfc862;

import com.github.jinahya.hello.misc._AbstractRfc86_Attachment;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
abstract class _Rfc862Attachment extends _AbstractRfc86_Attachment {

    abstract static class Client extends _Rfc862Attachment {

        /**
         * Creates a new instance.
         */
        Client() {
            super(_Rfc86_Utils.randomBytes());
            getBuffer(b -> b.position(b.limit()));
        }

        /**
         * Returns the {@link #getBuffer() buffer} configured for reading.
         *
         * @return the {@link #getBuffer() buffer} with non-zero remaining.
         */
        ByteBuffer getBufferForReading() {
            final var buffer = getBuffer();
            if (!buffer.hasRemaining()) {
                buffer.clear();
            }
            assert buffer.hasRemaining();
            return buffer;
        }

        /**
         * Returns {@link #getBuffer() buffer} configured for writing.
         *
         * @return the {@link #getBuffer() buffer} with non-zero remaining.
         */
        final ByteBuffer getBufferForWriting() {
            final var buffer = getBuffer();
            if (!buffer.hasRemaining()) {
                ThreadLocalRandom.current().nextBytes(buffer.array());
                buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
            }
            assert buffer.hasRemaining();
            return buffer;
        }
    }

    abstract static class Server extends _Rfc862Attachment {

        /**
         * Creates a new instance.
         */
        Server() {
            super(0);
        }

        @Override
        public void close() throws IOException {
//            _Rfc862Utils.logServerBytes(getBytes());
            super.close();
        }

        /**
         * Returns {@link #getBuffer() buffer} configured for reading.
         *
         * @return the {@link #getBuffer() buffer} with non-zero remaining.
         */
        final ByteBuffer getBufferForReading() {
            final var buffer = getBuffer();
            if (!buffer.hasRemaining()) {
                buffer.clear();
            }
            assert buffer.hasRemaining();
            return buffer;
        }

        /**
         * Returns {@link #getBuffer() buffer} configured for writing.
         *
         * @return the {@link #getBuffer() buffer} with non-zero remaining.
         */
        final ByteBuffer getBufferForWriting() {
            final var buffer = getBuffer();
            if (!buffer.hasRemaining()) {
                ThreadLocalRandom.current().nextBytes(buffer.array());
                buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
            }
            assert buffer.hasRemaining();
            return buffer;
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private _Rfc862Attachment(final int bytes) {
        super(bytes, _Rfc862Constants.ALGORITHM);
    }

    // --------------------------------------------------------------------------- java.io.Closeable

    @Override
    public void close() throws IOException {
        super.close();
    }

    // --------------------------------------------------------------------------------------- bytes

    // -------------------------------------------------------------------------------------- buffer

    abstract ByteBuffer getBufferForReading();

    abstract ByteBuffer getBufferForWriting();
}
