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
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc862Tcp2ClientAttachment extends _Rfc862Attachment.Client {

    Rfc862Tcp2ClientAttachment(final SocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    int write() throws IOException {
        assert client.isConnected();
        assert client.isOpen();
        assert client.socket().isConnected();
        assert !client.socket().isClosed();
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        int w;
        if (ThreadLocalRandom.current().nextBoolean()) {
            w = buffer.remaining();
            client.socket().getOutputStream().write(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position(),
                    buffer.limit()
            );
            client.socket().getOutputStream().flush();
            buffer.position(w);
        } else {
            w = client.write(buffer);
            assert w == buffer.position();
        }
        assert !buffer.hasRemaining();
        if (decreaseBytes(updateDigest(w)) == 0) {
            buffer.limit(buffer.capacity()).position(buffer.limit());
        }
        if (w == 0) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.socket().shutdownOutput();
            } else {
                client.shutdownOutput();
            }
        }
        return w;
    }

    int read() throws IOException {
        assert client.isConnected();
        assert client.isOpen();
        assert client.socket().isConnected();
        assert !client.socket().isClosed();
        int r;
        buffer.flip(); // limit -> position, position -> zero
        if (ThreadLocalRandom.current().nextBoolean()) {
            assert buffer.arrayOffset() == 0;
            r = client.socket().getInputStream().read(
                    buffer.array(),
                    0,
                    buffer.remaining()
            );
            if (r != -1) {
                buffer.position(r);
            }
        } else {
            r = client.read(buffer);
        }
        buffer.position(buffer.limit()).limit(buffer.capacity());
        if (r == -1 && getBytes() > 0) {
            throw new EOFException("unexpected eof");
        }
        return r;
    }

    private final SocketChannel client;
}
