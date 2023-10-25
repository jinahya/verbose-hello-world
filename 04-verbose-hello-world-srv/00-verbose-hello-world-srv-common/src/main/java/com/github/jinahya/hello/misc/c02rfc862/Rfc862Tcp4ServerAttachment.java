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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
class Rfc862Tcp4ServerAttachment extends _Rfc862Attachment.Server {

    /**
     * Creates a new instance with specified asynchronous socket channel.
     *
     * @param client the asynchronous socket channel.
     */
    Rfc862Tcp4ServerAttachment(final AsynchronousSocketChannel client) {
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
    int read() throws ExecutionException, InterruptedException, TimeoutException {
        final var r = client.read(buffer)
                .get(_Rfc86_Constants.READ_TIMEOUT, _Rfc86_Constants.READ_TIMEOUT_UNIT);
        if (r != -1) {
            increaseBytes(r);
        }
        return r;
    }

    int write() throws ExecutionException, InterruptedException, TimeoutException {
        buffer.flip();
        final var w = client.write(buffer)
                .get(_Rfc86_Constants.WRITE_TIMEOUT, _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
        updateDigest(w);
        buffer.compact();
        return w;
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousSocketChannel client;
}
