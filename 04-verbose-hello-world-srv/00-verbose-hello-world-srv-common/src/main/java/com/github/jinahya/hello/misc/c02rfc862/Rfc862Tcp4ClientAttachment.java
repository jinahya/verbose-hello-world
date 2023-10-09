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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc862Tcp4ClientAttachment extends _Rfc862Attachment.Client {

    Rfc862Tcp4ClientAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    // -------------------------------------------------------------------------------------- client
    int write() throws Exception {
        assert client.isOpen();
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        assert buffer.hasRemaining() || getBytes() == 0;
        final var w = client.write(buffer)
                .get(_Rfc86_Constants.WRITE_TIMEOUT, _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
        assert w > 0 || getBytes() == 0;
        if (decreaseBytes(updateDigest(w)) == 0) {
            buffer.clear().position(buffer.limit());
        }
        return w;
    }

    int read() throws Exception {
        assert client.isOpen();
        buffer.flip(); // limit -> position, position -> zero
        final var r = client.read(buffer)
                .get(_Rfc86_Constants.READ_TIMEOUT, _Rfc86_Constants.READ_TIMEOUT_UNIT);
        assert r >= -1;
        buffer.position(buffer.limit()).limit(buffer.capacity());
        if (r == -1 && getBytes() > 0) {
            throw new EOFException("unexpected eof");
        }
        return r;
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousSocketChannel client;
}
