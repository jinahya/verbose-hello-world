package com.github.jinahya.hello.misc.c01rfc863.real;

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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc863TcpNonBlockingClientAttachment {

    Rfc863TcpNonBlockingClientAttachment(final SelectionKey clientKey, final Semaphore semaphore) {
        super();
        this.clientKey = Objects.requireNonNull(clientKey, "clientKey is null");
        this.semaphore = Objects.requireNonNull(semaphore, "semaphore is null");
        buffer = ByteBuffer.allocate(_Rfc863Constants.CLIENT_BYTES);
        buffer.position(buffer.limit());
    }

    int write() throws IOException {
        assert clientKey.isWritable();
        final var channel = (WritableByteChannel) clientKey.channel();
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), bytes));
        }
        if (!buffer.hasRemaining()) {
            assert bytes == 0;
            clientKey.interestOpsAnd(~SelectionKey.OP_WRITE);
            channel.close();
            assert !clientKey.isValid();
            semaphore.release();
            return 0;
        }
        final var w = channel.write(buffer);
        bytes -= w;
        return w;
    }

    private final SelectionKey clientKey;

    private final Semaphore semaphore;

    private int bytes = _Rfc863Constants.CLIENT_BYTES;

    private final ByteBuffer buffer;
}
