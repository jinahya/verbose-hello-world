package com.github.jinahya.hello._04_java_nio;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long, Object, CompletionHandler) write(channel,
 * position, handler, attachment)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_22_Append_Path_Using_AsynchronousFileChannelWithHandler_Test
        extends HelloWorldTest {

    @Test
    void __(@TempDir final Path tempDir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // service.put(buffer) will increase the buffer's position by HelloWorld.BYTES
        BDDMockito.willAnswer(i -> {
                    final var channel = i.getArgument(0, AsynchronousFileChannel.class);
                    final var position = new AtomicLong(i.getArgument(1, Long.class));
                    final var attachment = i.getArgument(2);
                    @SuppressWarnings({"unchecked"})
                    final var handler = (CompletionHandler<AsynchronousFileChannel, Object>)
                            i.getArgument(3, CompletionHandler.class);
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    channel.write(
                            buffer,
                            position.get(),
                            attachment,
                            new CompletionHandler<>() {
                                @Override
                                public void completed(Integer result, Object a) {
                                    position.addAndGet(result);
                                    if (buffer.hasRemaining()) {
                                        channel.write(buffer, position.get(), attachment, this);
                                        return;
                                    }
                                    handler.completed(channel, attachment);
                                }

                                @Override
                                public void failed(Throwable exc, Object a) {
                                    handler.failed(exc, a);
                                }
                            }
                    );
                    return channel;
                })
                .given(service)
                .write(ArgumentMatchers.notNull(),
                       ArgumentMatchers.longThat(v -> v >= 0L),
                       ArgumentMatchers.any(),
                       ArgumentMatchers.notNull()
                );
        final var position = ThreadLocalRandom.current().nextLong(1024L);
        final var attachment = new CountDownLatch(1);
        final var handler = new CompletionHandler<AsynchronousFileChannel, CountDownLatch>() {
            @Override
            public void completed(AsynchronousFileChannel result, CountDownLatch a) {
                try {
                    result.force(true);
                    result.close();
                } catch (final IOException ioe) {
                    log.error("failed to handle completion", ioe);
                }
                a.countDown();
            }

            @Override
            public void failed(Throwable exc, CountDownLatch a) {
                log.error("failed to write", exc);
                a.countDown();
            }
        };
        var path = Files.createTempFile(tempDir, null, null);
        // ------------------------------------------------------------------------------------ when
        var channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);
        service.write(channel, position, attachment, handler);
        attachment.await();
        // ------------------------------------------------------------------------------------ then
        // assert, path's size increased by 12, after the <position>
        Assertions.assertEquals(
                position + HelloWorld.BYTES,
                Files.size(path)
        );
    }
}
