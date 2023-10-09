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
import java.util.concurrent.ThreadLocalRandom;

/**
 * An attachment class used by {@link Rfc863Tcp3Client}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class Rfc863Tcp3ClientAttachment extends _Rfc863Attachment.Client {

    Rfc863Tcp3ClientAttachment(final SelectionKey clientKey) {
        super();
        this.clientKey = Objects.requireNonNull(clientKey, "clientKey is null");
    }

    int write() throws IOException {
        assert !isClosed();
        assert clientKey.isValid();
        assert clientKey.isWritable();
        final var channel = (SocketChannel) clientKey.channel();
        assert channel != null;
        assert !channel.isBlocking();
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        final int w = channel.write(buffer);
        assert w >= 0;
        if (decreaseBytes(updateDigest(w)) == 0) { // no more bytes to send
            clientKey.cancel();
            assert !clientKey.isValid();
            close();
        }
        return w;
    }

    private final SelectionKey clientKey;
}
