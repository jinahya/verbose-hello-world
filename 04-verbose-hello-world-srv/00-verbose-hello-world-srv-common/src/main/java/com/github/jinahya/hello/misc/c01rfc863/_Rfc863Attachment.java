package com.github.jinahya.hello.misc.c01rfc863;

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
            assert buffer.hasRemaining() || getBytes() == 0;
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
         * Returns the {@code buffer} configured for reading.
         *
         * @return the {@code buffer} whose {@link ByteBuffer#remaining() remaining} is not zero.
         */
        final ByteBuffer getBufferForReading() {
            if (!buffer.hasRemaining()) {
                buffer.clear();
            }
            assert buffer.hasRemaining();
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
