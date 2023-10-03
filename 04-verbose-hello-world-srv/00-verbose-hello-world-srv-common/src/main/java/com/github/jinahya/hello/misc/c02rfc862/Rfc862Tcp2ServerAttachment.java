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
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc862Tcp2ServerAttachment extends _Rfc862Attachment.Server {

    Rfc862Tcp2ServerAttachment(final SocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    @Override
    public void close() throws IOException {
        client.close();
        assert client.socket().isClosed();
        super.close();
    }

    int read() throws IOException {
        assert client.isConnected();
        assert client.isOpen();
        assert client.socket().isConnected();
        assert !client.socket().isClosed();
        int r;
        if (ThreadLocalRandom.current().nextBoolean()) {
            assert buffer.arrayOffset() == 0;
            r = client.socket().getInputStream().read(
                    buffer.array(),
                    buffer.position(),
                    buffer.limit()
            );
            if (r != -1) {
                buffer.position(buffer.position() + r);
            }
        } else {
            r = client.read(buffer);
        }
        if (r == -1) {
            client.shutdownInput();
        } else {
            increaseBytes(r);
        }
        return r;
    }

    int write() throws IOException {
        assert client.isConnected();
        assert client.isOpen();
        assert client.socket().isConnected();
        assert !client.socket().isClosed();
        int w;
        buffer.flip();
        if (ThreadLocalRandom.current().nextBoolean()) {
            w = buffer.remaining();
            client.socket().getOutputStream().write(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position(),
                    buffer.remaining()
            );
            buffer.position(buffer.limit());
        } else {
            w = client.write(buffer);
        }
        assert !buffer.hasRemaining();
        updateDigest(w);
        buffer.compact();
        assert buffer.position() == 0;
        return w;
    }

    private final SocketChannel client;
}
