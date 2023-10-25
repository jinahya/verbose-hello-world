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

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;

@Slf4j
final class Rfc862Tcp3ServerAttachment extends _Rfc862Attachment.Server {

    Rfc862Tcp3ServerAttachment(final SelectionKey clientKey) {
        super();
        this.clientKey = Objects.requireNonNull(clientKey, "clientKey is null");
    }

    @Override
    public void close() throws IOException {
        clientKey.channel().close();
        assert !clientKey.isValid();
        super.close();
    }

    int read() throws IOException {
        assert !isClosed();
        assert clientKey.isValid();
        assert clientKey.isReadable();
        final var channel = (SocketChannel) clientKey.channel();
        final var r = channel.read(buffer);
        assert r >= -1;
        if (r == -1) {
            clientKey.interestOpsAnd(~SelectionKey.OP_READ);
        } else {
            increaseBytes(r);
            if (buffer.position() > 0) {
                clientKey.interestOpsOr(SelectionKey.OP_WRITE);
            }
        }
        return r;
    }

    int write() throws IOException {
        assert !isClosed();
        assert clientKey.isValid();
        assert clientKey.isWritable();
        final var channel = (SocketChannel) clientKey.channel();
        buffer.flip(); // limit -> position, position -> zero
        final var w = channel.write(buffer);
        updateDigest(w);
        buffer.compact();
        if (buffer.position() == 0 &&
            (clientKey.interestOps() & SelectionKey.OP_READ) != SelectionKey.OP_READ) {
            clientKey.interestOpsAnd(~SelectionKey.OP_WRITE);
            close();
        }
        return w;
    }

    private final SelectionKey clientKey;
}
