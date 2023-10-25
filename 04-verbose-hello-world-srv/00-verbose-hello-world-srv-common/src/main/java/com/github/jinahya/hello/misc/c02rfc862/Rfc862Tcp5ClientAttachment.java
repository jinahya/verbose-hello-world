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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc862Tcp5ClientAttachment extends _Rfc862Attachment.Client {

    Rfc862Tcp5ClientAttachment(final AsynchronousChannelGroup group,
                               final AsynchronousSocketChannel client) {
        super();
        this.group = Objects.requireNonNull(group, "group is null");
        this.client = Objects.requireNonNull(client, "client is null");
    }

    @Override
    public void close() throws IOException {
        group.shutdownNow();
        super.close();
    }

    // @formatter:off
    void connect() {
        client.connect(
                _Rfc862Constants.ADDR,      // <remote>
                this,                       // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override
                    public void completed(final Void result,
                                          final Rfc862Tcp5ClientAttachment attachment) {
                        _TcpUtils.logConnectedUnchecked(client);
                        write();
                    }
                    @Override
                    public void failed(final Throwable exc,
                                       final Rfc862Tcp5ClientAttachment attachment) {
                        log.error("failed to connect", exc);
                        closeUnchecked();
                    }
                }
        );
    }
    // @formatter:on

    void write() {
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        client.write(
                buffer,                                // <src>
                _Rfc86_Constants.WRITE_TIMEOUT,        // <timeout>
                _Rfc86_Constants.WRITE_TIMEOUT_UNIT,   // <unit>
                this,                                  // <attachment>
                writer                                 // <handler>
        );
    }

    void read() {
        buffer.flip(); // limit -> position, position -> zero
        client.read(
                buffer,                             // <dst>
                _Rfc86_Constants.READ_TIMEOUT,      // <timeout>
                _Rfc86_Constants.READ_TIMEOUT_UNIT, // <unit>
                this,                               // <attachment>
                reader                              // <handler>
        );
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousChannelGroup group;

    private final AsynchronousSocketChannel client;

    // @formatter:off
    private final
    CompletionHandler<Integer, Rfc862Tcp5ClientAttachment> writer = new CompletionHandler<>() {
        @Override
        public void completed(final Integer result, final Rfc862Tcp5ClientAttachment attachment) {
            if (decreaseBytes(updateDigest(result)) == 0) { // all bytes have been sent
                logDigest();
                try {
                    client.shutdownOutput();
                } catch (final IOException ioe) {
                    log.error("failed to shutdown output", ioe);
                    closeUnchecked();
                }
            }
            read();
        }
        @Override
        public void failed(final Throwable exc, final Rfc862Tcp5ClientAttachment attachment) {
            log.error("failed to write", exc);
            closeUnchecked();
        }
    };
    // @formatter:on

    // @formatter:off
    private final
    CompletionHandler<Integer, Rfc862Tcp5ClientAttachment> reader = new CompletionHandler<>() {
        @Override
        public void completed(final Integer result, final Rfc862Tcp5ClientAttachment attachment) {
            buffer.position(buffer.limit()).limit(buffer.capacity());
            if (result == -1) {
                if (getBytes() > 0) { // not all bytes have been sent
                    throw new UncheckedIOException(new EOFException("unexpected eof"));
                }
                closeUnchecked();
                return;
            }
            if (getBytes() == 0) { // all bytes have been sent
                read();
                return;
            }
            write();
        }
        @Override
        public void failed(final Throwable exc, final Rfc862Tcp5ClientAttachment attachment) {
            log.error("failed to read", exc);
            attachment.closeUnchecked();
        }
    };
    // @formatter:on
}
