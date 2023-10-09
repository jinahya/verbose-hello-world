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

import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc862Tcp3ClientAttachment extends _Rfc862Attachment.Client {

    Rfc862Tcp3ClientAttachment(final SelectionKey clientKey) {
        super();
        this.clientKey = Objects.requireNonNull(clientKey, "clientKey is null");
    }

    @Override
    public void close() throws IOException {
        clientKey.channel().close();
        assert !clientKey.isValid();
        super.close();
    }

    /**
     * Writes a sequence of bytes to the {@code clientKey#channel}, and returns a number of bytes
     * written.
     *
     * @return a number of bytes written.
     * @throws IOException if an I/O error occurs.
     */
    int write() throws IOException {
        assert !isClosed();
        assert clientKey.isValid();
        assert clientKey.isWritable();
        final var channel = (SocketChannel) clientKey.channel();
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        final var w = channel.write(buffer);
        assert w >= 0;
        if (decreaseBytes(updateDigest(w)) == 0) {
            channel.shutdownOutput();
            clientKey.interestOpsAnd(~SelectionKey.OP_WRITE);
            buffer.limit(buffer.capacity()).position(buffer.limit());
        }
        return w;
    }

    /**
     * Reads a sequence of bytes from {@code clientKey#channel}, and returns a number of bytes
     * read.
     *
     * @return the number of bytes read.
     * @throws IOException if an I/O error occurs.
     */
    int read() throws IOException {
        assert !isClosed();
        assert clientKey.isValid();
        assert clientKey.isReadable();
        final var channel = (SocketChannel) clientKey.channel();
        buffer.flip(); // limit -> position, position -> zero
        final var r = channel.read(buffer);
        buffer.position(buffer.limit()).limit(buffer.capacity());
        if (r == -1) {
            if (getBytes() > 0) {
                throw new EOFException("unexpected eof");
            }
            clientKey.interestOpsAnd(~SelectionKey.OP_READ);
            close();
        }
        return r;
    }

    private final SelectionKey clientKey;
}
