package com.github.jinahya.hello.misc.c02rfc862;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Attachment;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
abstract class _Rfc862Attachment
        extends _Rfc86_Attachment {

    abstract static class Client
            extends _Rfc862Attachment {

        Client() {
            super(_Rfc862Utils.logClientBytes(_Rfc86_Utils.newRandomBytes()));
            buffer.position(buffer.limit());
        }

        @Override
        final ByteBuffer getBufferForReading() {
            return buffer.flip();
        }

        @Override
        final ByteBuffer getBufferForWriting() {
            if (!buffer.hasRemaining()) {
                ThreadLocalRandom.current().nextBytes(buffer.array());
                buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
            }
            assert buffer.hasRemaining();
            return buffer;
        }
    }

    abstract static class Server
            extends _Rfc862Attachment {

        Server() {
            super(0);
        }

        @Override
        public void close()
                throws IOException {
            _Rfc862Utils.logServerBytes(getBytes());
            super.close();
        }

        @Override
        final ByteBuffer getBufferForReading() {
            return buffer;
        }

        @Override
        final ByteBuffer getBufferForWriting() {
            return buffer.flip();
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private _Rfc862Attachment(final int bytes) {
        super(bytes, _Rfc862Constants.ALGORITHM, _Rfc862Constants.PRINTER);
    }

    // --------------------------------------------------------------------------- java.io.Closeable

    @Override
    public void close()
            throws IOException {
        logDigest();
        super.close();
    }

    // --------------------------------------------------------------------------------------- bytes

    // -------------------------------------------------------------------------------------- buffer

    abstract ByteBuffer getBufferForReading();

    //
    abstract ByteBuffer getBufferForWriting();

    // -------------------------------------------------------------------------------------- digest
}
