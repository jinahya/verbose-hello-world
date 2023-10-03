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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;

@Slf4j
final class Rfc863Tcp5ClientAttachment extends _Rfc863Attachment.Client {

    Rfc863Tcp5ClientAttachment(final AsynchronousChannelGroup group,
                               final AsynchronousSocketChannel client) {
        super();
        this.group = Objects.requireNonNull(group, "group is null");
        this.client = Objects.requireNonNull(client, "client is null");
    }

    // ------------------------------------------------------------------------- java.lang.Closeable

    @Override
    public void close() throws IOException {
        group.shutdownNow();
        super.close();
    }

    // -------------------------------------------------------------------------------------- client

    /**
     * Make {@code client} to connect to {@link _Rfc863Constants#ADDR}.
     *
     * @see AsynchronousSocketChannel#connect(SocketAddress, Object, CompletionHandler)
     */
    void connect() {
        client.connect(
                _Rfc863Constants.ADDR, // <remote>
                null,                  // <attachment>
                connected              // <handler>
        );
    }

    private void write() {
        final var buffer = getBufferForWriting();
        client.write(
                buffer,                              // <src>
                _Rfc86_Constants.WRITE_TIMEOUT,      // <timeout
                _Rfc86_Constants.WRITE_TIMEOUT_UNIT, // <unit>
                null,                                // <attachment>
                written                              // <handler>
        );
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousChannelGroup group;

    private final AsynchronousSocketChannel client;

    // @formatter:off
    private final CompletionHandler<Void, Void> connected = new CompletionHandler<>() {
        @Override public void completed(final Void result, final Void attachment) {
            _Rfc86_Utils.logConnected(client);
            write();
        }
        @Override public void failed(final Throwable exc, final Void attachment) {
            log.error("failed to connect", exc);
            closeUnchecked();
        }
    };
    // @formatter:on

    // @formatter:off
    private final CompletionHandler<Integer, Void> written = new CompletionHandler<>() {
        @Override public void completed(final Integer result, final Void attachment) {
            if (decreaseBytes(updateDigest(result)) == 0) { // no more bytes to send
                closeUnchecked();
                return;
            }
            write();
        }
        @Override public void failed(final Throwable exc, final Void attachment) {
            log.error("failed to write", exc);
            closeUnchecked();
        }
    };
    // @formatter:on
}
