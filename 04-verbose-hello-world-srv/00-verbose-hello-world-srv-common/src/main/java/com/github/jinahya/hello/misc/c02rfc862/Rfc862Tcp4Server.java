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
import java.io.UncheckedIOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp4Server {

    // @formatter:off
    static class Attachment extends Rfc862Tcp3Server.Attachment {
        void shutdownGroupNow() {
            if (group == null) {
                throw new IllegalStateException("group is null");
            }
            try {
                group.shutdownNow();
            } catch (IOException ioe) {
                throw new UncheckedIOException("failed to shutdown group: " + group, ioe);
            }
        }
        AsynchronousChannelGroup group;
        AsynchronousSocketChannel client;
    }
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> W_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            attachment.digest.update(
                    attachment.slice
                            .position(attachment.buffer.position() - result)
                            .limit(attachment.buffer.position())
            );
            attachment.client.read(
                    attachment.buffer.compact(), // <dst>
                    8L, TimeUnit.SECONDS,
                    attachment,                  // <attachment>
                    R_HANDLER                    // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to write", exc);
            attachment.shutdownGroupNow();
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            if (result == -1) {
                if (attachment.buffer.position() == 0) { // no more bytes to send back, either
                    try {
                        attachment.client.close();
                    } catch (IOException ioe) {
                        log.error("failed to close {}", attachment.client, ioe);
                    }
                    attachment.shutdownGroupNow();
                    return;
                }
            } else {
                attachment.bytes += result;
            }
            attachment.client.write(
                    attachment.buffer.flip(), // <src>
                    8L, TimeUnit.SECONDS,
                    attachment,               // <attachment>
                    W_HANDLER                 // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to read", exc);
            attachment.shutdownGroupNow();
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<AsynchronousSocketChannel, Attachment> A_HANDLER = new CompletionHandler<>() {
        @Override public void completed(AsynchronousSocketChannel result, Attachment attachment) {
            try {
                log.info("accepted from {}, through {}", result.getRemoteAddress(),
                         result.getLocalAddress());
            } catch (IOException ioe) {
                log.error("failed get addresses from {}", result, ioe);
            }
            attachment.client = result;
            attachment.client.read(
                    attachment.buffer, // <dst>
                    8L, TimeUnit.SECONDS,
                    attachment,        // <attachment>
                    R_HANDLER          // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to accept", exc);
            attachment.shutdownGroupNow();
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        var group = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool());
        try (var server = AsynchronousServerSocketChannel.open(group)) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            var attachment = new Attachment();
            attachment.group = group;
            server.accept(
                    attachment, // <attachment>
                    A_HANDLER   // <handler>
            );
            if (!group.awaitTermination(8L, TimeUnit.SECONDS)) {
                log.error("channel group has not been terminated for a while");
            }
            _Rfc862Utils.logServerBytes(attachment.bytes);
            _Rfc862Utils.logDigest(attachment.digest);
        }
    }

    private Rfc862Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
