package com.github.jinahya.hello.misc.c01rfc863;

import com.github.jinahya.hello.misc._AbstractRfc86_Attachment;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
abstract class _Rfc863Attachment extends _AbstractRfc86_Attachment {

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
            super(_Rfc86_Utils.randomBytes());
            _Rfc863Utils.logClientBytes(getBytes());
        }
    }

    /**
     * Creates a new instance.
     */
    private _Rfc863Attachment(final int bytes) {
        super(bytes, _Rfc863Constants.ALGORITHM);
    }

    @Override
    public void close() throws IOException {
        logDigest();
        super.close();
    }

    /**
     * Returns a buffer reading.
     *
     * @return a buffer for reading.
     */
    final ByteBuffer getBufferForReading() {
        final var buffer = getBuffer();
        if (!buffer.hasRemaining()) {
            buffer.clear();
        }
        return buffer;
    }

    /**
     * Returns a buffer for writing.
     *
     * @return a buffer for writing.
     */
    final ByteBuffer getBufferForWriting() {
        final var buffer = getBuffer();
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        return buffer;
    }
}
