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

import com.github.jinahya.hello.misc._Rfc86_Constants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

final class Rfc863Tcp4ServerAttachment extends _Rfc863Attachment.Server {

    Rfc863Tcp4ServerAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    /**
     * Reads a sequence of bytes, and returns a number of bytes read.
     *
     * @return the number of bytes read.
     * @throws Exception if any thrown.
     * @see #getBufferForReading()
     * @see AsynchronousSocketChannel#read(ByteBuffer)
     * @see _Rfc86_Constants#READ_TIMEOUT
     * @see _Rfc86_Constants#READ_TIMEOUT_UNIT
     * @see java.util.concurrent.Future#get(long, TimeUnit)
     * @see Rfc863Tcp4ClientAttachment#write()
     */
    int read() throws Exception {
        final var buffer = getBufferForReading();
        final var r = client.read(buffer).get(_Rfc86_Constants.READ_TIMEOUT,
                                              _Rfc86_Constants.READ_TIMEOUT_UNIT);
        assert r >= -1;
        assert r == -1 || r > 0; // why?
        if (r != -1) {
            increaseBytes(updateDigest(r));
        }
        return r;
    }

    private final AsynchronousSocketChannel client;
}
