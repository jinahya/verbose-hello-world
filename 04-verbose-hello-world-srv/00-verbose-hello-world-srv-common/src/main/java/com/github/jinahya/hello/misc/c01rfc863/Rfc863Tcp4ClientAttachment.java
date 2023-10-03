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

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;

final class Rfc863Tcp4ClientAttachment extends _Rfc863Attachment.Client {

    /**
     * Creates a new instance holding specified client.
     *
     * @param client the client to hold.
     */
    Rfc863Tcp4ClientAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    /**
     * Writes a sequence of bytes to {@code client}, and returns a number of bytes written.
     *
     * @return the number of bytes written to the {@code client}; {@code 0} when no bytes left to
     * send.
     * @throws Exception if any thrown.
     * @see AsynchronousSocketChannel#write(ByteBuffer)
     * @see Rfc863Tcp4ServerAttachment#read()
     */
    int write() throws Exception {
        final var buffer = getBufferForWriting();
        final var w = client.write(buffer).get(_Rfc86_Constants.WRITE_TIMEOUT,
                                               _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
        assert w >= 0;
        decreaseBytes(updateDigest(w));
        return w;
    }

    private final AsynchronousSocketChannel client;
}
