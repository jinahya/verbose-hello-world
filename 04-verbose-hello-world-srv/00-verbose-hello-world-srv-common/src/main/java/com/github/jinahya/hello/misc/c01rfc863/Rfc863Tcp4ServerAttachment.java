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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

final class Rfc863Tcp4ServerAttachment extends _Rfc863Attachment.Server {

    /**
     * Creates a new instance with specified client.
     *
     * @param client the client.
     */
    Rfc863Tcp4ServerAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    // --------------------------------------------------------------------------- java.io.Closeable
    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    // -------------------------------------------------------------------------------------- client

    /**
     * Reads a sequence of bytes, and returns the number of bytes read.
     *
     * @return the number of bytes read.
     * @throws ExecutionException   if failed to read.
     * @throws InterruptedException when interrupted while getting the result of the read
     *                              operation.
     * @throws TimeoutException     when timed out while getting the result of the read operation.
     * @see #getBufferForReading()
     * @see AsynchronousSocketChannel#read(ByteBuffer)
     * @see _Rfc86_Constants#READ_TIMEOUT
     * @see _Rfc86_Constants#READ_TIMEOUT_UNIT
     * @see java.util.concurrent.Future#get(long, TimeUnit)
     * @see Rfc863Tcp4ClientAttachment#write()
     */
    int read() throws ExecutionException, InterruptedException, TimeoutException {
        assert !isClosed();
        if (!buffer.hasRemaining()) {
            buffer.clear();
        }
        final var r = client.read(buffer).get(_Rfc86_Constants.READ_TIMEOUT,
                                              _Rfc86_Constants.READ_TIMEOUT_UNIT);
        assert r >= -1;
        assert r == -1 || r > 0; // why?
        if (r != -1) {
            increaseBytes(updateDigest(r));
        }
        return r;
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousSocketChannel client;
}
