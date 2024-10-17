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
import org.mockito.Mockito;

import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

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

    @SuppressWarnings({"unchecked"})
    @Test
    void __(@TempDir final Path dir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel, position, attachment, handler)>
        //         will write the <hello-world-bytes> to the <channel>,
        //         and will invoke <handler.completed(channel, attachment)>
        Mockito.doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousFileChannel.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2);
            final var handler = i.getArgument(3, CompletionHandler.class);
            log.debug("write({}, {}, {}, {})", channel, position, attachment, handler);
            final var buffer = helloWorldBuffer();
            channel.write(
                    buffer,                     // <src>
                    position,                   // <position>
                    attachment,                 // <attachment>
                    new CompletionHandler<>() { // <handler>
                        @Override // @formatter:off
                        public void completed(final Integer r, final Object a) {
                            log.debug("completed({}, {})", r, a);
                            assert r > 0; // why?
                            if (buffer.hasRemaining()) {
                                channel.write(
                                        buffer,       // <src>
                                        position + r, // <position>
                                        a,            // <attachment>
                                        this          // <handler>
                                );
                                return;
                            }
                            handler.completed(channel, a); // unchecked
                        }
                        @Override
                        public void failed(final Throwable t, final Object a) {
                            log.error("completed({}, {})", t, a, t);
                            handler.failed(t, a); // unchecked
                        } // @formatter:on
                    }
            );
            return channel;
        }).when(service).write(
                ArgumentMatchers.notNull(),              // <channel>
                ArgumentMatchers.longThat(v -> v >= 0L), // <position>
                ArgumentMatchers.any(),                  // <attachment>
                ArgumentMatchers.notNull()               // <handler>
        );
        // create, a real file
        final var path = Files.createTempFile(dir, null, null);
        // prepare a random <position>
        final var position = ThreadLocalRandom.current().nextLong(128);
        // ------------------------------------------------------------------------------------ when
        try (final var channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE)) {
            final var latch = new CountDownLatch(1);
            // -------------------------------------------------------------------------------- when
            service.write(
                    channel,                    // <channel>
                    position,                   // <position>
                    latch,                      // <attachment>
                    new CompletionHandler<>() { // <handler>
                        @Override // @formatter:off
                        public void completed(final AsynchronousFileChannel result,
                                              final CountDownLatch attachment) {
                            log.debug("completed({}, {})", result, attachment);
                            attachment.countDown();
                        }
                        @Override
                        public void failed(final Throwable exc, final CountDownLatch attachment) {
                            log.error("failed({}, {})", exc, attachment, exc);
                            attachment.countDown();
                        } // @formatter:on
                    }
            );
            latch.await();
            channel.force(false);
        }
        // ------------------------------------------------------------------------------------ then
        // assert, <path>'s <size> is equal to <position + 12>
        final var size = Files.size(path);
        log.debug("size: {}", size);
        Assertions.assertEquals(
                position + HelloWorld.BYTES,
                size
        );
    }
}
