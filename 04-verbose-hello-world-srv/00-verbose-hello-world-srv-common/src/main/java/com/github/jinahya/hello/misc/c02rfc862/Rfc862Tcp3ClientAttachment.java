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

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

final class Rfc862Tcp3ClientAttachment extends _Rfc862Attachment.Client {

    Rfc862Tcp3ClientAttachment(final SelectionKey clientKey) {
        super();
        this.clientKey = Objects.requireNonNull(clientKey, "clientKey is null");
    }

    int write() throws IOException {
        assert clientKey.isValid();
        assert clientKey.isWritable();
        final var channel = (SocketChannel) clientKey.channel();
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        final var w = channel.write(buffer);
        if (decreaseBytes(updateDigest(w)) == 0) {
            logDigest();
            channel.shutdownOutput();
            clientKey.interestOpsAnd(~SelectionKey.OP_WRITE);
            buffer.limit(buffer.capacity()).position(buffer.limit());
        }
        return w;
    }

    int read() throws IOException {
        assert clientKey.isValid();
        assert clientKey.isReadable();
        buffer.flip(); // limit -> position, position -> zero
        final var r = ((ReadableByteChannel) clientKey.channel()).read(buffer);
        buffer.position(buffer.limit()).limit(buffer.capacity());
        if (r == -1) {
            if (getBytes() > 0) {
                throw new EOFException("unexpected eof");
            }
            clientKey.interestOpsAnd(~SelectionKey.OP_READ);
            clientKey.cancel();
            assert !clientKey.isValid();
        }
        return r;
    }

    private final SelectionKey clientKey;
}
