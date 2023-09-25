package com.github.jinahya.hello.misc.c01rfc863;

import com.github.jinahya.hello.misc._Rfc86_Attachment;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings({
        "java:S101" // _Rfc863...
})
abstract class _Rfc863Attachment extends _Rfc86_Attachment {

    abstract static class Client extends _Rfc863Attachment {

        Client() {
            super(_Rfc86_Utils.randomBytes());
            _Rfc863Utils.logClientBytes(getBytes());
            buffer.position(buffer.limit());
        }

        final ByteBuffer getBufferForWriting() {
            if (!buffer.hasRemaining()) {
                ThreadLocalRandom.current().nextBytes(buffer.array());
                buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
            }
            return buffer;
        }
    }

    abstract static class Server extends _Rfc863Attachment {

        Server() {
            super(0);
        }

        /**
         * {@inheritDoc}
         *
         * @throws IOException {@inheritDoc}
         */
        @Override
        public void close() throws IOException {
            _Rfc863Utils.logServerBytes(getBytes());
            super.close();
        }

        /**
         * Returns the {@code buffer} property configured for reading.
         *
         * @return the {@code buffer} property configured for reading.
         */
        final ByteBuffer getBufferForReading() {
            if (!buffer.hasRemaining()) {
                buffer.clear();
            }
            return buffer;
        }
    }

    private _Rfc863Attachment(final int bytes) {
        super(bytes, _Rfc863Constants.ALGORITHM, _Rfc863Constants.PRINTER);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException {@inheritDoc}.
     */
    @Override
    public void close() throws IOException {
        logDigest();
        super.close();
    }
}
