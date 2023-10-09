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

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;

final class Rfc863Tcp3ServerAttachment extends _Rfc863Attachment.Server {

    Rfc863Tcp3ServerAttachment(final SelectionKey clientKey) {
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
        assert channel != null;
        assert !channel.isBlocking();
        if (!buffer.hasRemaining()) {
            buffer.clear();
        }
        final int r = channel.read(buffer);
        if (r == -1) {
            close();
        } else {
            assert r >= 0;
            increaseBytes(updateDigest(r));
        }
        return r;
    }

    private final SelectionKey clientKey;
}
