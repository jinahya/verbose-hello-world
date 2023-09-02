package com.github.jinahya.hello.misc.c03chat;

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

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritePendingException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp3Client {

    // @formatter:off
    private static class ChatTcp3ClientAttachment extends ChatTcp3Server.ChatTcp3ServerAttachment {
        public ChatTcp3ClientAttachment(AsynchronousSocketChannel client,
                                        SubmissionPublisher<ByteBuffer> publisher) {
            super(client, publisher);
        }
        @Override public void onNext(ByteBuffer item) {
            Objects.requireNonNull(item, "item is null");
            Objects.requireNonNull(item, "item is null");
            buffers.add(item);
            try {
                client.write(buffers.get(0), this, W_HANDLER);
            } catch (WritePendingException wpe) {
                // empty
            }
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, ChatTcp3ClientAttachment> W_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, ChatTcp3ClientAttachment attachment) {
            assert !attachment.buffers.isEmpty();
            var buffer = attachment.buffers.get(0);
            if (!buffer.hasRemaining()) {
                attachment.buffers.remove(0);
            }
            if (!attachment.buffers.isEmpty()) {
                attachment.client.write(
                        attachment.buffers.get(0), // <src>
                        attachment,                // <attachment>
                        this                       // <handler>
                );
            }
        }
        @Override public void failed(Throwable exc, ChatTcp3ClientAttachment attachment) {
            log.error("failed to write", exc);
            attachment.closeUnchecked();
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, ChatTcp3ClientAttachment> R_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, ChatTcp3ClientAttachment attachment) {
            if (result == -1) {
                attachment.closeUnchecked();
                return;
            }
            if (!attachment.buffer.hasRemaining()) {
                _ChatMessage.OfBuffer.printToSystemOut(attachment.buffer);
                attachment.buffer.clear();
            }
            attachment.client.read(attachment.buffer, attachment, this);
        }
        @Override public void failed(Throwable exc, ChatTcp3ClientAttachment attachment) {
            log.error("failed to read", exc);
            attachment.closeUnchecked();
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            addr = InetAddress.getLoopbackAddress();
        }
        var group = AsynchronousChannelGroup.withThreadPool(
                Executors.newCachedThreadPool());
        try (var client = AsynchronousSocketChannel.open();
             var publisher = new SubmissionPublisher<ByteBuffer>()) {
            client.connect(
                    new InetSocketAddress(addr, _ChatConstants.PORT),
                    new ChatTcp3ClientAttachment(client, publisher),
                    new CompletionHandler<>() { // @formatter:on
                        @Override
                        public void completed(Void result,
                                              ChatTcp3ClientAttachment attachment) {
                            try {
                                log.debug("connected to {}, through {}",
                                          attachment.client.getRemoteAddress(),
                                          attachment.client.getLocalAddress());
                            } catch (IOException ioe) {
                                log.error("failed to get addresses from {}",
                                          attachment.client,
                                          ioe);
                            }
                            HelloWorldLangUtils.readLinesAndCallWhenTests(
                                    HelloWorldServerUtils::isQuit,
                                    // <predicate>
                                    () -> {                        // <callable>
                                        group.shutdownNow();
                                        return null;
                                    },
                                    l -> {                         // <consumer>
                                        var message = _ChatUtils.prependUsername(
                                                l);
                                        var buffer = _ChatMessage.OfBuffer.of(
                                                message);
                                        attachment.publisher.submit(buffer);
                                    }
                            );
                            attachment.publisher.subscribe(attachment);
                            attachment.client.read(attachment.buffer,
                                                   attachment, R_HANDLER);
                        }

                        @Override
                        public void failed(Throwable exc,
                                           ChatTcp3ClientAttachment attachment) {
                            log.error("failed to accept", exc);
                            try {
                                group.shutdownNow();
                            } catch (IOException ioe) {
                                log.error("failed shutdown " + group, ioe);
                            }
                        } // @formatter:off
                    });
            while (!group.awaitTermination(8L, TimeUnit.SECONDS)) {
                // does nothing but just waiting...
            }
        }
    }

    private ChatTcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
