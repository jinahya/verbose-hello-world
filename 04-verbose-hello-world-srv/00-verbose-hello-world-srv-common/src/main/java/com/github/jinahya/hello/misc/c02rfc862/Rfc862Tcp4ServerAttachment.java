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

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;

class Rfc862Tcp4ServerAttachment extends _Rfc862Attachment.Server {

    Rfc862Tcp4ServerAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    int read() throws Exception {
        final var r = client.read(buffer)
                .get(_Rfc86_Constants.READ_TIMEOUT, _Rfc86_Constants.READ_TIMEOUT_UNIT);
        if (r != -1) {
            increaseBytes(r);
        }
        return r;
    }

    int write() throws Exception {
        buffer.flip();
        final var w = client.write(buffer)
                .get(_Rfc86_Constants.WRITE_TIMEOUT, _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
        updateDigest(w);
        buffer.compact();
        return w;
    }

    private final AsynchronousSocketChannel client;
}
