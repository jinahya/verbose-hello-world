package com.github.jinahya.hello.miscellaneous.c02rfc862;

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

import com.github.jinahya.hello.util.HelloWorldSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.MessageDigest;
import java.util.concurrent.CountDownLatch;

@Slf4j
class Rfc862Tcp4Server {

    static class Attachment {

        AsynchronousSocketChannel client;

        final CountDownLatch latch = new CountDownLatch(3);

        final ByteBuffer buffer = _Rfc862Utils.newBuffer();

        int bytes;

        final MessageDigest digest = _Rfc862Utils.newDigest();
    }

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> W_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            HelloWorldSecurityUtils.updatePreceding(attachment.digest, attachment.buffer, result);
            if (attachment.latch.getCount() == 1) { // all already received
                if (attachment.buffer.hasRemaining()) {
                    attachment.client.write(
                            attachment.buffer, // <src>
                            attachment,        // <attachment>
                            this               // <handler>
                    );
                    return;
                }
                attachment.latch.countDown(); // -1 for all sent
                try {
                    attachment.client.shutdownOutput();
                } catch (IOException ioe) {
                    failed(ioe, attachment);
                }
                return;
            }
            attachment.client.read(
                    attachment.buffer.compact(), // <dst>
                    attachment,                  // <attachment>
                    R_HANDLER                    // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to write", exc);
            while(attachment.latch.getCount() > 0L) {
                attachment.latch.countDown();
            }
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            if (result == -1) {
                attachment.latch.countDown(); // -1 for all received
                if (attachment.buffer.position() == 0) { // no more bytes to write
                    attachment.latch.countDown(); // -1 for all written
                    try {
                        attachment.client.close();
                    } catch (IOException ioe) {
                        failed(ioe, attachment);
                    }
                    return;
                }
            } else {
                attachment.bytes += result;
            }
            attachment.client.write(
                    attachment.buffer.flip(), // <src>
                    attachment,               // <attachment>
                    W_HANDLER                 // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to read", exc);
            while(attachment.latch.getCount() > 0L) {
                attachment.latch.countDown();
            }
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<AsynchronousSocketChannel, Attachment> A_HANDLER = new CompletionHandler<>() {
        @Override public void completed(AsynchronousSocketChannel result, Attachment attachment) {
            try {
                log.debug("accepted from {}, through {}", result.getRemoteAddress(),
                          result.getLocalAddress());
            } catch (IOException ioe) {
                failed(ioe, attachment);
                return;
            }
            attachment.latch.countDown(); // -1 for being accepted
            attachment.client = result;
            attachment.client.read(
                    attachment.buffer,    // <dst>
                    attachment,           // <attachment>
                    R_HANDLER             // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to accept", exc);
            while(attachment.latch.getCount() > 0L) {
                attachment.latch.countDown();
            }
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(_Rfc862Constants.ADDRESS);
            log.debug("bound to {}", server.getLocalAddress());
            var attachment = new Attachment();
            server.accept(
                    attachment, // <attachment>
                    A_HANDLER   // <handler>
            );
            attachment.latch.await();
            _Rfc862Utils.logServerBytesSent(attachment.bytes);
            _Rfc862Utils.logDigest(attachment.digest);
        }
    }

    private Rfc862Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
