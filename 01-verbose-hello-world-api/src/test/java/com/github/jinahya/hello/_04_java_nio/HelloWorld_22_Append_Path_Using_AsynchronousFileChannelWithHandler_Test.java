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
import java.util.concurrent.atomic.LongAccumulator;

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
        // stub, <service.write(channel, position, attachment, handler)>
        //         will write 12 bytes to the <channel>
        //         and will invoke <handler.completed(channel, attachment)>
        BDDMockito.willAnswer(i -> {
                    final var channel = i.getArgument(0, AsynchronousFileChannel.class);
                    final var position = i.getArgument(1, Long.class);
                    final var attachment = i.getArgument(2);
                    @SuppressWarnings({"unchecked"})
                    final var handler = (CompletionHandler<AsynchronousFileChannel, Object>)
                            i.getArgument(3, CompletionHandler.class);
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    final var accumulator = new LongAccumulator(Long::sum, position);
                    channel.write( // @formatter:off
                            buffer,                     // <src>
                            accumulator.get(),          // <position>
                            attachment,                 // <attachment>
                            new CompletionHandler<>() { // <handler>
                                @Override
                                public void completed(final Integer r, final Object a) {
                                    accumulator.accumulate(r);
                                    if (buffer.hasRemaining()) {
                                        channel.write(
                                                buffer,            // <src>
                                                accumulator.get(), // <position>
                                                a,                 // <attachment>
                                                this               // <handler>
                                        );
                                        return;
                                    }
                                    assert accumulator.get() == position + HelloWorld.BYTES;
                                    handler.completed(channel, a);
                                }
                                @Override
                                public void failed(final Throwable t, final Object a) {
                                    handler.failed(t, a);
                                }
                            }
                    ); // @formatter:on
                    return channel;
                })
                .given(service)
                .write(ArgumentMatchers.notNull(),              // <channel>
                       ArgumentMatchers.longThat(v -> v >= 0L), // <position>
                       ArgumentMatchers.any(),                  // <attachment>
                       ArgumentMatchers.notNull()               // <handler>
                );
        final var position = ThreadLocalRandom.current().nextLong(1024L);
        final var latch = new CountDownLatch(1);
        final var handler = new CompletionHandler<AsynchronousFileChannel, CountDownLatch>() {
            @Override public void completed(final AsynchronousFileChannel r, // @formatter:off
                                            final CountDownLatch a) {
                a.countDown();
            }
            @Override public void failed(final Throwable t, final CountDownLatch a) {
                log.error("failed to write", t);
                a.countDown();
            }
        }; // @formatter:on
        final var path = Files.createTempFile(tempDir, null, null);
        // ------------------------------------------------------------------------------------ when
        final var channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);
        service.write(channel, position, latch, handler);
        try {
            latch.await();
        } catch (final InterruptedException ie) {
            channel.force(true);
            channel.close();
            throw ie;
        }
        // ------------------------------------------------------------------------------------ then
        // assert, <path>'s <size> increased by <12>, after the <position>
        Assertions.assertEquals(
                position + HelloWorld.BYTES,
                Files.size(path)
        );
    }
}
